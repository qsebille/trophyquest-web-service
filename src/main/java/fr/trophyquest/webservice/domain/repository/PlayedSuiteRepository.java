package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.PlayedSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayedSuiteRepository extends JpaRepository<PlayedSuite, UUID> {
    @EntityGraph(attributePaths = {"suite"})
    Page<PlayedSuite> findByPlayerId(UUID playerId, Pageable pageable);
}
