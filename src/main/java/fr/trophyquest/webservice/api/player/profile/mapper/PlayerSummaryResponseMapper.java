package fr.trophyquest.webservice.api.player.profile.mapper;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerSummaryResponse;
import fr.trophyquest.webservice.domain.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerSummaryResponseMapper {
    public PlayerSummaryResponse fromPlayer(Player player) {
        return PlayerSummaryResponse.builder()
                .playerId(player.getId())
                .pseudo(player.getPseudo())
                .avatarUrl(player.getAvatarUrl())
                .nbPlayedSuites(player.getNbPlayedSuites())
                .nbEarnedBronzeTrophies(player.getNbEarnedBronze())
                .nbEarnedSilverTrophies(player.getNbEarnedSilver())
                .nbEarnedGoldTrophies(player.getNbEarnedGold())
                .nbEarnedPlatinumTrophies(player.getNbEarnedPlatinum())
                .build();
    }
}
