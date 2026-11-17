package fr.trophyquest.webservice.api.player.profile.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record PlayerSummaryResponse(
        UUID playerId,
        String pseudo,
        String avatarUrl,
        int nbPlayedSuites,
        int nbEarnedBronzeTrophies,
        int nbEarnedSilverTrophies,
        int nbEarnedGoldTrophies,
        int nbEarnedPlatinumTrophies
) {
}
