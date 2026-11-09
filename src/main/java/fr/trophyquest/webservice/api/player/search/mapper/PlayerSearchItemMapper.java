package fr.trophyquest.webservice.api.player.search.mapper;

import fr.trophyquest.webservice.api.player.search.dto.PlayerSearchItem;
import fr.trophyquest.webservice.domain.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerSearchItemMapper {

    public PlayerSearchItem map(Player player) {
        return PlayerSearchItem.builder()
                .id(player.getId())
                .pseudo(player.getPseudo())
                .avatarUrl(player.getAvatarUrl())
                .nbEarnedPlatinumTrophies(player.getNbEarnedPlatinum())
                .nbEarnedGoldTrophies(player.getNbEarnedGold())
                .nbEarnedSilverTrophies(player.getNbEarnedSilver())
                .nbEarnedBronzeTrophies(player.getNbEarnedBronze())
                .nbPlayedSuites(player.getNbPlayedSuites())
                .build();
    }

}
