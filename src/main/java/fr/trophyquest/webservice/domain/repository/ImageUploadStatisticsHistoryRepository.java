package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.ImageUploadStatisticsHistory;
import org.springframework.data.repository.Repository;

import java.time.Instant;
import java.util.List;

public interface ImageUploadStatisticsHistoryRepository
        extends Repository<ImageUploadStatisticsHistory, ImageUploadStatisticsHistory.Key> {
    List<ImageUploadStatisticsHistory> findBySampledAtAfterOrderBySampledAtAscImageTypeAsc(Instant since);
}
