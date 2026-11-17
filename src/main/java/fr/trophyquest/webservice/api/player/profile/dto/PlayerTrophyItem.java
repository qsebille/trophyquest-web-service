package fr.trophyquest.webservice.api.player.profile.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PlayerTrophyItem(
        UUID trophyId,
        UUID suiteId,
        Long gameId,
        String suiteTitle,
        int rank,
        String title,
        String description,
        String color,
        String iconUrl,
        Instant earnedAt
) {
}
