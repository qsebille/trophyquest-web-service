package fr.trophyquest.webservice.api.suite.dto;

import java.util.List;
import java.util.UUID;

public record SuiteDetailsResponse(
        UUID suiteId,
        Long gameId,
        String title,
        List<String> platforms,
        String imageUrl,
        int nbBronzeTrophies,
        int nbSilverTrophies,
        int nbGoldTrophies,
        int nbPlatinumTrophies
) {
}
