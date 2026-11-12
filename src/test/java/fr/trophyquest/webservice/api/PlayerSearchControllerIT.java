package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerSearchControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/player/search";

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");
        runScript("/sql/player-search-background.sql");
    }

    @Test
    void should_search_players_ignoring_case_and_return_statistics() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("query", "OFR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("00000001-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.content[0].pseudo").value("Lofrings"))
                .andExpect(jsonPath("$.content[0].avatarUrl").value("lofrings.png"))
                .andExpect(jsonPath("$.content[0].nbPlayedSuites").value(4))
                .andExpect(jsonPath("$.content[0].nbEarnedPlatinumTrophies").value(2))
                .andExpect(jsonPath("$.content[0].nbEarnedGoldTrophies").value(2))
                .andExpect(jsonPath("$.content[0].nbEarnedSilverTrophies").value(2))
                .andExpect(jsonPath("$.content[0].nbEarnedBronzeTrophies").value(3));
    }

    @Test
    void should_paginate_players_in_alphabetical_order() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("query", "").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].pseudo").value("Casual"));

        mockMvc.perform(get(BASE_PATH).param("query", "").param("page", "1").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].pseudo").value("Lofrings"));
    }

    @Test
    void should_return_empty_results_when_no_player_matches() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("query", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void should_return_empty_page_beyond_last_player() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("query", "").param("page", "2").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void should_require_query() throws Exception {
        mockMvc.perform(get(BASE_PATH)).andExpect(status().isBadRequest());
    }

    @Test
    void should_return_background_of_most_played_recent_game() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("control-background.png"));
    }
}
