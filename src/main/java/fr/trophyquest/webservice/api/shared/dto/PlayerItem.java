package fr.trophyquest.webservice.api.shared.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PlayerItem(
        UUID playerId,
        String pseudo,
        String avatarUrl,
        Instant lastPlayedAt
) {
}
