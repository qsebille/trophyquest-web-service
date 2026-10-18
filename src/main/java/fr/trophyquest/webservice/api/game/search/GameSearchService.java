package fr.trophyquest.webservice.api.game.search;

import fr.trophyquest.webservice.api.game.search.dto.GameSearchItem;
import fr.trophyquest.webservice.api.game.search.mapper.GameSearchItemMapper;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.Game;
import fr.trophyquest.webservice.domain.repository.GameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameSearchService {
    private final GameRepository gameRepository;
    private final GameSearchItemMapper gameSearchItemMapper;

    public GameSearchService(GameRepository gameRepository,
                             GameSearchItemMapper gameSearchItemMapper) {
        this.gameRepository = gameRepository;
        this.gameSearchItemMapper = gameSearchItemMapper;
    }

    /**
     * Return a paginated list of game search items matching the given query.
     *
     * @param query Query that matches the name of the game
     * @return A paginated list of game search items matching the given query
     */
    public Pagination<GameSearchItem> searchGames(String query, int pageNumber, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        Page<Game> result = gameRepository.searchByName(query, pageRequest);
        List<GameSearchItem> content = result.stream()
                .map(this.gameSearchItemMapper::fromGame)
                .toList();

        return Pagination.<GameSearchItem>builder()
                .page(result.getNumber())
                .size(result.getSize())
                .total(result.getTotalElements())
                .content(content)
                .build();
    }
}
