package fr.trophyquest.webservice.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class CognitoUserInfoService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String userInfoUri;
    private final URI cognitoApiUri;

    public CognitoUserInfoService(RestClient.Builder restClientBuilder, ObjectMapper objectMapper,
                                  @Value("${cognito.user-info-uri}") String userInfoUri,
                                  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") URI issuerUri) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.userInfoUri = userInfoUri;
        this.cognitoApiUri = issuerUri.resolve("/");
    }

    public CognitoUserInfo fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(userInfoUri)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(CognitoUserInfo.class);
    }

    public void updateDisplayName(String accessToken, String displayName) {
        var payload = Map.of(
                "AccessToken", accessToken,
                "UserAttributes", List.of(Map.of("Name", "preferred_username", "Value", displayName))
        );
        try {
            restClient.post()
                    .uri(cognitoApiUri)
                    .contentType(MediaType.parseMediaType("application/x-amz-json-1.0"))
                    .header("X-Amz-Target", "AWSCognitoIdentityProviderService.UpdateUserAttributes")
                    .body(output -> objectMapper.writeValue(output, payload))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            String errorType = exception.getResponseHeaders() == null ? null
                    : exception.getResponseHeaders().getFirst("x-amzn-errortype");
            if ("AliasExistsException".equals(errorType)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This nickname is already in use.");
            }
            if ("InvalidParameterException".equals(errorType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cognito rejected this nickname.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cognito could not update the nickname.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cognito could not update the nickname.");
        }
    }
}
