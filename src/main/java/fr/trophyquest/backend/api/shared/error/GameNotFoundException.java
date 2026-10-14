package fr.trophyquest.backend.api.shared.error;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game not found by game id: " + gameId);
    }
}
