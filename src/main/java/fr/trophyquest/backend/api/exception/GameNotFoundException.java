package fr.trophyquest.backend.api.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game not found by game id: " + gameId);
    }
}
