package fr.trophyquest.webservice.api.game.details.services;

import fr.trophyquest.webservice.api.game.details.dto.GamePlayerItem;
import fr.trophyquest.webservice.api.game.details.mappers.GamePlayerItemMapper;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.PlayedGame;
import fr.trophyquest.webservice.domain.repository.PlayedGameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameDetailsPlayerService {

    private final PlayedGameRepository playedGameRepository;

    private final GamePlayerItemMapper gamePlayerItemMapper;

    public GameDetailsPlayerService(PlayedGameRepository playedGameRepository,
                                    GamePlayerItemMapper gamePlayerItemMapper) {
        this.playedGameRepository = playedGameRepository;
        this.gamePlayerItemMapper = gamePlayerItemMapper;
    }

    /**
     * Fetch players that played a game.
     *
     * @param gameId ID of the game
     * @param page   Page number for pagination
     * @param size   Page size for pagination
     * @return A paginated list of players that played the game
     */
    public Pagination<GamePlayerItem> searchPlayersByGameId(long gameId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PlayedGame> playedGames = this.playedGameRepository.findByGameId(gameId, pageRequest);
        List<GamePlayerItem> content = playedGames.getContent().stream()
                .map(this.gamePlayerItemMapper::fromPlayedGame)
                .collect(Collectors.toList());
        return Pagination.<GamePlayerItem>builder()
                .content(content)
                .total(playedGames.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }

}
