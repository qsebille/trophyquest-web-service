package fr.trophyquest.webservice.api.player.profile.service;

import fr.trophyquest.webservice.api.player.profile.PlayerNotFoundException;
import fr.trophyquest.webservice.api.player.profile.dto.PlayerSummaryResponse;
import fr.trophyquest.webservice.api.player.profile.mapper.PlayerSummaryResponseMapper;
import fr.trophyquest.webservice.domain.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PlayerProfileService {
    private final PlayerRepository playerRepository;
    private final PlayerSummaryResponseMapper mapper;

    public PlayerProfileService(PlayerRepository playerRepository, PlayerSummaryResponseMapper mapper) {
        this.playerRepository = playerRepository;
        this.mapper = mapper;
    }

    /**
     * Returns the details of a player given its ID.
     *
     * @param playerId ID of the player.
     * @return The summary of a player.
     */
    public PlayerSummaryResponse fetchSummaryByPlayerId(UUID playerId) {
        return playerRepository.findById(playerId).map(mapper::fromPlayer)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    /**
     * Throw an exception if player does not exist in database.
     *
     * @param playerId ID of the player to test.
     */
    public void requirePlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
    }
}
