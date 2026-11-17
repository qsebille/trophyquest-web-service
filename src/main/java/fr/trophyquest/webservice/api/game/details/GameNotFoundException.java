package fr.trophyquest.webservice.api.game.details;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long gameId) {
        super("Game not found by game id: " + gameId);
    }
}
