package fr.trophyquest.webservice.api.game.search.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GameSearchItem(
        long gameId,
        String title,
        String coverUrl,
        List<String> platforms,
        int nbPlayers,
        int nbPlatinumTrophies,
        int nbGoldTrophies,
        int nbSilverTrophies,
        int nbBronzeTrophies
) {
}
