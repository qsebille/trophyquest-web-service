package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.Suite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiteRepository extends JpaRepository<Suite, UUID> {
    List<Suite> findAllByGameId(long gameId);

    @EntityGraph(attributePaths = {"candidates", "matching", "lastPlayer"})
    Page<Suite> findByMatchingStatus(String status, Pageable pageable);
}
