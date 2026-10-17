package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    Optional<Game> findFirstById(Long gameId);
}
