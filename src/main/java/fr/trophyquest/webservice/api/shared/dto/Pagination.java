package fr.trophyquest.webservice.api.shared.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record Pagination<T>(
        List<T> content,
        long total,
        long page,
        long size
) {
}
