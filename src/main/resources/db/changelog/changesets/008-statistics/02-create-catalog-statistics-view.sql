--liquibase formatted sql
--changeset trophyquest:008-catalog-statistics-view
CREATE VIEW metadata.catalog_statistics AS
WITH suite_counts AS (SELECT COUNT(*) FILTER (WHERE matching.matched_game_id IS NOT NULL) AS associated_suites,
                             COUNT(*) FILTER (WHERE matching.matched_game_id IS NULL)     AS unassociated_suites
                      FROM psn.suite suite
                               LEFT JOIN app.igdb_matching matching ON matching.suite_id = suite.id
                          AND matching.status IN ('AUTO_MATCH', 'MANUAL_MATCH')),
     matched_games AS (SELECT DISTINCT matched_game_id
                       FROM app.igdb_matching
                       WHERE status IN ('AUTO_MATCH', 'MANUAL_MATCH')),
     game_counts
         AS (SELECT COUNT(*)
                    FILTER (WHERE COALESCE(game.aws_cover_url, game.igdb_cover_url) ~ '[^[:space:]]')                         AS games_with_cover,
                    COUNT(*) FILTER (WHERE COALESCE(game.aws_background_url, game.igdb_background_url, '')
                        ~
                                           '[^[:space:]]')                                                                    AS games_with_background,
                    COUNT(*) FILTER (WHERE COALESCE(game.aws_background_url, game.igdb_background_url, '')
                        !~
                                           '[^[:space:]]')                                                                    AS games_without_background,
                    COUNT(*) FILTER (WHERE EXISTS (SELECT 1
                                                   FROM igdb.screenshot screenshot
                                                   WHERE screenshot.game_id = game.id
                                                     AND COALESCE(screenshot.aws_url, screenshot.igdb_url) ~ '[^[:space:]]')) AS games_with_screenshots
             FROM igdb.game game
                      JOIN matched_games matched ON matched.matched_game_id = game.id)
SELECT 1                                 AS id,
       suite_counts.*,
       game_counts.*,
       (SELECT COUNT(*) FROM psn.player) AS players,
       (SELECT COUNT(*) FROM psn.trophy) AS trophies
FROM suite_counts
         CROSS JOIN game_counts;
--rollback DROP VIEW metadata.catalog_statistics;
