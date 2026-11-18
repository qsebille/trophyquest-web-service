package fr.trophyquest.webservice.api.game.details.service;

import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.api.shared.mapper.PlayerItemMapper;
import fr.trophyquest.webservice.domain.repository.PlayedGameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameDetailsPlayerService {

    private final PlayedGameRepository playedGameRepository;

    private final PlayerItemMapper playerItemMapper;

    public GameDetailsPlayerService(PlayedGameRepository playedGameRepository,
                                    PlayerItemMapper playerItemMapper) {
        this.playedGameRepository = playedGameRepository;
        this.playerItemMapper = playerItemMapper;
    }

    public List<PlayerItem> fetchPlayersByGameId(long gameId, int size) {
        return playedGameRepository.findRecentPlayersByGameId(gameId, size).stream()
                .map(playedGame -> playerItemMapper.fromPlayer(playedGame.getPlayer(), playedGame.getLastPlayedAt()))
                .toList();
    }
}
