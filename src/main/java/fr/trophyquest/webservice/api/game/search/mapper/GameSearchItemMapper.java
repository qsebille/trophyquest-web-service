package fr.trophyquest.webservice.api.game.search.mapper;

import fr.trophyquest.webservice.api.game.search.dto.GameSearchItem;
import fr.trophyquest.webservice.domain.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameSearchItemMapper {

    public GameSearchItem fromGame(Game game) {
        return GameSearchItem.builder()
                .gameId(game.getId())
                .title(game.getName())
                .coverUrl(game.getCoverUrl())
                .platforms(game.getPlatforms())
                .nbPlayers(game.getNbPlayers())
                .nbPlatinumTrophies(game.getNbPlatinumTrophies())
                .nbGoldTrophies(game.getNbGoldTrophies())
                .nbSilverTrophies(game.getNbSilverTrophies())
                .nbBronzeTrophies(game.getNbBronzeTrophies())
                .build();
    }
}
