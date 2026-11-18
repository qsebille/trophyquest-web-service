package fr.trophyquest.webservice.api.game.details;


import fr.trophyquest.webservice.api.game.details.dto.GameDetailsResponse;
import fr.trophyquest.webservice.api.game.details.dto.GameSuiteItem;
import fr.trophyquest.webservice.api.game.details.service.GameDetailsPlayerService;
import fr.trophyquest.webservice.api.game.details.service.GameDetailsService;
import fr.trophyquest.webservice.api.game.details.service.GameDetailsSuiteService;
import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.api.shared.service.GameBackgroundService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/game/details")
public class GameDetailsController {

    private final GameDetailsService gameDetailsService;
    private final GameDetailsPlayerService gameDetailsPlayerService;
    private final GameDetailsSuiteService gameDetailsSuiteService;
    private final GameBackgroundService gameBackgroundService;


    public GameDetailsController(GameDetailsService gameDetailsService,
                                 GameDetailsPlayerService gameDetailsPlayerService,
                                 GameDetailsSuiteService gameDetailsSuiteService,
                                 GameBackgroundService gameBackgroundService) {
        this.gameDetailsService = gameDetailsService;
        this.gameDetailsPlayerService = gameDetailsPlayerService;
        this.gameDetailsSuiteService = gameDetailsSuiteService;
        this.gameBackgroundService = gameBackgroundService;
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
    public List<PlayerItem> fetchPlayersByGameId(@PathVariable long gameId,
                                                 @RequestParam(defaultValue = "10") int size) {
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be positive");
        }
        return gameDetailsPlayerService.fetchPlayersByGameId(gameId, size);
    }

    @GetMapping("/{gameId}/background")
    public BackgroundImageResponse fetchBackgroundByGameId(@PathVariable long gameId) {
        return gameBackgroundService.fetchBackgroundByGameId(gameId);
    }

}
