package fr.trophyquest.webservice.api.shared.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record TrophyItem(
        UUID trophyId,
        int rank,
        String title,
        String description,
        String color,
        String iconUrl,
        boolean isHidden,
        String groupName,
        Instant earnedAt
) {
}
