package fr.trophyquest.backend.api.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}