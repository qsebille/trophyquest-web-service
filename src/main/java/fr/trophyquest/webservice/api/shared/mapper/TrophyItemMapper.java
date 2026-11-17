package fr.trophyquest.webservice.api.shared.mapper;

import fr.trophyquest.webservice.api.shared.dto.TrophyItem;
import fr.trophyquest.webservice.domain.entity.Trophy;
import org.springframework.stereotype.Component;

@Component
public class TrophyItemMapper {
    public TrophyItemMapper() {
    }

    public TrophyItem fromTrophy(Trophy trophy) {
        return TrophyItem.builder()
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
