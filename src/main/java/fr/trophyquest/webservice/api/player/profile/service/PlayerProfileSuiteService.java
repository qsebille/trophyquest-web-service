package fr.trophyquest.webservice.api.player.profile.service;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerSuiteItem;
import fr.trophyquest.webservice.api.player.profile.mapper.PlayerSuiteItemMapper;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.PlayedSuite;
import fr.trophyquest.webservice.domain.repository.PlayedSuiteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PlayerProfileSuiteService {
    private final PlayedSuiteRepository repository;
    private final PlayerSuiteItemMapper mapper;
    private final PlayerProfileService playerProfileService;

    public PlayerProfileSuiteService(PlayedSuiteRepository repository, PlayerSuiteItemMapper mapper,
                                     PlayerProfileService playerProfileService) {
        this.repository = repository;
        this.mapper = mapper;
        this.playerProfileService = playerProfileService;
    }

    public Pagination<PlayerSuiteItem> searchByPlayerId(UUID playerId, int page, int size) {
        playerProfileService.requirePlayer(playerId);
        PageRequest request = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastPlayedAt"));
        Page<PlayedSuite> result = repository.findByPlayerId(playerId, request);
        return Pagination.<PlayerSuiteItem>builder()
                .content(result.getContent().stream().map(mapper::fromPlayedSuite).toList())
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }
}
