package fr.trophyquest.webservice.api.game.details.service;

import fr.trophyquest.webservice.api.game.details.dto.GameTrophyItem;
import fr.trophyquest.webservice.api.game.details.mappers.GameTrophyItemMapper;
import fr.trophyquest.webservice.domain.entity.EarnedTrophy;
import fr.trophyquest.webservice.domain.repository.EarnedTrophyRepository;
import fr.trophyquest.webservice.domain.repository.TrophyRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameDetailsTrophyService {

    private final EarnedTrophyRepository earnedTrophyRepository;
    private final TrophyRepository trophyRepository;
    private final GameTrophyItemMapper gameTrophyItemMapper;

    public GameDetailsTrophyService(EarnedTrophyRepository earnedTrophyRepository,
                                    TrophyRepository trophyRepository,
                                    GameTrophyItemMapper gameTrophyItemMapper) {
        this.earnedTrophyRepository = earnedTrophyRepository;
        this.trophyRepository = trophyRepository;
        this.gameTrophyItemMapper = gameTrophyItemMapper;
    }

    /**
     * Fetch all trophies linked to a suite, with earned date if the player has earned them.
     *
     * @param suiteId  ID of the suite
     * @param playerId ID of the player. If not provided, all trophies will be returned with no earned date
     * @return A list of trophies.
     */
    public List<GameTrophyItem> fetchTrophiesForSuite(UUID suiteId, Optional<UUID> playerId) {
        List<GameTrophyItem> trophies = this.trophyRepository.findAllBySuiteId(suiteId)
                .stream()
                .map(this.gameTrophyItemMapper::fromTrophy)
                .toList();

        if (playerId.isEmpty()) {
            return trophies;
        }

        Map<UUID, Instant> earnedAtByTrophyId = this.earnedTrophyRepository
                .findAllByTrophySuiteIdAndPlayerId(suiteId, playerId.orElse(null))
                .stream()
                .collect(Collectors.toMap(
                        earnedTrophy -> earnedTrophy.getTrophy().getId(),
                        EarnedTrophy::getEarnedAt
                ));

        return trophies.stream()
                .map(trophy -> new GameTrophyItem(
                        trophy.trophyId(),
                        trophy.rank(),
                        trophy.title(),
                        trophy.description(),
                        trophy.color(),
                        trophy.iconUrl(),
                        trophy.isHidden(),
                        trophy.groupName(),
                        earnedAtByTrophyId.get(trophy.trophyId())
                ))
                .toList();
    }

}
