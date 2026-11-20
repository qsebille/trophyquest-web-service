package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.Suite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiteRepository extends JpaRepository<Suite, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO app.suite_title_override (suite_id, title)
            SELECT id, :title
            FROM psn.suite
            WHERE id = :suiteId
            ON CONFLICT (suite_id) DO UPDATE SET title = EXCLUDED.title
            """, nativeQuery = true)
    int upsertTitleOverride(@Param("suiteId") UUID suiteId, @Param("title") String title);

    List<Suite> findAllByGameId(long gameId);

    @EntityGraph(attributePaths = {"candidates", "matching", "lastPlayer"})
    Page<Suite> findByMatchingStatus(String status, Pageable pageable);
}
