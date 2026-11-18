package fr.trophyquest.webservice.api.shared.mapper;

import fr.trophyquest.webservice.api.shared.dto.PlayerItem;
import fr.trophyquest.webservice.domain.entity.Player;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PlayerItemMapper {
    public PlayerItemMapper() {
    }

    public PlayerItem fromPlayer(Player player, Instant lastPlayedAt) {
        return PlayerItem.builder()
                .playerId(player.getId())
                .pseudo(player.getPseudo())
                .avatarUrl(player.getAvatarUrl())
                .lastPlayedAt(lastPlayedAt)
                .build();
    }
}
