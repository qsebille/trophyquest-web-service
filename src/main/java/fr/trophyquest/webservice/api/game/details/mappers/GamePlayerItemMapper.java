package fr.trophyquest.webservice.api.game.details.mappers;

import fr.trophyquest.webservice.api.game.details.dto.GamePlayerItem;
import fr.trophyquest.webservice.domain.entity.PlayedGame;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerItemMapper {
    public GamePlayerItemMapper() {
    }

    public GamePlayerItem fromPlayedGame(PlayedGame playedGame) {
        return GamePlayerItem.builder()
                .playerId(playedGame.getPlayer().getId())
                .pseudo(playedGame.getPlayer().getPseudo())
                .avatarUrl(playedGame.getPlayer().getAvatarUrl())
                .lastPlayedAt(playedGame.getLastPlayedAt())
                .build();
    }
}
