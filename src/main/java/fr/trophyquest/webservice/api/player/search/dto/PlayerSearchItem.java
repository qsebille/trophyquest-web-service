package fr.trophyquest.webservice.api.player.search.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlayerSearchItem(
        UUID id,
        String pseudo,
        String avatarUrl,
        int nbPlayedSuites,
        int nbEarnedPlatinumTrophies,
        int nbEarnedGoldTrophies,
        int nbEarnedSilverTrophies,
        int nbEarnedBronzeTrophies
) {
}
