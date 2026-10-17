package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.Suite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuiteRepository extends JpaRepository<Suite, UUID> {
    List<Suite> findAllByGameId(long gameId);
}
