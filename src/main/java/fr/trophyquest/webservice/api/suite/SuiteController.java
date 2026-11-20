package fr.trophyquest.webservice.api.suite;

import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.api.shared.dto.TrophyItem;
import fr.trophyquest.webservice.api.suite.dto.SuiteDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/suite")
public class SuiteController {
    private final SuiteService suiteService;

    public SuiteController(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    @GetMapping("/{suiteId}")
    public SuiteDetailsResponse fetchDetailsBySuiteId(@PathVariable UUID suiteId) {
        return suiteService.fetchDetailsBySuiteId(suiteId);
    }

    @PutMapping("/{suiteId}/title")
    public SuiteDetailsResponse updateTitle(@PathVariable UUID suiteId, @RequestBody UpdateTitleRequest request) {
        String title = request.title() == null ? "" : request.title().strip();
        if (title.isEmpty() || title.indexOf(0) >= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid suite title.");
        }
        return suiteService.updateTitle(suiteId, title);
    }

    @GetMapping("/{suiteId}/background")
    public BackgroundImageResponse fetchBackgroundBySuiteId(@PathVariable UUID suiteId) {
        return suiteService.fetchBackgroundBySuiteId(suiteId);
    }

    @GetMapping("/{suiteId}/players")
    public Pagination<PlayerItem> searchPlayersBySuiteId(@PathVariable UUID suiteId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be positive");
        }
        return suiteService.searchPlayersBySuiteId(suiteId, page, size);
    }

    @GetMapping("/{suiteId}/trophies")
    public List<TrophyItem> fetchTrophiesBySuiteId(@PathVariable UUID suiteId,
                                                   @RequestParam Optional<UUID> playerId) {
        return suiteService.fetchTrophiesBySuiteId(suiteId, playerId);
    }

    public record UpdateTitleRequest(String title) {
    }
}
