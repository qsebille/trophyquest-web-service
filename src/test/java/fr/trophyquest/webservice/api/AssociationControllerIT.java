package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssociationControllerIT extends IntegrationTestBase {

    private static final String BASE_PATH = "/api/association";
    private static final String SUITE_ID = "a0000003-0000-0000-0000-000000000000";

    @Autowired
    MockMvc mockMvc;

    private JdbcTemplate jdbc;

    @BeforeEach
    void beforeEach() {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");
        jdbc = new JdbcTemplate(dataSource);
    }

    @Test
    void should_return_only_uncertain_associations_with_candidates() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].suite.suiteId").value(SUITE_ID))
                .andExpect(jsonPath("$.content[0].suite.title").value("Tomb Raider"))
                .andExpect(jsonPath("$.content[0].suite.imageUrl").value("tr.png"))
                .andExpect(jsonPath("$.content[0].suite.platforms[0]").value("PS4"))
                .andExpect(jsonPath("$.content[0].suite.lastPlayerPseudo").value("Casual"))
                .andExpect(jsonPath("$.content[0].suite.lastPlayerAvatar").value("casual.png"))
                .andExpect(jsonPath("$.content[0].suite.nbTrophies").value(2))
                .andExpect(jsonPath("$.content[0].games.length()").value(2))
                .andExpect(jsonPath("$.content[0].games[*].gameId", containsInAnyOrder(1003, 1004)))
                .andExpect(jsonPath("$.content[0].games[?(@.gameId == 1003)].title").value("Tomb Raider"))
                .andExpect(jsonPath("$.content[0].games[?(@.gameId == 1003)].score").value(0.88));
    }

    @Test
    void should_return_empty_page_beyond_last_association() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void should_validate_selected_candidate_and_reject_others() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/validate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"suiteId\":\"" + SUITE_ID + "\",\"gameId\":1003}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        assertThat(matchingStatus()).isEqualTo("MANUAL_MATCH");
        assertThat(jdbc.queryForObject("SELECT matched_game_id FROM app.igdb_matching WHERE suite_id = ?::uuid",
                Long.class, SUITE_ID)).isEqualTo(1003L);
        assertThat(candidateStatus(1003)).isEqualTo("VALIDATED");
        assertThat(candidateStatus(1004)).isEqualTo("REJECTED");
        assertThat(jdbc.queryForObject("SELECT status FROM app.igdb_matching WHERE matched_game_id = 1005",
                String.class)).isEqualTo("AUTO_MATCH");
        assertNoRemainingAssociation();
    }

    @Test
    void should_reject_all_candidates_and_clear_matching() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/reject").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"suiteId\":\"" + SUITE_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        assertThat(matchingStatus()).isEqualTo("NO_MATCH_FOUND");
        assertThat(jdbc.queryForObject("SELECT matched_game_id FROM app.igdb_matching WHERE suite_id = ?::uuid",
                Long.class, SUITE_ID)).isNull();
        assertThat(candidateStatus(1003)).isEqualTo("REJECTED");
        assertThat(candidateStatus(1004)).isEqualTo("REJECTED");
        assertNoRemainingAssociation();
    }

    @Test
    void should_require_page() throws Exception {
        mockMvc.perform(get(BASE_PATH)).andExpect(status().isBadRequest());
    }

    @Test
    void should_reject_invalid_suite_id_without_changing_database() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/validate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"suiteId\":\"invalid\",\"gameId\":1003}"))
                .andExpect(status().isBadRequest());
        assertThat(matchingStatus()).isEqualTo("UNCERTAIN");
        assertThat(candidateStatus(1003)).isEqualTo("PENDING");
        assertThat(candidateStatus(1004)).isEqualTo("PENDING");
    }

    private String matchingStatus() {
        return jdbc.queryForObject("SELECT status FROM app.igdb_matching WHERE suite_id = ?::uuid", String.class, SUITE_ID);
    }

    private String candidateStatus(int gameId) {
        return jdbc.queryForObject("SELECT status FROM app.igdb_candidate WHERE suite_id = ?::uuid AND candidate_id = ?",
                String.class, SUITE_ID, gameId);
    }

    private void assertNoRemainingAssociation() throws Exception {
        mockMvc.perform(get(BASE_PATH).param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
