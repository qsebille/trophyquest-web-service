package fr.trophyquest.webservice.api.statistics;

import fr.trophyquest.webservice.domain.repository.CatalogStatisticsRepository;
import fr.trophyquest.webservice.domain.repository.ImageUploadStatisticsHistoryRepository;
import fr.trophyquest.webservice.domain.repository.ImageUploadStatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class StatisticsService {
    private static final int CATALOG_STATISTICS_ID = 1;

    private final ImageUploadStatisticsRepository imagesRepository;
    private final CatalogStatisticsRepository catalogRepository;
    private final ImageUploadStatisticsHistoryRepository historyRepository;

    public StatisticsService(ImageUploadStatisticsRepository imagesRepository,
                             CatalogStatisticsRepository catalogRepository,
                             ImageUploadStatisticsHistoryRepository historyRepository) {
        this.imagesRepository = imagesRepository;
        this.catalogRepository = catalogRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public StatisticsResponse getStatistics() {
        var measuredAt = Instant.now();

        var images = imagesRepository.findAllByOrderByImageTypeAsc().stream()
                .map(image -> new StatisticsResponse.ImageCounts(
                        image.getImageType(), image.getSourceCount(), image.getCachedCount(), image.getPendingCount()))
                .toList();

        var counts = catalogRepository.findById(CATALOG_STATISTICS_ID).orElseThrow();
        var catalog = new StatisticsResponse.Catalog(
                counts.getAssociatedSuites(), counts.getUnassociatedSuites(),
                counts.getGamesWithCover(), counts.getGamesWithBackground(),
                counts.getGamesWithoutBackground(), counts.getGamesWithScreenshots(),
                counts.getPlayers(), counts.getTrophies());

        var history = historyRepository.findBySampledAtAfterOrderBySampledAtAscImageTypeAsc(
                        measuredAt.minus(24, ChronoUnit.HOURS)).stream()
                .map(point -> new StatisticsResponse.HistoryPoint(
                        point.getSampledAt(), point.getImageType(), point.getPendingCount()))
                .toList();

        return new StatisticsResponse(measuredAt, images, catalog, history);
    }
}
