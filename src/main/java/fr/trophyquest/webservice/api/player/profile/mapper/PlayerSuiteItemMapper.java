package fr.trophyquest.webservice.api.player.profile.mapper;

import fr.trophyquest.webservice.api.player.profile.dto.PlayerSuiteItem;
import fr.trophyquest.webservice.domain.entity.PlayedSuite;
import org.springframework.stereotype.Component;

@Component
public class PlayerSuiteItemMapper {
    public PlayerSuiteItem fromPlayedSuite(PlayedSuite playedSuite) {
        return PlayerSuiteItem.builder()
                .suiteId(playedSuite.getSuite().getId())
                .gameId(playedSuite.getSuite().getGameId())
                .title(playedSuite.getSuite().getName())
                .platforms(playedSuite.getSuite().getPlatforms())
                .imageUrl(playedSuite.getSuite().getImageUrl())
                .nbBronzeTrophies(playedSuite.getSuite().getNbBronzeTrophies())
                .nbSilverTrophies(playedSuite.getSuite().getNbSilverTrophies())
                .nbGoldTrophies(playedSuite.getSuite().getNbGoldTrophies())
                .nbPlatinumTrophies(playedSuite.getSuite().getNbPlatinumTrophies())
                .nbEarnedBronzeTrophies(playedSuite.getNbEarnedBronze())
                .nbEarnedSilverTrophies(playedSuite.getNbEarnedSilver())
                .nbEarnedGoldTrophies(playedSuite.getNbEarnedGold())
                .nbEarnedPlatinumTrophies(playedSuite.getNbEarnedPlatinum())
                .lastPlayedAt(playedSuite.getLastPlayedAt())
                .build();
    }
}
