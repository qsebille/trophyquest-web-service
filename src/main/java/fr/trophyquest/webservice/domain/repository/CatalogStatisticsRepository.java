package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.CatalogStatistics;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CatalogStatisticsRepository extends Repository<CatalogStatistics, Integer> {
    Optional<CatalogStatistics> findById(Integer id);
}
