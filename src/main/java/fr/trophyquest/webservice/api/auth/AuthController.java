package fr.trophyquest.webservice.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static fr.trophyquest.webservice.config.SecurityConfig.ADMIN_AUTHORITY;

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
    public AuthUserResponse me(JwtAuthenticationToken authentication) {
        CognitoUserInfo userInfo = cognitoUserInfoService.fetchUserInfo(authentication.getToken().getTokenValue());
        return this.authUserService.fetchCurrentUser(userInfo, isAdmin(authentication));
    }

    @PutMapping("/me")
    public AuthUserResponse updateDisplayName(JwtAuthenticationToken authentication,
                                              @RequestBody UpdateDisplayNameRequest request) {
        String displayName = request.displayName() == null ? "" : request.displayName().strip();
        if (displayName.length() < 3 || displayName.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a nickname of 3 to 30 characters.");
        }

        String accessToken = authentication.getToken().getTokenValue();
        CognitoUserInfo userInfo = cognitoUserInfoService.fetchUserInfo(accessToken);
        cognitoUserInfoService.updateDisplayName(accessToken, displayName);
        return authUserService.updateDisplayName(userInfo, displayName, isAdmin(authentication));
    }

    private boolean isAdmin(JwtAuthenticationToken authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
    }

    public record UpdateDisplayNameRequest(String displayName) {
    }
}
