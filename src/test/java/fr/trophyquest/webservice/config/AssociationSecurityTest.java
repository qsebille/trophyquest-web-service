package fr.trophyquest.webservice.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fr.trophyquest.webservice.api.association.AssociationController;
import fr.trophyquest.webservice.api.association.AssociationSearchService;
import fr.trophyquest.webservice.api.association.AssociationValidationService;
import fr.trophyquest.webservice.api.auth.AuthController;
import fr.trophyquest.webservice.api.auth.AuthUserResponse;
import fr.trophyquest.webservice.api.auth.AuthUserService;
import fr.trophyquest.webservice.api.auth.CognitoUserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AssociationController.class, AuthController.class})
@Import({SecurityConfig.class, CorsProperties.class, AssociationSecurityTest.LocalJwtConfig.class})
class AssociationSecurityTest {

    private static final String ISSUER = "https://cognito.example.test/trophyquest";
    private static final String CLIENT_ID = "trophyquest";
    private static final String PROFILE_SCOPE = "aws.cognito.signin.user.admin";
    private static final String ASSOCIATION = "/api/association";
    private static final String PROFILE = "/api/auth/me";

    @Autowired
    MockMvc mvc;

    @Autowired
    RSAKey signingKey;

    @MockitoBean
    AssociationSearchService associationSearchService;

    @MockitoBean
    AssociationValidationService associationValidationService;

    @MockitoBean
    AuthUserService authUserService;

    @MockitoBean
    CognitoUserInfoService cognitoUserInfoService;

    @Test
    void should_require_the_admin_group_for_every_association_endpoint() throws Exception {
        String userToken = token("access", CLIENT_ID, List.of("moderator"), PROFILE_SCOPE);
        String adminToken = token("access", CLIENT_ID, List.of("admin"), "openid");
        String body = """
                {"suiteId":"a0000003-0000-0000-0000-000000000000","gameId":1003}
                """;
        for (MockHttpServletRequestBuilder request : List.of(
                get(ASSOCIATION).param("page", "0"),
                post(ASSOCIATION + "/validate").contentType(MediaType.APPLICATION_JSON).content(body),
                post(ASSOCIATION + "/reject").contentType(MediaType.APPLICATION_JSON).content(body)
        )) {
            mvc.perform(request).andExpect(status().isUnauthorized());
            mvc.perform(request.header(HttpHeaders.AUTHORIZATION, userToken)).andExpect(status().isForbidden());
        }

        mvc.perform(get(ASSOCIATION).param("page", "0").header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, org.hamcrest.Matchers.containsString("no-store")));
        for (String action : List.of("/validate", "/reject")) {
            mvc.perform(post(ASSOCIATION + action).contentType(MediaType.APPLICATION_JSON).content(body)
                            .header(HttpHeaders.AUTHORIZATION, adminToken))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void should_reject_identity_tokens_and_tokens_from_another_client() throws Exception {
        for (String bearer : List.of(token("id", CLIENT_ID, List.of("admin"), "openid"),
                token("access", "other-client", List.of("admin"), "openid"))) {
            mvc.perform(get(ASSOCIATION).param("page", "0").header(HttpHeaders.AUTHORIZATION, bearer))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void should_keep_the_profile_scope_independent_of_the_admin_role() throws Exception {
        when(authUserService.updateDisplayName(any(), eq("Camille"), eq(false)))
                .thenReturn(AuthUserResponse.builder().displayName("Camille").build());
        String body = """
                {"displayName":"Camille"}
                """;

        mvc.perform(put(PROFILE).contentType(MediaType.APPLICATION_JSON).content(body)
                        .header(HttpHeaders.AUTHORIZATION, token("access", CLIENT_ID, null, PROFILE_SCOPE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdmin").value(false));
        mvc.perform(put(PROFILE).contentType(MediaType.APPLICATION_JSON).content(body)
                        .header(HttpHeaders.AUTHORIZATION, token("access", CLIENT_ID, List.of("admin"), "openid")))
                .andExpect(status().isForbidden());
    }

    private String token(String use, String clientId, List<String> groups, String scope) throws JOSEException {
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject("user")
                .expirationTime(Date.from(Instant.now().plusSeconds(300)))
                .claim("token_use", use)
                .claim("client_id", clientId)
                .claim("cognito:groups", groups)
                .claim("scope", scope)
                .build());
        jwt.sign(new RSASSASigner(signingKey));
        return "Bearer " + jwt.serialize();
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class LocalJwtConfig {
        @Bean
        RSAKey signingKey() throws JOSEException {
            return new RSAKeyGenerator(2048).generate();
        }

        @Bean
        @Primary
        JwtDecoder localJwtDecoder(RSAKey signingKey) throws JOSEException {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(signingKey.toRSAPublicKey()).build();
            decoder.setJwtValidator(SecurityConfig.accessTokenValidator(ISSUER, CLIENT_ID));
            return decoder;
        }
    }
}
