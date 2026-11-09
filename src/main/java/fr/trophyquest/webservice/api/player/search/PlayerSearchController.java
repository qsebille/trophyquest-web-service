package fr.trophyquest.webservice.api.player.search;

import fr.trophyquest.webservice.api.player.search.dto.PlayerSearchItem;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player/search")
public class PlayerSearchController {

    private final PlayerSearchService playerSearchService;

    public PlayerSearchController(PlayerSearchService playerSearchService) {
        this.playerSearchService = playerSearchService;
    }


    @GetMapping("")
    public Pagination<PlayerSearchItem> searchPlayers(@RequestParam(name = "query") String query,
                                                      @RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "size", defaultValue = "20") int size) {
        return playerSearchService.searchPlayers(query, page, size);
    }

}
