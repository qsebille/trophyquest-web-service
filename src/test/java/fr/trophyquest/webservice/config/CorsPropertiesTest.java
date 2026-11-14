package fr.trophyquest.webservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

import static org.assertj.core.api.Assertions.assertThat;

class CorsPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(CorsProperties.class);

    @Test
    void should_bind_default_origin() {
        contextRunner.run(context -> assertThat(context.getBean(CorsProperties.class).getAllowedOrigins())
                .containsExactly("http://localhost:4200"));
    }

    @Test
    void should_bind_local_origin() {
        contextRunner.withPropertyValues("spring.profiles.active=local").run(context ->
                assertThat(context.getBean(CorsProperties.class).getAllowedOrigins())
                        .containsExactly("http://localhost:4200"));
    }

    @Test
    void should_accept_production_post_origins_and_reject_other_origins() {
        contextRunner.withPropertyValues("spring.profiles.active=prod").run(context -> {
            var properties = context.getBean(CorsProperties.class);
            assertThat(properties.getAllowedOrigins())
                    .containsExactly("https://trophyquest.fr", "https://www.trophyquest.fr");
            var source = new SecurityConfig().corsConfigurationSource(properties);

            for (String origin : properties.getAllowedOrigins()) {
                var request = new MockHttpServletRequest("POST", "/api/association/validate");
                request.addHeader("Origin", origin);
                var response = new MockHttpServletResponse();

                assertThat(new DefaultCorsProcessor().processRequest(
                        source.getCorsConfiguration(request), request, response)).isTrue();
                assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo(origin);
            }

            var request = new MockHttpServletRequest("POST", "/api/association/validate");
            request.addHeader("Origin", "https://example.com");
            var response = new MockHttpServletResponse();
            assertThat(new DefaultCorsProcessor().processRequest(
                    source.getCorsConfiguration(request), request, response)).isFalse();
            assertThat(response.getStatus()).isEqualTo(403);
        });
    }
}
