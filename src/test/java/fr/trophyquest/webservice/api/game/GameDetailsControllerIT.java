package fr.trophyquest.webservice.api.game;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameDetailsControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/game-details";

    @Autowired
    MockMvc mockMvc;


    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/game/insert-tomb-raider-game-suites.sql");
    }

    @Test
    void should_return_game_details() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(1001))
                .andExpect(jsonPath("$.title").value("Tomb Raider"))
        ;
    }

    @Test
    void should_return_game_suites() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/1001/suites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Tomb Raider 1"))
                .andExpect(jsonPath("$[0].nbBronzeTrophies").value(4))
                .andExpect(jsonPath("$[0].nbSilverTrophies").value(0))
                .andExpect(jsonPath("$[0].nbGoldTrophies").value(0))
                .andExpect(jsonPath("$[0].nbPlatinumTrophies").value(1))
                .andExpect(jsonPath("$[1].title").value("Tomb Raider 2"))
                .andExpect(jsonPath("$[1].nbBronzeTrophies").value(0))
                .andExpect(jsonPath("$[1].nbSilverTrophies").value(4))
                .andExpect(jsonPath("$[1].nbGoldTrophies").value(0))
                .andExpect(jsonPath("$[1].nbPlatinumTrophies").value(1))
                .andExpect(jsonPath("$[2].title").value("Tomb Raider 3"))
                .andExpect(jsonPath("$[2].nbBronzeTrophies").value(0))
                .andExpect(jsonPath("$[2].nbSilverTrophies").value(0))
                .andExpect(jsonPath("$[2].nbGoldTrophies").value(4))
                .andExpect(jsonPath("$[2].nbPlatinumTrophies").value(1))
                .andExpect(jsonPath("$[3]").doesNotExist())
        ;
    }

    @Test
    void should_return_players() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/1001/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].pseudo").value("Lofrings"))
                .andExpect(jsonPath("$.content[0].avatarUrl").value("lofrings.png"))
                .andExpect(jsonPath("$.content[1]").doesNotExist())
        ;
    }

    @Test
    void should_return_background() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/1001/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("tr-background.png"))
        ;
    }

    @Test
    void should_return_suite_trophies() throws Exception {
        String PLAYER_ID = "00000001-0000-0000-0000-000000000000";
        String TR1_ID = "a0000001-0000-0000-0000-000000000000";
        String TR2_ID = "a0000002-0000-0000-0000-000000000000";

        // With Player
        mockMvc.perform(get(BASE_PATH + "/suite/" + TR1_ID + "/trophies?playerId=" + PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].title").value("Tomb Raider Platinum"))
                .andExpect(jsonPath("$[0].color").value("platinum"))
                .andExpect(jsonPath("$[0].groupName").value("Tomb Raider 1"))
                .andExpect(jsonPath("$[0].earnedAt").isEmpty())
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[1].title").value("Finished Peru"))
                .andExpect(jsonPath("$[1].color").value("bronze"))
                .andExpect(jsonPath("$[1].groupName").value("Tomb Raider 1"))
                .andExpect(jsonPath("$[1].earnedAt").value("2025-09-01T10:00:00Z"))
                .andExpect(jsonPath("$[2].rank").value(3))
                .andExpect(jsonPath("$[2].title").value("Finished Greece"))
                .andExpect(jsonPath("$[2].color").value("bronze"))
                .andExpect(jsonPath("$[2].groupName").value("Tomb Raider 1"))
                .andExpect(jsonPath("$[2].earnedAt").value("2025-09-10T15:00:00Z"))
                .andExpect(jsonPath("$[3].rank").value(4))
                .andExpect(jsonPath("$[3].title").value("Finished Egypt"))
                .andExpect(jsonPath("$[3].color").value("bronze"))
                .andExpect(jsonPath("$[3].groupName").value("Tomb Raider 1"))
                .andExpect(jsonPath("$[3].earnedAt").isEmpty())
                .andExpect(jsonPath("$[4].rank").value(1))
                .andExpect(jsonPath("$[4].title").value("Finished Unfinished Business"))
                .andExpect(jsonPath("$[4].color").value("bronze"))
                .andExpect(jsonPath("$[4].groupName").value("Tomb Raider 1: Unfinished Business"))
                .andExpect(jsonPath("$[4].earnedAt").isEmpty())
                .andExpect(jsonPath("$[5]").doesNotExist())
        ;

        // Without Player
        mockMvc.perform(get(BASE_PATH + "/suite/" + TR2_ID + "/trophies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].title").value("Tomb Raider II Platinum"))
                .andExpect(jsonPath("$[0].color").value("platinum"))
                .andExpect(jsonPath("$[0].groupName").value("Tomb Raider 2"))
                .andExpect(jsonPath("$[0].earnedAt").isEmpty())
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[1].title").value("Finished Venice"))
                .andExpect(jsonPath("$[1].color").value("silver"))
                .andExpect(jsonPath("$[1].groupName").value("Tomb Raider 2"))
                .andExpect(jsonPath("$[1].earnedAt").isEmpty())
                .andExpect(jsonPath("$[2].rank").value(3))
                .andExpect(jsonPath("$[2].title").value("Finished Maria Doria"))
                .andExpect(jsonPath("$[2].color").value("silver"))
                .andExpect(jsonPath("$[2].groupName").value("Tomb Raider 2"))
                .andExpect(jsonPath("$[2].earnedAt").isEmpty())
                .andExpect(jsonPath("$[3].rank").value(4))
                .andExpect(jsonPath("$[3].title").value("Finished Tibet"))
                .andExpect(jsonPath("$[3].color").value("silver"))
                .andExpect(jsonPath("$[3].groupName").value("Tomb Raider 2"))
                .andExpect(jsonPath("$[3].earnedAt").isEmpty())
                .andExpect(jsonPath("$[4].rank").value(1))
                .andExpect(jsonPath("$[4].title").value("Finished Golden Mask"))
                .andExpect(jsonPath("$[4].color").value("silver"))
                .andExpect(jsonPath("$[4].groupName").value("Tomb Raider 2: Golden Mask"))
                .andExpect(jsonPath("$[4].earnedAt").isEmpty())
                .andExpect(jsonPath("$[5]").doesNotExist())
        ;
    }

}

