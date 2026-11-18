package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameDetailsControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/game/details";

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
    void should_return_recent_players_with_a_size_limit() throws Exception {
        new JdbcTemplate(dataSource).update("""
                INSERT INTO psn.played_suite (player_id, suite_id, last_played_at)
                VALUES ('00000002-0000-0000-0000-000000000000',
                        'a0000001-0000-0000-0000-000000000000', '2027-01-01T00:00:00Z')
                """);

        mockMvc.perform(get(BASE_PATH + "/1001/players").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pseudo").value("Another player"))
                .andExpect(jsonPath("$[1]").doesNotExist());

        mockMvc.perform(get(BASE_PATH + "/1001/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].pseudo").value("Lofrings"))
                .andExpect(jsonPath("$[1].avatarUrl").value("lofrings.png"));

        mockMvc.perform(get(BASE_PATH + "/1001/players").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_background() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/1001/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("tr-background.png"))
        ;
    }
}
