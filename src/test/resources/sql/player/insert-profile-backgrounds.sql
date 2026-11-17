-- Distinct games make the selected background depend on play dates, not game IDs.
INSERT INTO igdb.game (id, name, igdb_background_url)
VALUES (1002, 'Latest game', 'latest-background.png'),
       (1003, 'Older game', 'older-background.png'),
       (1004, 'Another player game', 'other-player-background.png');

UPDATE app.igdb_matching SET matched_game_id = 1002
WHERE suite_id = 'a0000003-0000-0000-0000-000000000000';
UPDATE app.igdb_matching SET matched_game_id = 1003
WHERE suite_id = 'a0000002-0000-0000-0000-000000000000';

INSERT INTO psn.suite (id, name, platforms)
VALUES ('a0000004-0000-0000-0000-000000000000', 'Another player suite', '{PS5}');
INSERT INTO app.igdb_matching (suite_id, matched_game_id, status)
VALUES ('a0000004-0000-0000-0000-000000000000', 1004, 'AUTO_MATCH');
INSERT INTO psn.played_suite (player_id, suite_id, last_played_at)
VALUES ('00000002-0000-0000-0000-000000000000', 'a0000004-0000-0000-0000-000000000000', '2026-02-01T10:00:00Z');
