package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Page<Player> findByPseudoContainingIgnoreCase(String query, Pageable pageable);
}
