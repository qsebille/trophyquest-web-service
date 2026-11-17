package fr.trophyquest.webservice.api.player.profile;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerSummaryResponse;
import fr.trophyquest.webservice.api.player.profile.dto.PlayerSuiteItem;
import fr.trophyquest.webservice.api.player.profile.dto.PlayerTrophyItem;
import fr.trophyquest.webservice.api.player.profile.service.PlayerProfileService;
import fr.trophyquest.webservice.api.player.profile.service.PlayerProfileSuiteService;
import fr.trophyquest.webservice.api.player.profile.service.PlayerProfileTrophyService;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/player/profile")
public class PlayerProfileController {
    private final PlayerProfileService profileService;
    private final PlayerProfileSuiteService suiteService;
    private final PlayerProfileTrophyService trophyService;

    public PlayerProfileController(PlayerProfileService profileService, PlayerProfileSuiteService suiteService,
                                   PlayerProfileTrophyService trophyService) {
        this.profileService = profileService;
        this.suiteService = suiteService;
        this.trophyService = trophyService;
    }

    @GetMapping("/{playerId}")
    public PlayerSummaryResponse fetchSummaryByPlayerId(@PathVariable UUID playerId) {
        return profileService.fetchSummaryByPlayerId(playerId);
    }

    @GetMapping("/{playerId}/suites")
    public Pagination<PlayerSuiteItem> searchSuitesByPlayerId(@PathVariable UUID playerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        validatePagination(page, size);
        return suiteService.searchByPlayerId(playerId, page, size);
    }

    @GetMapping("/{playerId}/trophies")
    public Pagination<PlayerTrophyItem> searchTrophiesByPlayerId(@PathVariable UUID playerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        validatePagination(page, size);
        return trophyService.searchByPlayerId(playerId, page, size);
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be positive");
        }
    }
}
