package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HomeControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/home";

    @Autowired
    MockMvc mockMvc;

    @Test
    void should_return_top_games() throws Exception {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");

        mockMvc.perform(get(BASE_PATH + "/top-games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].gameId").value(1005))
                .andExpect(jsonPath("$[0].title").value("Control Resonant"))
                .andExpect(jsonPath("$[0].nbRecentPlayers").value(2))
                .andExpect(jsonPath("$[1].gameId").value(1002))
                .andExpect(jsonPath("$[1].title").value("The Last of Us Part II"))
                .andExpect(jsonPath("$[1].nbRecentPlayers").value(1))
                .andExpect(jsonPath("$[2]").doesNotExist())
        ;
    }

    @Test
    void should_return_top_players() throws Exception {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");

        mockMvc.perform(get(BASE_PATH + "/top-players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].pseudo").value("Lofrings"))
                .andExpect(jsonPath("$[0].nbRecentBronzeTrophies").value(1))
                .andExpect(jsonPath("$[0].nbRecentSilverTrophies").value(1))
                .andExpect(jsonPath("$[0].nbRecentGoldTrophies").value(1))
                .andExpect(jsonPath("$[0].nbRecentPlatinumTrophies").value(1))
                .andExpect(jsonPath("$[1].pseudo").value("Casual"))
                .andExpect(jsonPath("$[1].nbRecentBronzeTrophies").value(1))
                .andExpect(jsonPath("$[1].nbRecentSilverTrophies").value(0))
                .andExpect(jsonPath("$[1].nbRecentGoldTrophies").value(0))
                .andExpect(jsonPath("$[1].nbRecentPlatinumTrophies").value(0))
                .andExpect(jsonPath("$[2]").doesNotExist())
        ;
    }
}

