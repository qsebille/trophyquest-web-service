package fr.trophyquest.webservice.api.player.search;

import fr.trophyquest.webservice.api.player.search.dto.PlayerSearchItem;
import fr.trophyquest.webservice.api.player.search.mapper.PlayerSearchItemMapper;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.Player;
import fr.trophyquest.webservice.domain.repository.PlayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerSearchService {

    private final PlayerRepository playerRepository;
    private final PlayerSearchItemMapper playerSearchItemMapper;

    public PlayerSearchService(PlayerRepository playerRepository,
                               PlayerSearchItemMapper playerSearchItemMapper) {
        this.playerRepository = playerRepository;
        this.playerSearchItemMapper = playerSearchItemMapper;
    }

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

}
