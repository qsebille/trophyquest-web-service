package fr.trophyquest.webservice.api.player.profile.service;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerTrophyItem;
import fr.trophyquest.webservice.api.player.profile.mapper.PlayerTrophyItemMapper;
import fr.trophyquest.webservice.api.shared.dto.Pagination;
import fr.trophyquest.webservice.domain.entity.EarnedTrophy;
import fr.trophyquest.webservice.domain.repository.EarnedTrophyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PlayerProfileTrophyService {
    private final EarnedTrophyRepository repository;
    private final PlayerTrophyItemMapper mapper;
    private final PlayerProfileService playerProfileService;

    public PlayerProfileTrophyService(EarnedTrophyRepository repository, PlayerTrophyItemMapper mapper,
                                      PlayerProfileService playerProfileService) {
        this.repository = repository;
        this.mapper = mapper;
        this.playerProfileService = playerProfileService;
    }

    public Pagination<PlayerTrophyItem> searchByPlayerId(UUID playerId, int page, int size) {
        playerProfileService.requirePlayer(playerId);
        PageRequest request = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "earnedAt"));
        Page<EarnedTrophy> result = repository.findByPlayerId(playerId, request);
        return Pagination.<PlayerTrophyItem>builder()
                .content(result.getContent().stream().map(mapper::fromEarnedTrophy).toList())
                .total(result.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }
}
