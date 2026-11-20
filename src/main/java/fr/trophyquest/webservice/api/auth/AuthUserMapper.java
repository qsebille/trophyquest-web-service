package fr.trophyquest.webservice.api.auth;

import fr.trophyquest.webservice.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthUserMapper {

    public AuthUserMapper() {
    }

    public AuthUserResponse toDTO(User entity, String displayName, boolean isAdmin) {
        return AuthUserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(displayName)
                .isAdmin(isAdmin)
                .build();
    }
}
