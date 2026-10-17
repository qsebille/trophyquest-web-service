package fr.trophyquest.webservice.api.home.mapper;

import fr.trophyquest.webservice.api.home.dto.HomePlayerItem;
import fr.trophyquest.webservice.domain.projection.RecentPlayerProjection;
import org.springframework.stereotype.Component;

@Component
public class HomePlayerItemMapper {

    public HomePlayerItem fromRecentPlayerProjection(RecentPlayerProjection projection) {
        return HomePlayerItem.builder()
                .playerId(projection.getPlayerId())
                .pseudo(projection.getPseudo())
                .avatarUrl(projection.getAvatarUrl())
                .nbRecentBronzeTrophies(projection.getNbRecentBronzeTrophies())
                .nbRecentSilverTrophies(projection.getNbRecentSilverTrophies())
                .nbRecentGoldTrophies(projection.getNbRecentGoldTrophies())
                .nbRecentPlatinumTrophies(projection.getNbRecentPlatinumTrophies())
                .build();
    }

}
