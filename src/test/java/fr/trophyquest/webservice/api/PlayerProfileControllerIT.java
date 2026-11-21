package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerProfileControllerIT extends IntegrationTestBase {
    private static final String BASE_PATH = "/api/player/profile/";
    private static final String PLAYER_ID = "00000001-0000-0000-0000-000000000000";
    private static final String NEW_PLAYER_ID = "00000003-0000-0000-0000-000000000000";

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/game/insert-tomb-raider-game-suites.sql");
        runScript("/sql/player/insert-profile-activity.sql");
    }

    @Test
    void should_return_summary_with_earned_counts_by_color() throws Exception {
        mockMvc.perform(get(BASE_PATH + PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(PLAYER_ID))
                .andExpect(jsonPath("$.pseudo").value("Lofrings"))
                .andExpect(jsonPath("$.avatarUrl").value("lofrings.png"))
                .andExpect(jsonPath("$.nbPlayedSuites").value(3))
                .andExpect(jsonPath("$.nbEarnedBronzeTrophies").value(2))
                .andExpect(jsonPath("$.nbEarnedSilverTrophies").value(1))
                .andExpect(jsonPath("$.nbEarnedGoldTrophies").value(1))
                .andExpect(jsonPath("$.nbEarnedPlatinumTrophies").value(1));
    }

    @Test
    void should_serve_the_cached_avatar_until_the_psn_avatar_changes() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String refreshedAt = "SELECT updated_at FROM psn.player WHERE id = ?::uuid";
        OffsetDateTime previousRefresh = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        jdbc.update("UPDATE psn.player SET updated_at = ? WHERE id = ?::uuid", previousRefresh, PLAYER_ID);
        jdbc.update("UPDATE psn.player SET aws_avatar_url = 'cached.png' WHERE id = ?::uuid", PLAYER_ID);

        assertThat(jdbc.queryForObject(refreshedAt, OffsetDateTime.class, PLAYER_ID)).isEqualTo(previousRefresh);
        mockMvc.perform(get(BASE_PATH + PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("cached.png"));

        jdbc.update("UPDATE psn.player SET psn_avatar_url = 'lofrings.png' WHERE id = ?::uuid", PLAYER_ID);

        assertThat(jdbc.queryForObject(refreshedAt, OffsetDateTime.class, PLAYER_ID)).isAfter(previousRefresh);
        assertThat(jdbc.queryForObject("SELECT aws_avatar_url FROM psn.player WHERE id = ?::uuid",
                String.class, PLAYER_ID)).isEqualTo("cached.png");

        jdbc.update("UPDATE psn.player SET psn_avatar_url = 'new.png' WHERE id = ?::uuid", PLAYER_ID);

        mockMvc.perform(get(BASE_PATH + PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("new.png"));
    }

    @Test
    void should_return_zero_counts_without_activity() throws Exception {
        mockMvc.perform(get(BASE_PATH + NEW_PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("New player"))
                .andExpect(jsonPath("$.nbPlayedSuites").value(0))
                .andExpect(jsonPath("$.nbEarnedBronzeTrophies").value(0))
                .andExpect(jsonPath("$.nbEarnedSilverTrophies").value(0))
                .andExpect(jsonPath("$.nbEarnedGoldTrophies").value(0))
                .andExpect(jsonPath("$.nbEarnedPlatinumTrophies").value(0));
    }

    @Test
    void should_return_only_player_suites_in_reverse_chronological_order() throws Exception {
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/suites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].suiteId").value("a0000003-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.content[0].gameId").value(1001))
                .andExpect(jsonPath("$.content[0].title").value("Tomb Raider 3"))
                .andExpect(jsonPath("$.content[0].imageUrl").value("tr3.png"))
                .andExpect(jsonPath("$.content[0].nbGoldTrophies").value(4))
                .andExpect(jsonPath("$.content[0].nbEarnedGoldTrophies").value(1))
                .andExpect(jsonPath("$.content[0].lastPlayedAt").value("2025-12-15T18:00:00Z"))
                .andExpect(jsonPath("$.content[1].title").value("Tomb Raider 2"))
                .andExpect(jsonPath("$.content[1].nbEarnedSilverTrophies").value(1))
                .andExpect(jsonPath("$.content[1].nbEarnedPlatinumTrophies").value(1))
                .andExpect(jsonPath("$.content[2].title").value("Tomb Raider 1"))
                .andExpect(jsonPath("$.content[2].nbEarnedBronzeTrophies").value(2));
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/suites").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Tomb Raider 1"));
    }

    @Test
    void should_return_only_earned_trophies_in_reverse_chronological_order() throws Exception {
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/trophies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].trophyId").value("c0000012-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.content[0].suiteId").value("a0000003-0000-0000-0000-000000000000"))
                .andExpect(jsonPath("$.content[0].gameId").value(1001))
                .andExpect(jsonPath("$.content[0].suiteTitle").value("Tomb Raider 3"))
                .andExpect(jsonPath("$.content[0].title").value("Finished India"))
                .andExpect(jsonPath("$.content[0].description").value("Finish the India levels"))
                .andExpect(jsonPath("$.content[0].color").value("gold"))
                .andExpect(jsonPath("$.content[0].iconUrl").value("tr3-india.png"))
                .andExpect(jsonPath("$.content[0].earnedAt").value("2025-12-15T18:00:00Z"))
                .andExpect(jsonPath("$.content[1].title").value("Tomb Raider II Platinum"))
                .andExpect(jsonPath("$.content[2].title").value("Finished Venice"))
                .andExpect(jsonPath("$.content[3].title").value("Finished Greece"))
                .andExpect(jsonPath("$.content[4].title").value("Finished Peru"));
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/trophies").param("page", "1").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Finished Greece"))
                .andExpect(jsonPath("$.content[1].title").value("Finished Peru"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/suites", "/trophies"})
    void should_return_empty_pages(String suffix) throws Exception {
        mockMvc.perform(get(BASE_PATH + NEW_PLAYER_ID + suffix))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + suffix).param("page", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/suites", "/trophies", "/background"})
    void should_return_not_found_for_unknown_player(String suffix) throws Exception {
        mockMvc.perform(get(BASE_PATH + "00000099-0000-0000-0000-000000000000" + suffix))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/suites", "/trophies", "/background"})
    void should_reject_invalid_player_id(String suffix) throws Exception {
        mockMvc.perform(get(BASE_PATH + "invalid-id" + suffix)).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/suites", "/trophies"})
    void should_reject_invalid_pagination(String suffix) throws Exception {
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + suffix).param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + suffix).param("size", "0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + suffix).param("size", "-1"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void should_return_background_of_players_latest_game() throws Exception {
        runScript("/sql/player/insert-profile-backgrounds.sql");
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("latest-background.png"));
    }

    @Test
    void should_return_empty_background_without_played_games() throws Exception {
        mockMvc.perform(get(BASE_PATH + NEW_PLAYER_ID + "/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(""));
    }

    @Test
    void should_return_empty_background_when_latest_game_has_no_image() throws Exception {
        runScript("/sql/player/insert-profile-backgrounds.sql");
        new JdbcTemplate(dataSource).update("UPDATE igdb.game SET igdb_background_url = NULL WHERE id = 1002");
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(""));
    }

    @Test
    void should_prioritize_known_play_dates_over_missing_dates() throws Exception {
        runScript("/sql/player/insert-profile-backgrounds.sql");
        new JdbcTemplate(dataSource).update("UPDATE psn.played_suite SET last_played_at = NULL WHERE suite_id = ?::uuid",
                "a0000003-0000-0000-0000-000000000000");
        mockMvc.perform(get(BASE_PATH + PLAYER_ID + "/background"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("older-background.png"));
    }

}
