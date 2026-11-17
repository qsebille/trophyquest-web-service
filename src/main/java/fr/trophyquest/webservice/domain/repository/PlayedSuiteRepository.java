package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.PlayedSuite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PlayedSuiteRepository extends JpaRepository<PlayedSuite, UUID> {
    @EntityGraph(attributePaths = {"suite"})
    Page<PlayedSuite> findByPlayerId(UUID playerId, Pageable pageable);

    @EntityGraph(attributePaths = {"player"})
    @Query("""
            SELECT ps
            FROM PlayedSuite ps
            WHERE ps.suite.id = :suiteId
            ORDER BY ps.lastPlayedAt DESC NULLS LAST, ps.player.id
            """)
    Page<PlayedSuite> findBySuiteId(@Param("suiteId") UUID suiteId, Pageable pageable);
}
