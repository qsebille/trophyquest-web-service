package fr.trophyquest.webservice.api.player.profile.dto;

import lombok.Builder;
import java.util.UUID;
import java.time.Instant;
import java.util.List;

@Builder
public record PlayerSuiteItem(
        UUID suiteId,
        Long gameId,
        String title,
        List<String> platforms,
        String imageUrl,
        int nbBronzeTrophies,
        int nbSilverTrophies,
        int nbGoldTrophies,
        int nbPlatinumTrophies,
        int nbEarnedBronzeTrophies,
        int nbEarnedSilverTrophies,
        int nbEarnedGoldTrophies,
        int nbEarnedPlatinumTrophies,
        Instant lastPlayedAt
) {
}
