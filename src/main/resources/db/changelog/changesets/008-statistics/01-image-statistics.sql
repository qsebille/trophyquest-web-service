--liquibase formatted sql
--changeset trophyquest:008-image-statistics-view
CREATE VIEW metadata.image_upload_statistics AS
WITH matched_games AS (SELECT DISTINCT matched_game_id
                       FROM app.igdb_matching
                       WHERE status IN ('AUTO_MATCH', 'MANUAL_MATCH')),
     types (image_type) AS (VALUES ('psn-player'),
                                   ('psn-suite'),
                                   ('psn-trophy'),
                                   ('igdb-cover'),
                                   ('igdb-background'),
                                   ('igdb-screenshot')),
     images AS (SELECT 'psn-player' AS image_type, psn_avatar_url AS source_url, aws_avatar_url AS aws_url
                FROM psn.player
                UNION ALL
                SELECT 'psn-suite', psn_image_url, aws_image_url
                FROM psn.suite
                UNION ALL
                SELECT 'psn-trophy', psn_icon_url, aws_icon_url
                FROM psn.trophy
                UNION ALL
                SELECT image.image_type, image.source_url, image.aws_url
                FROM igdb.game game
                         JOIN matched_games matched ON matched.matched_game_id = game.id
                         CROSS JOIN LATERAL (
                    VALUES ('igdb-cover', game.igdb_cover_url, game.aws_cover_url),
                           ('igdb-background', game.igdb_background_url, game.aws_background_url)
                    ) AS image(image_type, source_url, aws_url)
                UNION ALL
                SELECT 'igdb-screenshot', screenshot.igdb_url, screenshot.aws_url
                FROM igdb.screenshot screenshot
                         JOIN matched_games matched ON matched.matched_game_id = screenshot.game_id)
SELECT types.image_type,
       COUNT(images.image_type)                                       AS source_count,
       COUNT(images.aws_url)                                          AS cached_count,
       COUNT(images.image_type) FILTER (WHERE images.aws_url IS NULL) AS pending_count
FROM types
         LEFT JOIN images ON images.image_type = types.image_type AND images.source_url ~ '[^[:space:]]'
GROUP BY types.image_type;
--rollback DROP VIEW metadata.image_upload_statistics;

--changeset trophyquest:008-image-statistics-history
CREATE TABLE metadata.image_upload_statistics_history
(
    sampled_at    timestamptz NOT NULL,
    image_type    text        NOT NULL,
    pending_count bigint      NOT NULL CHECK (pending_count >= 0),
    PRIMARY KEY (sampled_at, image_type)
);
--rollback DROP TABLE metadata.image_upload_statistics_history;
