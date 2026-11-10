package fr.trophyquest.webservice.api.association.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AssociationItem(
        AssociationSuiteItem suite,
        List<AssociationGameItem> games
) {
}
