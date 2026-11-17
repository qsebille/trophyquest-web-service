package fr.trophyquest.webservice.api.game.details.service;

import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.api.shared.mapper.PlayerItemMapper;
import fr.trophyquest.webservice.domain.entity.PlayedGame;
import fr.trophyquest.webservice.domain.repository.PlayedGameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameDetailsPlayerService {

    private final PlayedGameRepository playedGameRepository;

    private final PlayerItemMapper playerItemMapper;

    public GameDetailsPlayerService(PlayedGameRepository playedGameRepository,
                                    PlayerItemMapper playerItemMapper) {
        this.playedGameRepository = playedGameRepository;
        this.playerItemMapper = playerItemMapper;
    }

    /**
     * Fetch players that played a game.
     *
     * @param gameId ID of the game
     * @param page   Page number for pagination
     * @param size   Page size for pagination
     * @return A paginated list of players that played the game
     */
    public Pagination<PlayerItem> searchPlayersByGameId(long gameId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "lastPlayedAt");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<PlayedGame> playedGames = this.playedGameRepository.findByGameId(gameId, pageRequest);
        List<PlayerItem> content = playedGames.getContent().stream()
                .map(playedGame -> playerItemMapper.fromPlayer(playedGame.getPlayer(), playedGame.getLastPlayedAt()))
                .collect(Collectors.toList());
        return Pagination.<PlayerItem>builder()
                .content(content)
                .total(playedGames.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }

}
