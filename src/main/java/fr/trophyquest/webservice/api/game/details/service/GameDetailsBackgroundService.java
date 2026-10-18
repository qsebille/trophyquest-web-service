package fr.trophyquest.webservice.api.game.details.service;

import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.error.GameNotFoundException;
import fr.trophyquest.webservice.domain.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameDetailsBackgroundService {

    private final GameRepository gameRepository;

    public GameDetailsBackgroundService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Fetch the background of a game, given its ID.
     *
     * @param gameId ID of the game.
     * @return The URL of the background image in a {@link BackgroundImageResponse} object.
     */
    public BackgroundImageResponse fetchBackgroundByGameId(long gameId) {
        return this.gameRepository.findFirstById(gameId)
                .map(game -> BackgroundImageResponse.builder()
                        .url(game.getBackgroundUrl())
                        .build())
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

}
