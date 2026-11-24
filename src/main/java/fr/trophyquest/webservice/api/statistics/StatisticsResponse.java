package fr.trophyquest.webservice.api.statistics;

import java.time.Instant;
import java.util.List;

public record StatisticsResponse(Instant measuredAt, List<ImageCounts> images, Catalog catalog,
                                 List<HistoryPoint> history) {
    public record ImageCounts(String type, long source, long cached, long pending) {
    }

    public record Catalog(long associatedSuites, long unassociatedSuites, long gamesWithCover,
                          long gamesWithBackground, long gamesWithoutBackground, long gamesWithScreenshots,
                          long players, long trophies) {
    }

    public record HistoryPoint(Instant sampledAt, String type, long pending) {
    }
}
