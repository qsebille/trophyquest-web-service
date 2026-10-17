package fr.trophyquest.webservice.domain.repository;

import fr.trophyquest.webservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCognitoSub(String cognitoSub);
}
