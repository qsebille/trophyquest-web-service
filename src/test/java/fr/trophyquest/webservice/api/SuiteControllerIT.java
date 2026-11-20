package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SuiteControllerIT extends IntegrationTestBase {
    private static final String SUITE_PATH = "/api/suite/{suiteId}";
    private static final String SUITE_ID = "a0000001-0000-0000-0000-000000000000";
    private static final String OTHER_SUITE_ID = "a0000002-0000-0000-0000-000000000000";
    private static final String PLAYER_ID = "00000001-0000-0000-0000-000000000000";
    private static final String OTHER_PLAYER_ID = "00000002-0000-0000-0000-000000000000";
    private static final String UNKNOWN_SUITE_ID = "a0000009-0000-0000-0000-000000000000";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/game/insert-tomb-raider-game-suites.sql");
    }

    @Test
    void should_return_suite_details() throws Exception {
        mockMvc.perform(get(SUITE_PATH, SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suiteId").value(SUITE_ID))
                .andExpect(jsonPath("$.gameId").value(1001))
                .andExpect(jsonPath("$.title").value("Tomb Raider 1"))
                .andExpect(jsonPath("$.platforms[0]").value("PS5"))
                .andExpect(jsonPath("$.imageUrl").value("tr1.png"))
                .andExpect(jsonPath("$.nbBronzeTrophies").value(4))
                .andExpect(jsonPath("$.nbPlatinumTrophies").value(1));
    }

    @Test
    void should_require_the_admin_role_to_update_a_suite_title() throws Exception {
        stubToken("regular-user", List.of("moderator"));

        mockMvc.perform(put(SUITE_PATH + "/title", SUITE_ID)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Corrected title"}
                                """))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put(SUITE_PATH + "/title", SUITE_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer regular-user")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Corrected title"}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(SUITE_PATH, SUITE_ID))
                .andExpect(jsonPath("$.title").value("Tomb Raider 1"));
    }

    @Test
    void should_keep_the_admin_title_when_psn_data_changes() throws Exception {
        stubToken("admin-user", List.of("admin"));

        mockMvc.perform(put(SUITE_PATH + "/title", SUITE_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-user")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"  Corrected title  "}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Corrected title"));

        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        assertThat(jdbc.queryForObject("SELECT name FROM psn.suite WHERE id = ?::uuid", String.class, SUITE_ID))
                .isEqualTo("Tomb Raider 1");
        assertThat(jdbc.update("""
                INSERT INTO psn.suite (id, name, psn_image_url, platforms)
                VALUES (?::uuid, 'New PSN title', 'new-psn-image.png', ARRAY['PS5'])
                ON CONFLICT (id) DO NOTHING
                """, SUITE_ID)).isZero();
        jdbc.update("UPDATE psn.suite SET name = 'Refreshed PSN title' WHERE id = ?::uuid", SUITE_ID);

        mockMvc.perform(get(SUITE_PATH, SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Corrected title"))
                .andExpect(jsonPath("$.gameId").value(1001))
                .andExpect(jsonPath("$.nbBronzeTrophies").value(4));
        mockMvc.perform(get(SUITE_PATH, OTHER_SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tomb Raider 2"));

        mockMvc.perform(put(SUITE_PATH + "/title", SUITE_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-user")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Second correction"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Second correction"));
    }

    @Test
    void should_reject_empty_titles_and_unknown_suites() throws Exception {
        stubToken("admin-user", List.of("admin"));

        for (String body : List.of("{}", "{\"title\":\"  \"}")) {
            mockMvc.perform(put(SUITE_PATH + "/title", SUITE_ID)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer admin-user")
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(put(SUITE_PATH + "/title", UNKNOWN_SUITE_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-user")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title":"Corrected title"}
                                """))
                .andExpect(status().isNotFound());
    }

    private void stubToken(String token, List<String> groups) {
        when(jwtDecoder.decode(token)).thenReturn(Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject(token)
                .claim("cognito:groups", groups)
                .build());
    }

    @Test
    void should_return_suite_without_matching_game() throws Exception {
        new JdbcTemplate(dataSource).update("DELETE FROM app.igdb_matching WHERE suite_id = ?::uuid", SUITE_ID);

        mockMvc.perform(get(SUITE_PATH, SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(nullValue()))
                .andExpect(jsonPath("$.title").value("Tomb Raider 1"));
    }

    @Test
    void should_return_the_associated_game_background() throws Exception {
        mockMvc.perform(get(SUITE_PATH + "/background", SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("tr-background.png"));
    }

    @Test
    void should_return_no_background_when_the_suite_has_no_associated_game() throws Exception {
        new JdbcTemplate(dataSource).update("DELETE FROM app.igdb_matching WHERE suite_id = ?::uuid", SUITE_ID);

        mockMvc.perform(get(SUITE_PATH + "/background", SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(nullValue()));
    }

    @Test
    void should_return_no_background_when_the_associated_game_has_no_image() throws Exception {
        new JdbcTemplate(dataSource).update("UPDATE igdb.game SET igdb_background_url = NULL WHERE id = 1001");

        mockMvc.perform(get(SUITE_PATH + "/background", SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(nullValue()));
    }

    @Test
    void should_paginate_players_and_use_activity_from_the_requested_suite() throws Exception {
        new JdbcTemplate(dataSource).update("""
                INSERT INTO psn.played_suite (player_id, suite_id, last_played_at)
                VALUES (?::uuid, ?::uuid, NULL)
                """, OTHER_PLAYER_ID, SUITE_ID);

        mockMvc.perform(get(SUITE_PATH + "/players", SUITE_ID).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.content[0].lastPlayedAt").value("2025-09-20T21:00:00Z"));

        mockMvc.perform(get(SUITE_PATH + "/players", SUITE_ID).param("page", "1").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].playerId").value(OTHER_PLAYER_ID));

        mockMvc.perform(get(SUITE_PATH + "/players", OTHER_SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.content[0].lastPlayedAt").value("2025-11-15T16:00:00Z"));
    }

    @Test
    void should_return_all_suite_trophies_with_optional_earned_dates() throws Exception {
        new JdbcTemplate(dataSource).update("""
                UPDATE psn.earned_trophy SET earned_at = NULL
                WHERE trophy_id = 'c0000003-0000-0000-0000-000000000000'
                """);

        mockMvc.perform(get(SUITE_PATH + "/trophies", SUITE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[?(@.earnedAt != null)]", hasSize(0)));

        mockMvc.perform(get(SUITE_PATH + "/trophies", SUITE_ID).param("playerId", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[?(@.earnedAt != null)].earnedAt", contains("2025-09-01T10:00:00Z")));
    }

    @Test
    void should_return_not_found_for_unknown_suites() throws Exception {
        for (String path : new String[]{SUITE_PATH, SUITE_PATH + "/players", SUITE_PATH + "/trophies",
                SUITE_PATH + "/background"}) {
            mockMvc.perform(get(path, UNKNOWN_SUITE_ID)).andExpect(status().isNotFound());
        }
    }
}
