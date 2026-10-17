package fr.trophyquest.webservice.api.game.details.mappers;

import fr.trophyquest.webservice.api.game.details.dto.GameTrophyItem;
import fr.trophyquest.webservice.domain.entity.Trophy;
import org.springframework.stereotype.Component;

@Component
public class GameTrophyItemMapper {
    public GameTrophyItemMapper() {
    }

    public GameTrophyItem fromTrophy(Trophy trophy) {
        return GameTrophyItem.builder()
                .trophyId(trophy.getId())
                .rank(trophy.getRank())
                .title(trophy.getTitle())
                .description(trophy.getDescription())
                .color(trophy.getColor())
                .iconUrl(trophy.getIconUrl())
                .isHidden(trophy.getIsHidden())
                .groupName(trophy.getGroupName())
                .build();
    }
}
