package fr.trophyquest.webservice.api.game.search;

import fr.trophyquest.webservice.api.game.search.dto.GameSearchItem;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/search")
public class GameSearchController {
    private final GameSearchService gameSearchService;

    public GameSearchController(GameSearchService gameSearchService) {
        this.gameSearchService = gameSearchService;
    }

    @GetMapping("")
    public Pagination<GameSearchItem> searchGames(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        return gameSearchService.searchGames(query, page, size);
    }
}
