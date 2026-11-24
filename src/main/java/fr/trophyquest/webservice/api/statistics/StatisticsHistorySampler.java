package fr.trophyquest.webservice.api.statistics;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableScheduling
public class StatisticsHistorySampler {
    private final JdbcClient jdbc;

    public StatisticsHistorySampler(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Scheduled(cron = "0 */15 * * * *", zone = "UTC")
    @Transactional
    public void sample() {
        jdbc.sql("""
                INSERT INTO metadata.image_upload_statistics_history (sampled_at, image_type, pending_count)
                SELECT date_bin(INTERVAL '15 minutes', CURRENT_TIMESTAMP, TIMESTAMPTZ '2000-01-01 00:00:00+00'),
                       image_type, pending_count
                FROM metadata.image_upload_statistics
                ON CONFLICT (sampled_at, image_type) DO NOTHING
                """).update();
        jdbc.sql("""
                DELETE FROM metadata.image_upload_statistics_history
                WHERE sampled_at < CURRENT_TIMESTAMP - INTERVAL '30 days'
                """).update();
    }
}
