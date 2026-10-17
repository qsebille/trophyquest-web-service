package fr.trophyquest.webservice.api.home.dto;

import lombok.Builder;

@Builder
public record HomeGameItem(
        long gameId,
        String title,
        String coverUrl,
        int nbRecentPlayers
) {
}
