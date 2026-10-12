package fr.trophyquest.backend.api.auth;

import fr.trophyquest.backend.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthUserMapper {

    public AuthUserMapper() {
    }

    public AuthUserResponse toDTO(User entity) {
        return AuthUserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getDisplayName())
                .build();
    }
}
