package fr.trophyquest.webservice.api.suite;

import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.api.shared.dto.TrophyItem;
import fr.trophyquest.webservice.api.shared.mapper.PlayerItemMapper;
import fr.trophyquest.webservice.api.shared.service.GameBackgroundService;
import fr.trophyquest.webservice.api.suite.dto.SuiteDetailsResponse;
import fr.trophyquest.webservice.domain.entity.PlayedSuite;
import fr.trophyquest.webservice.domain.entity.Suite;
import fr.trophyquest.webservice.domain.repository.PlayedSuiteRepository;
import fr.trophyquest.webservice.domain.repository.SuiteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SuiteService {
    private final SuiteRepository suiteRepository;
    private final PlayedSuiteRepository playedSuiteRepository;
    private final PlayerItemMapper playerMapper;
    private final SuiteTrophyService trophyService;
    private final GameBackgroundService gameBackgroundService;

    public SuiteService(SuiteRepository suiteRepository,
                        PlayedSuiteRepository playedSuiteRepository,
                        PlayerItemMapper playerMapper,
                        SuiteTrophyService trophyService,
                        GameBackgroundService gameBackgroundService) {
        this.suiteRepository = suiteRepository;
        this.playedSuiteRepository = playedSuiteRepository;
        this.playerMapper = playerMapper;
        this.trophyService = trophyService;
        this.gameBackgroundService = gameBackgroundService;
    }

    public SuiteDetailsResponse fetchDetailsBySuiteId(UUID suiteId) {
        Suite suite = fetchSuite(suiteId);
        return new SuiteDetailsResponse(suite.getId(), suite.getGameId(), suite.getName(), suite.getPlatforms(),
                suite.getImageUrl(), suite.getNbBronzeTrophies(), suite.getNbSilverTrophies(),
                suite.getNbGoldTrophies(), suite.getNbPlatinumTrophies());
    }

    @Transactional
    public SuiteDetailsResponse updateTitle(UUID suiteId, String title) {
        if (suiteRepository.upsertTitleOverride(suiteId, title) == 0) {
            throw new SuiteNotFoundException(suiteId);
        }
        return fetchDetailsBySuiteId(suiteId);
    }

    public BackgroundImageResponse fetchBackgroundBySuiteId(UUID suiteId) {
        Long gameId = fetchSuite(suiteId).getGameId();
        return gameId == null ? new BackgroundImageResponse(null) : gameBackgroundService.fetchBackgroundByGameId(gameId);
    }

    public Pagination<PlayerItem> searchPlayersBySuiteId(UUID suiteId, int page, int size) {
        requireSuite(suiteId);
        Page<PlayedSuite> result = playedSuiteRepository.findBySuiteId(suiteId, PageRequest.of(page, size));
        return Pagination.<PlayerItem>builder()
                .content(result.getContent().stream()
                        .map(playedSuite -> playerMapper.fromPlayer(playedSuite.getPlayer(), playedSuite.getLastPlayedAt()))
                        .toList())
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }

    public List<TrophyItem> fetchTrophiesBySuiteId(UUID suiteId, Optional<UUID> playerId) {
        requireSuite(suiteId);
        return trophyService.fetchTrophiesForSuite(suiteId, playerId);
    }

    private void requireSuite(UUID suiteId) {
        if (!suiteRepository.existsById(suiteId)) {
            throw new SuiteNotFoundException(suiteId);
        }
    }

    private Suite fetchSuite(UUID suiteId) {
        return suiteRepository.findById(suiteId)
                .orElseThrow(() -> new SuiteNotFoundException(suiteId));
    }
}
