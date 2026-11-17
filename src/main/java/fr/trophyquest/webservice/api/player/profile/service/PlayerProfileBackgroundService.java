package fr.trophyquest.webservice.api.player.profile.service;

import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.domain.repository.PlayedGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PlayerProfileBackgroundService {
    private final PlayedGameRepository playedGameRepository;
    private final PlayerProfileService playerProfileService;

    public PlayerProfileBackgroundService(PlayedGameRepository playedGameRepository,
                                          PlayerProfileService playerProfileService) {
        this.playedGameRepository = playedGameRepository;
        this.playerProfileService = playerProfileService;
    }

    public BackgroundImageResponse fetchBackgroundByPlayerId(UUID playerId) {
        playerProfileService.requirePlayer(playerId);
        String url = playedGameRepository.getLastPlayedBackgroundImage(playerId);
        return BackgroundImageResponse.builder().url(url == null ? "" : url).build();
    }
}
