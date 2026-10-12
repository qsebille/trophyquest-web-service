package fr.trophyquest.backend.api.auth;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthUserResponse(
        UUID id,
        String email,
        String displayName
) {
}
