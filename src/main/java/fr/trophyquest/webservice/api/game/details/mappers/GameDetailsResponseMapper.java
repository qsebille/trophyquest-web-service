package fr.trophyquest.webservice.api.game.details.mappers;

import fr.trophyquest.webservice.api.game.details.dto.GameDetailsResponse;
import fr.trophyquest.webservice.domain.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameDetailsResponseMapper {
    public GameDetailsResponseMapper() {
    }

    public GameDetailsResponse fromGame(Game game) {
        return GameDetailsResponse.builder()
                .gameId(game.getId())
                .title(game.getName())
                .summary(game.getSummary())
                .genres(game.getGenres())
                .themes(game.getThemes())
                .releaseDate(game.getReleaseDate())
                .coverUrl(game.getCoverUrl())
                .screenshotUrls(game.getScreenshotsUrl())
                .build();
    }
}
