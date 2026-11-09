package fr.trophyquest.webservice.api.player.search;

import fr.trophyquest.webservice.api.player.search.dto.PlayerSearchItem;
import fr.trophyquest.webservice.api.player.search.mapper.PlayerSearchItemMapper;
import fr.trophyquest.webservice.api.shared.dto.BackgroundImageResponse;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.Player;
import fr.trophyquest.webservice.domain.projection.RecentGameProjection;
import fr.trophyquest.webservice.domain.repository.PlayedGameRepository;
import fr.trophyquest.webservice.domain.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerSearchService {

    private final PlayerRepository playerRepository;
    private final PlayedGameRepository playedGameRepository;
    private final PlayerSearchItemMapper playerSearchItemMapper;

    public PlayerSearchService(PlayerRepository playerRepository,
                               PlayedGameRepository playedGameRepository,
                               PlayerSearchItemMapper playerSearchItemMapper) {
        this.playerRepository = playerRepository;
        this.playedGameRepository = playedGameRepository;
        this.playerSearchItemMapper = playerSearchItemMapper;
    }

    /**
     * Returns a paginated list of players matching the given query.
     *
     * @param query Query for player pseudo
     * @param page  Page parameter
     * @param size  Size parameter
     * @return A paginated list of players matching the given query.
     */
    public Pagination<PlayerSearchItem> searchPlayers(String query, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "pseudo");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Player> searchResult = playerRepository.findByPseudoContainingIgnoreCase(query, pageRequest);

        List<PlayerSearchItem> searchItems = searchResult.getContent().stream()
                .map(playerSearchItemMapper::map)
                .collect(Collectors.toList());

        return Pagination.<PlayerSearchItem>builder()
                .content(searchItems)
                .total(searchResult.getTotalElements())
                .page(searchResult.getNumber())
                .size(searchResult.getSize())
                .build();
    }

    /**
     * Returns the background image for the player search page.
     *
     * @return The background image response.
     */
    public BackgroundImageResponse getBackground() {
        Instant recentLimit = Instant.now().minus(7, ChronoUnit.DAYS);
        RecentGameProjection topGame = this.playedGameRepository.searchTopRecent(recentLimit, 1).getFirst();
        return BackgroundImageResponse.builder().url(topGame.getBackgroundUrl()).build();
    }

}
