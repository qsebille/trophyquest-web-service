package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.ImageUploadStatistics;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ImageUploadStatisticsRepository extends Repository<ImageUploadStatistics, String> {
    List<ImageUploadStatistics> findAllByOrderByImageTypeAsc();
}
