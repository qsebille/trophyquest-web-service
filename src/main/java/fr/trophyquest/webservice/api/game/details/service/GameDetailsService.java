package fr.trophyquest.webservice.api.game.details.service;

import fr.trophyquest.webservice.api.game.details.dto.GameDetailsResponse;
import fr.trophyquest.webservice.api.game.details.mappers.GameDetailsResponseMapper;
import fr.trophyquest.webservice.api.shared.error.GameNotFoundException;
import fr.trophyquest.webservice.domain.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameDetailsService {

    private final GameRepository gameRepository;

    private final GameDetailsResponseMapper gameDetailsResponseMapper;

    public GameDetailsService(GameRepository gameRepository,
                              GameDetailsResponseMapper gameDetailsResponseMapper) {
        this.gameRepository = gameRepository;
        this.gameDetailsResponseMapper = gameDetailsResponseMapper;
    }

    /**
     * Fetch game details by game id.
     *
     * @param gameId Game ID
     * @return Game details DTO
     */
    public GameDetailsResponse fetchDetailsByGameId(long gameId) {
        return this.gameRepository.findFirstById(gameId)
                .map(this.gameDetailsResponseMapper::fromGame)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

}
