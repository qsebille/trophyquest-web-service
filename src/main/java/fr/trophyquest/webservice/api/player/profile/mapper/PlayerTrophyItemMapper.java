package fr.trophyquest.webservice.api.player.profile.mapper;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerTrophyItem;
import fr.trophyquest.webservice.domain.entity.EarnedTrophy;
import org.springframework.stereotype.Component;

@Component
public class PlayerTrophyItemMapper {
    public PlayerTrophyItem fromEarnedTrophy(EarnedTrophy earnedTrophy) {
        return PlayerTrophyItem.builder()
                .trophyId(earnedTrophy.getTrophy().getId())
                .suiteId(earnedTrophy.getTrophy().getSuite().getId())
                .gameId(earnedTrophy.getTrophy().getSuite().getGameId())
                .suiteTitle(earnedTrophy.getTrophy().getSuite().getName())
                .rank(earnedTrophy.getTrophy().getRank())
                .title(earnedTrophy.getTrophy().getTitle())
                .description(earnedTrophy.getTrophy().getDescription())
                .color(earnedTrophy.getTrophy().getColor())
                .iconUrl(earnedTrophy.getTrophy().getIconUrl())
                .earnedAt(earnedTrophy.getEarnedAt())
                .build();
    }
}
