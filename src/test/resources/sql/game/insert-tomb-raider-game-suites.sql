-- ============================================================================
-- 1. Players
-- ============================================================================
INSERT INTO psn.player (id, pseudo, psn_avatar_url)
VALUES ('00000001-0000-0000-0000-000000000000', 'Lofrings', 'lofrings.png'),
       ('00000002-0000-0000-0000-000000000000', 'Another player', null);


-- ============================================================================
-- 2. Suites
-- ============================================================================
INSERT INTO psn.suite (id, name, psn_image_url, platforms)
VALUES ('a0000001-0000-0000-0000-000000000000', 'Tomb Raider 1', 'tr1.png', '{PS5,PS5}'),
       ('a0000002-0000-0000-0000-000000000000', 'Tomb Raider 2', 'tr2.png', '{PS4,PS5}'),
       ('a0000003-0000-0000-0000-000000000000', 'Tomb Raider 3', 'tr3.png', '{PS4,PS5}');


-- ============================================================================
-- 3. Trophy groups
-- One group per suite, plus a DLC for The Last of Us II.
-- ============================================================================
INSERT INTO psn.group (id, suite_id, psn_id, name)
VALUES ('b0000001-0000-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', 'default',
        'Tomb Raider 1'),
       ('b0000001-0001-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', '001',
        'Tomb Raider 1: Unfinished Business'),
       ('b0000002-0000-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000', 'default',
        'Tomb Raider 2'),
       ('b0000002-0001-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000', '001',
        'Tomb Raider 2: Golden Mask'),
       ('b0000003-0000-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000', 'default',
        'Tomb Raider 3'),
       ('b0000003-0001-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000', '001',
        'Tomb Raider 3: Lost Artifact');


-- ============================================================================
-- 4. Trophies
-- TR 1 : platinum + bronze trophies only
-- TR 2 : platinum + silver trophies only
-- TR 3 : platinum + gold trophies only
-- ============================================================================

-- Tomb Raider 1
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000001-0000-0000-0000-000000000000',
        'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000',
        1, 'Tomb Raider Platinum', 'Get all TR1 trophies', 'platinum',
        false, 'tr1-platinum.png'),
       ('c0000002-0000-0000-0000-000000000000',
        'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000',
        2, 'Finished Peru', 'Finish the Peru levels', 'bronze',
        false, 'tr1-peru.png'),
       ('c0000003-0000-0000-0000-000000000000',
        'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000',
        3, 'Finished Greece', 'Finish the Greece levels', 'bronze',
        false, 'tr1-greece.png'),
       ('c0000004-0000-0000-0000-000000000000',
        'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000',
        4, 'Finished Egypt', 'Finish the Egypt levels', 'bronze',
        false, 'tr1-egypt.png');

-- Tomb Raider 1: Unfinished Business
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000005-0000-0000-0000-000000000000',
        'a0000001-0000-0000-0000-000000000000',
        'b0000001-0001-0000-0000-000000000000',
        1, 'Finished Unfinished Business', 'Finish the Unfinished Business levels', 'bronze',
        false, 'tr1-unfinished-business.png');

-- Tomb Raider 2
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000006-0000-0000-0000-000000000000',
        'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000',
        1, 'Tomb Raider II Platinum', 'Get all TR2 trophies', 'platinum',
        false, 'tr2-platinum.png'),
       ('c0000007-0000-0000-0000-000000000000',
        'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000',
        2, 'Finished Venice', 'Finish the Venice levels', 'silver',
        false, 'tr2-venice.png'),
       ('c0000008-0000-0000-0000-000000000000',
        'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000',
        3, 'Finished Maria Doria', 'Finish the Maria Doria levels', 'silver',
        false, 'tr2-maria-doria.png'),
       ('c0000009-0000-0000-0000-000000000000',
        'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000',
        4, 'Finished Tibet', 'Finish the Tibet levels', 'silver',
        false, 'tr2-tibet.png');

-- Tomb Raider 2: Golden Mask
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000010-0000-0000-0000-000000000000',
        'a0000002-0000-0000-0000-000000000000',
        'b0000002-0001-0000-0000-000000000000',
        1, 'Finished Golden Mask', 'Finish the Golden Mask levels', 'silver',
        false, 'tr1-golden-mask.png');

