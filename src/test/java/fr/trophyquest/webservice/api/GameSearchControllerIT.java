package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameSearchControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/game/search";

    @Autowired
    MockMvc mockMvc;


    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");
    }

    @Test
    void should_return_paginated_games_if_not_query() throws Exception {
        mockMvc.perform(get(BASE_PATH + "?size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Astro Bot"))
                .andExpect(jsonPath("$.content[1].title").value("Control Resonant"))
                .andExpect(jsonPath("$.content[2]").doesNotExist())
        ;
    }

    @Test
    void should_return_queried_games() throws Exception {
        mockMvc.perform(get(BASE_PATH + "?size=2&query=control"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Control Resonant"))
                .andExpect(jsonPath("$.content[1]").doesNotExist())
        ;
    }

}

