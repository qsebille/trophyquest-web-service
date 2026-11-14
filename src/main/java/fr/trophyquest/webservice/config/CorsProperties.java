package fr.trophyquest.webservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
public class CorsProperties {
    private List<String> allowedOrigins;
}
