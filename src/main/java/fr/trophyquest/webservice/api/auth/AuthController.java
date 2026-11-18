package fr.trophyquest.webservice.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CognitoUserInfoService cognitoUserInfoService;
    private final AuthUserService authUserService;

    public AuthController(
            AuthUserService authUserService,
            CognitoUserInfoService cognitoUserInfoService
    ) {
        this.authUserService = authUserService;
        this.cognitoUserInfoService = cognitoUserInfoService;
    }

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal Jwt jwt) {
        CognitoUserInfo userInfo = cognitoUserInfoService.fetchUserInfo(jwt.getTokenValue());
        return this.authUserService.fetchCurrentUser(userInfo);
    }

    @PutMapping("/me")
    public AuthUserResponse updateDisplayName(@AuthenticationPrincipal Jwt jwt,
                                              @RequestBody UpdateDisplayNameRequest request) {
        String displayName = request.displayName() == null ? "" : request.displayName().strip();
        if (displayName.length() < 3 || displayName.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a nickname of 3 to 30 characters.");
        }

        String accessToken = jwt.getTokenValue();
        CognitoUserInfo userInfo = cognitoUserInfoService.fetchUserInfo(accessToken);
        cognitoUserInfoService.updateDisplayName(accessToken, displayName);
        return authUserService.updateDisplayName(userInfo, displayName);
    }

    public record UpdateDisplayNameRequest(String displayName) {
    }
}
