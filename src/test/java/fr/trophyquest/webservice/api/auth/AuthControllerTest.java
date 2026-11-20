package fr.trophyquest.webservice.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.trophyquest.webservice.domain.entity.User;
import fr.trophyquest.webservice.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fr.trophyquest.webservice.config.SecurityConfig.ADMIN_AUTHORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthControllerTest {
    private static final String USER_INFO_URL = "https://auth.example.test/oauth2/userInfo";
    private static final String COGNITO_API_URL = "https://cognito-idp.example.test/";
    private static final Jwt JWT = Jwt.withTokenValue("user-access-token")
            .header("alg", "RS256")
            .subject("cognito-user")
            .build();
    private static final JwtAuthenticationToken USER = new JwtAuthenticationToken(JWT, List.of());
    private static final JwtAuthenticationToken ADMIN = new JwtAuthenticationToken(
            JWT, List.of(new SimpleGrantedAuthority(ADMIN_AUTHORITY)));

    private final RestClient.Builder client = RestClient.builder();
    private final MockRestServiceServer cognito = MockRestServiceServer.bindTo(client).build();
    private final UserRepository users = mock(UserRepository.class);
    private final AuthController controller = new AuthController(
            new AuthUserService(users, new AuthUserMapper()),
            new CognitoUserInfoService(client, new ObjectMapper(), USER_INFO_URL,
                    URI.create(COGNITO_API_URL + "user-pool"))
    );

    @Test
    void should_update_the_authenticated_users_preferred_username_before_saving_it() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setDisplayName("Camille");
        when(users.findByCognitoSub("cognito-user")).thenReturn(Optional.of(user));
        when(users.save(user)).thenReturn(user);
        expectUserInfo("Camille");
        expectNicknameUpdate().andRespond(withSuccess());

        AuthUserResponse response = controller.updateDisplayName(ADMIN,
                new AuthController.UpdateDisplayNameRequest("  Étoile 星  "));

        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.displayName()).isEqualTo("Étoile 星");
        assertThat(response.isAdmin()).isTrue();
        assertThat(user.getDisplayName()).isEqualTo("Étoile 星");
        verify(users).save(user);
        cognito.verify();
    }

    @Test
    void should_return_the_admin_flag_from_the_authenticated_authorities() {
        when(users.findByCognitoSub("cognito-user")).thenReturn(Optional.of(new User()));
        expectUserInfo("Camille");
        expectUserInfo("Camille");

        assertThat(controller.me(USER).isAdmin()).isFalse();
        assertThat(controller.me(ADMIN).isAdmin()).isTrue();
        cognito.verify();
    }

    @Test
    void should_keep_the_local_profile_when_cognito_rejects_the_update() {
        expectUserInfo("Camille");
        expectNicknameUpdate().andRespond(withBadRequest().header("x-amzn-errortype", "AliasExistsException"));

        assertThatThrownBy(() -> controller.updateDisplayName(USER,
                new AuthController.UpdateDisplayNameRequest("Étoile 星")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode().value()).isEqualTo(409));

        verifyNoInteractions(users);
        cognito.verify();
    }

    @Test
    void should_read_the_current_nickname_from_cognito_when_the_local_copy_is_older() {
        User user = new User();
        user.setDisplayName("Camille");
        when(users.findByCognitoSub("cognito-user")).thenReturn(Optional.of(user));
        expectUserInfo("Nouveau pseudo");

        assertThat(controller.me(USER).displayName()).isEqualTo("Nouveau pseudo");
        cognito.verify();
    }

    private void expectUserInfo(String preferredUsername) {
        cognito.expect(requestTo(USER_INFO_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer user-access-token"))
                .andRespond(withSuccess("""
                        {"sub":"cognito-user","email":"camille@example.test","preferred_username":"%s"}
                        """.formatted(preferredUsername), MediaType.APPLICATION_JSON));
    }

    private ResponseActions expectNicknameUpdate() {
        return cognito.expect(requestTo(COGNITO_API_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Amz-Target", "AWSCognitoIdentityProviderService.UpdateUserAttributes"))
                .andExpect(content().contentType("application/x-amz-json-1.0"))
                .andExpect(content().json("""
                        {
                          "AccessToken": "user-access-token",
                          "UserAttributes": [{"Name": "preferred_username", "Value": "Étoile 星"}]
                        }
                        """));
    }
}
