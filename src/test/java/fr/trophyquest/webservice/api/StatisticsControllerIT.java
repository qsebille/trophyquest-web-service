package fr.trophyquest.webservice.api;

import fr.trophyquest.webservice.IntegrationTestBase;
import fr.trophyquest.webservice.api.statistics.StatisticsHistorySampler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatisticsControllerIT extends IntegrationTestBase {
    private static final String ENDPOINT = "/api/statistics";

    @Autowired
    MockMvc mvc;

    @Autowired
    StatisticsHistorySampler sampler;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @Test
    void should_count_pending_images_and_distinct_games_on_the_admin_dashboard() throws Exception {
        runScript("/sql/clean-db.sql");
        runScript("/sql/insert-stub-dataset.sql");
        var jdbc = new JdbcTemplate(dataSource);
        jdbc.update("DELETE FROM metadata.image_upload_statistics_history");
        jdbc.update("""
                UPDATE app.igdb_matching SET matched_game_id = 1001, status = 'MANUAL_MATCH'
                WHERE status = 'UNCERTAIN'
                """);
        jdbc.update("UPDATE igdb.game SET igdb_background_url = 'background.png' WHERE id = 1001");
        jdbc.update("UPDATE igdb.game SET igdb_background_url = '   ' WHERE id = 1002");
        jdbc.update("UPDATE igdb.game SET igdb_background_url = 'candidate.png' WHERE id = 1003");
        jdbc.update("UPDATE igdb.game SET aws_cover_url = 'cached.png' WHERE id = 1001");
        stub_token("admin", List.of("admin"));
        stub_token("player", List.of("user"));

        mvc.perform(get(ENDPOINT)).andExpect(status().isUnauthorized());
        mvc.perform(get(ENDPOINT).header(HttpHeaders.AUTHORIZATION, "Bearer player"))
                .andExpect(status().isForbidden());

        sampler.sample();
        sampler.sample();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM metadata.image_upload_statistics_history", Long.class))
                .isEqualTo(6L);
        mvc.perform(get(ENDPOINT).header(HttpHeaders.AUTHORIZATION, "Bearer admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images.length()").value(6))
                .andExpect(jsonPath("$.images[?(@.type == 'igdb-background')].pending").value(contains(1)))
                .andExpect(jsonPath("$.images[?(@.type == 'igdb-cover')].cached").value(contains(1)))
                .andExpect(jsonPath("$.images[?(@.type == 'igdb-cover')].pending").value(contains(2)))
                .andExpect(jsonPath("$.catalog.associatedSuites").value(4))
                .andExpect(jsonPath("$.catalog.unassociatedSuites").value(1))
                .andExpect(jsonPath("$.catalog.gamesWithBackground").value(1))
                .andExpect(jsonPath("$.catalog.gamesWithoutBackground").value(2))
                .andExpect(jsonPath("$.catalog.gamesWithScreenshots").value(1))
                .andExpect(jsonPath("$.history.length()").value(6));
    }

    private void stub_token(String token, List<String> groups) {
        when(jwtDecoder.decode(token)).thenReturn(Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject(token)
                .claim("cognito:groups", groups)
                .build());
    }
}
