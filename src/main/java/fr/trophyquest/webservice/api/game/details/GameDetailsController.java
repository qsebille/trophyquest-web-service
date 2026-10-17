package fr.trophyquest.webservice.api.game.details;


import fr.trophyquest.webservice.api.game.details.dto.GameDetailsResponse;
import fr.trophyquest.webservice.api.game.details.dto.GamePlayerItem;
import fr.trophyquest.webservice.api.game.details.dto.GameSuiteItem;
import fr.trophyquest.webservice.api.game.details.dto.GameTrophyItem;
import fr.trophyquest.webservice.api.game.details.services.GameDetailsBackgroundService;
import fr.trophyquest.webservice.api.game.details.services.GameDetailsPlayerService;
import fr.trophyquest.webservice.api.game.details.services.GameDetailsService;
import fr.trophyquest.webservice.api.game.details.services.GameDetailsSuiteService;
import fr.trophyquest.webservice.api.game.details.services.GameDetailsTrophyService;
import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/game-details")
public class GameDetailsController {

    private final GameDetailsService gameDetailsService;
    private final GameDetailsPlayerService gameDetailsPlayerService;
    private final GameDetailsSuiteService gameDetailsSuiteService;
    private final GameDetailsTrophyService gameDetailsTrophyService;
    private final GameDetailsBackgroundService gameDetailsBackgroundService;


    public GameDetailsController(GameDetailsService gameDetailsService,
                                 GameDetailsPlayerService gameDetailsPlayerService,
                                 GameDetailsSuiteService gameDetailsSuiteService,
                                 GameDetailsTrophyService gameDetailsTrophyService,
                                 GameDetailsBackgroundService gameDetailsBackgroundService) {
        this.gameDetailsService = gameDetailsService;
        this.gameDetailsPlayerService = gameDetailsPlayerService;
        this.gameDetailsSuiteService = gameDetailsSuiteService;
        this.gameDetailsTrophyService = gameDetailsTrophyService;
        this.gameDetailsBackgroundService = gameDetailsBackgroundService;
    }

    @GetMapping("/{gameId}")
    public GameDetailsResponse fetchDetailsByGameId(@PathVariable long gameId) {
        return gameDetailsService.fetchDetailsByGameId(gameId);
    }

    @GetMapping("/{gameId}/suites")
    public List<GameSuiteItem> fetchSuitesByGameId(@PathVariable long gameId) {
        return gameDetailsSuiteService.fetchSuitesByGameId(gameId);
    }

    @GetMapping("/{gameId}/players")
    public Pagination<GamePlayerItem> searchPlayersByGameId(@PathVariable long gameId,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        return gameDetailsPlayerService.searchPlayersByGameId(gameId, page, size);
    }

    @GetMapping("/suite/{suiteId}/trophies")
    public List<GameTrophyItem> fetchTrophiesBySuiteId(@PathVariable UUID suiteId,
                                                       @RequestParam(name = "playerId", required = false) Optional<UUID> playerId) {
        return gameDetailsTrophyService.fetchTrophiesForSuite(suiteId, playerId);
    }

    @GetMapping("/{gameId}/background")
    public BackgroundImageResponse fetchBackgroundByGameId(@PathVariable long gameId) {
        return gameDetailsBackgroundService.fetchBackgroundByGameId(gameId);
    }

}