-- Tomb Raider 3
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000011-0000-0000-0000-000000000000',
        'a0000003-0000-0000-0000-000000000000',
        'b0000003-0000-0000-0000-000000000000',
        1, 'Tomb Raider III Platinum', 'Get all TR3 trophies', 'platinum',
        false, 'tr3-platinum.png'),
       ('c0000012-0000-0000-0000-000000000000',
        'a0000003-0000-0000-0000-000000000000',
        'b0000003-0000-0000-0000-000000000000',
        2, 'Finished India', 'Finish the India levels', 'gold',
        false, 'tr3-india.png'),
       ('c0000013-0000-0000-0000-000000000000',
        'a0000003-0000-0000-0000-000000000000',
        'b0000003-0000-0000-0000-000000000000',
        3, 'Finished Nevada', 'Finish the Nevada levels', 'gold',
        false, 'tr3-nevada.png'),
       ('c0000014-0000-0000-0000-000000000000',
        'a0000003-0000-0000-0000-000000000000',
        'b0000003-0000-0000-0000-000000000000',
        4, 'Finished Antarctic', 'Finish the Antarctic levels', 'gold',
        false, 'tr3-antarctic.png');

-- Tomb Raider 3: Lost Artifact
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000015-0000-0000-0000-000000000000',
        'a0000003-0000-0000-0000-000000000000',
        'b0000003-0001-0000-0000-000000000000',
        1, 'Finished Lost Artifact', 'Finish the Lost Artifact levels', 'gold',
        false, 'tr3-lost-artifact.png');


-- ============================================================================
-- 5. Played suites
-- ============================================================================
INSERT INTO psn.played_suite (player_id, suite_id, last_played_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', '2025-09-20T21:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000', '2025-11-15T16:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000', '2025-12-15T18:00:00Z');


-- ============================================================================
-- 6. Earned trophies
-- ============================================================================
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'c0000002-0000-0000-0000-000000000000', '2025-09-01T10:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'c0000003-0000-0000-0000-000000000000', '2025-09-10T15:00:00Z');


-- ============================================================================
-- 7. IGDB referential (igdb.collection, igdb.company, igdb.game, igdb.screenshot)
-- ============================================================================
-- Collections
INSERT INTO igdb.collection (id, name)
VALUES (1, 'Tomb Raider');

-- Companies
INSERT INTO igdb.company (id, name, country_code)
VALUES (10, 'Aspyr', 392);

-- IGDB game
INSERT INTO igdb.game (id, name, summary, game_type, release_date, genres, themes, platforms, igdb_background_url,
                       publisher_id, developer_id, collection_id, official_website)
VALUES (1001, 'Tomb Raider', 'Lara Croft Adventure', 'bundle', '2024-02-14', '{Platform}',
        '{Action}', '{PS4,PS5}', 'tr-background.png',
        10, 10, 1, 'https://tomb-raider.example.com');

-- IGDB screenshots
INSERT INTO igdb.screenshot (id, game_id, igdb_url)
VALUES ('screenshot-1', 1001, 'https://igdb.example.com/screenshots/1.png'),
       ('screenshot-2', 1001, 'https://igdb.example.com/screenshots/2.png');


-- ============================================================================
-- 8. PSN / IGDB associations
-- ============================================================================
INSERT INTO app.igdb_matching (suite_id, matched_game_id, status)
VALUES ('a0000001-0000-0000-0000-000000000000', 1001, 'AUTO_MATCH'),
       ('a0000002-0000-0000-0000-000000000000', 1001, 'AUTO_MATCH'),
       ('a0000003-0000-0000-0000-000000000000', 1001, 'AUTO_MATCH');
;


-- ============================================================================
-- 9. Candidates with scores
-- ============================================================================
INSERT INTO app.igdb_candidate (suite_id, candidate_id, score, status)
VALUES ('a0000001-0000-0000-0000-000000000000', 1001, 1.0, 'VALIDATED'),
       ('a0000002-0000-0000-0000-000000000000', 1001, 1.0, 'VALIDATED'),
       ('a0000003-0000-0000-0000-000000000000', 1001, 1.0, 'VALIDATED');
