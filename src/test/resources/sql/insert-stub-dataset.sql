-- ============================================================================
-- Test dataset for TrophyQuest
-- ============================================================================
-- UUID convention, to be readable:
--   - Players  : 0000000X-0000-0000-0000-000000000000
--   - Suites   : a000000X-0000-0000-0000-000000000000
--   - Groups   : b000000X-0000-0000-0000-000000000000
--   - Trophies : c000000X-000Y-000Z-0000-000000000000 (X = suite, Y = trophy rank, Z = group rank)
--
-- Scenario :
--  SUITES:
--   - Astro Bot            -> automatically linked to an IGDB game (AUTO_MATCH)
--   - The Last of Us II    -> manually linked to an IGDB game (MANUAL_MATCH)
--   - Tomb Raider          -> uncertain matching, waiting for association (UNCERTAIN)
--   - Obscure              -> no IGDB game found (NO_MATCH_FOUND)
--   - Control Resonant     -> automatically linked to an IGDB game (AUTO_MATCH)
--  PLAYERS:
--   - Lofrings      -> Has finished Astro Bot. Recently, has played TLOU2 and Control Resonant.
--   - Casual        -> Has started Astro Bot and Tomb Raider. Recently, has played Control Resonant.
-- ============================================================================


-- ============================================================================
-- 1. Players
-- ============================================================================
INSERT INTO psn.player (id, pseudo, psn_avatar_url)
VALUES ('00000001-0000-0000-0000-000000000000', 'Lofrings', 'lofrings.png'),
       ('00000002-0000-0000-0000-000000000000', 'Casual', 'casual.png');


-- ============================================================================
-- 2. Suites
-- ============================================================================
INSERT INTO psn.suite (id, name, psn_image_url, platforms)
VALUES ('a0000001-0000-0000-0000-000000000000', 'Astro Bot', 'astro-bot.png', '{PS5}'),
       ('a0000002-0000-0000-0000-000000000000', 'The Last of Us Part II', 'tlou2.png', '{PS4,PS5}'),
       ('a0000003-0000-0000-0000-000000000000', 'Tomb Raider', 'tr.png', '{PS4}'),
       ('a0000004-0000-0000-0000-000000000000', 'Obscure', NULL, '{PS3,PSVITA}'),
       ('a0000005-0000-0000-0000-000000000000', 'Control Resonant', 'control.png', '{PS5}');



-- ============================================================================
-- 3. Trophy groups
-- One group per suite, plus a DLC for The Last of Us II.
-- ============================================================================
INSERT INTO psn.group (id, suite_id, psn_id, name)
VALUES ('b0000001-0000-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', 'default', 'Astro Bot'),
       ('b0000002-0000-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000', 'default', 'TLOU 2 Trophies'),
       ('b0000003-0000-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000', '001', 'No Return (DLC)'),
       ('b0000004-0000-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000', 'default', 'Tomb Raider'),
       ('b0000005-0000-0000-0000-000000000000', 'a0000004-0000-0000-0000-000000000000', 'default', 'Obscure'),
       ('b0000006-0000-0000-0000-000000000000', 'a0000004-0000-0000-0000-000000000000', 'default', 'Control Resonant');


-- ============================================================================
-- 4. Trophies
-- ============================================================================
-- Astro Bot: 4 trophies (1 platinum, 1 gold, 1 silver, 1 bronze)
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000001-0001-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000', 1, 'Astro Platinum', 'Get all trophies', 'platinum', false,
        'astro-platinum.png'),
       ('c0000001-0002-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000', 2, 'Héros galactique', 'Finish the main story', 'gold', false,
        'astro-gold.png'),
       ('c0000001-0003-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000', 3, 'Collectionneur', 'Find 100 bots', 'silver', false,
        'astro-silver.png'),
       ('c0000001-0004-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000',
        'b0000001-0000-0000-0000-000000000000', 4, 'Premier pas', 'Finish level 1', 'bronze', false,
        'astro-bronze.png');

-- The Last of Us Part II: 3 trophies for main game and 1 DLC
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000002-0001-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000', 1, 'TLOU2 Platinum', 'Get all trophies', 'platinum', false,
        'tlou2-platinum.png'),
       ('c0000002-0002-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000', 2, 'Survivor', 'Finish the story', 'gold', true,
        'tlou2-gold.png'),
       ('c0000002-0003-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000',
        'b0000002-0000-0000-0000-000000000000', 3, 'Upgrade', 'Upgrade a weapin', 'bronze', false,
        'tlou2-bronze.png'),
       ('c0000002-0001-0001-0000-000000000000', 'a0000002-0000-0000-0000-000000000000',
        'b0000003-0000-0000-0000-000000000000', 1, 'No return', 'Finished No Return', 'silver', false,
        'tlou2-dlc-silver.png');

-- Tomb Raider: 2 trophies (no platinum)
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000003-0001-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000',
        'b0000004-0000-0000-0000-000000000000', 1, 'Fished Peru', 'Finished all levels in Peru', 'bronze',
        false, 'tr-bronze.png'),
       ('c0000003-0002-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000',
        'b0000004-0000-0000-0000-000000000000', 2, 'Finished story', 'Finished the story', 'gold', false,
        'tr-gold.png');

-- Obscure: 1 trophy
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000004-0001-0000-0000-000000000000', 'a0000004-0000-0000-0000-000000000000',
        'b0000005-0000-0000-0000-000000000000', 1, 'Mysterious', 'What a mysterious trophy', 'bronze', true,
        NULL);

-- Control Resonant: 3 trophies
INSERT INTO psn.trophy (id, suite_id, group_id, rank, title, description, color, is_hidden, psn_icon_url)
VALUES ('c0000005-0001-0000-0000-000000000000', 'a0000005-0000-0000-0000-000000000000',
        'b0000006-0000-0000-0000-000000000000', 1, 'Total Control', 'Get all Control trophies', 'platinum', true,
        'control-platinum.png'),
       ('c0000005-0002-0000-0000-000000000000', 'a0000005-0000-0000-0000-000000000000',
        'b0000006-0000-0000-0000-000000000000', 2, 'Story finished', 'Finished the story', 'gold', true,
        'control-gold.png'),
       ('c0000005-0003-0000-0000-000000000000', 'a0000005-0000-0000-0000-000000000000',
        'b0000006-0000-0000-0000-000000000000', 3, 'Resonant', 'Has defeated a resonant', 'bronze', true,
        'control-bronze.png');;


-- ============================================================================
-- 5. Played suites
-- Lofrings: Astro bot and Obscure (in 2025), TLOU2 and Control Resonant (recently).
-- Casual: Astro bot and Tomb Raider (in 2025), Control Resonnant (recently).
-- ============================================================================
INSERT INTO psn.played_suite (player_id, suite_id, last_played_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', '2025-09-20T21:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'a0000004-0000-0000-0000-000000000000', '2025-12-15T16:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'a0000002-0000-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '2 days'),
       ('00000001-0000-0000-0000-000000000000', 'a0000005-0000-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '1 day'),
       ('00000002-0000-0000-0000-000000000000', 'a0000001-0000-0000-0000-000000000000', '2025-08-11T14:00:00Z'),
       ('00000002-0000-0000-0000-000000000000', 'a0000003-0000-0000-0000-000000000000', '2025-10-12T17:00:00Z'),
       ('00000002-0000-0000-0000-000000000000', 'a0000005-0000-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '1 day');


-- ============================================================================
-- 6. Earned trophies
-- ============================================================================
-- Lofrings: all trophies for Astro Bot (all trophies)
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'c0000001-0004-0000-0000-000000000000', '2025-09-01T10:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'c0000001-0003-0000-0000-000000000000', '2025-09-10T15:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'c0000001-0002-0000-0000-000000000000', '2025-09-18T20:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'c0000001-0001-0000-0000-000000000000', '2025-09-20T21:00:00Z');

-- Lofrings - The Last of Us Part II (all trophies, one recent)
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'c0000002-0001-0001-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '2 days'),
       ('00000001-0000-0000-0000-000000000000', 'c0000002-0003-0000-0000-000000000000', '2025-12-23T12:00:00Z'),
       ('00000001-0000-0000-0000-000000000000', 'c0000002-0002-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '2 days'),
       ('00000001-0000-0000-0000-000000000000', 'c0000002-0001-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '2 days');

-- Lofrings - Control Resonant (one trophy, recent)
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000001-0000-0000-0000-000000000000', 'c0000005-0003-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Casual - Control Resonant (one trophy, recent)
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000002-0000-0000-0000-000000000000', 'c0000005-0003-0000-0000-000000000000',
        CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Lofrings: all trophies for Astro Bot (all trophies)
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000002-0000-0000-0000-000000000000', 'c0000001-0004-0000-0000-000000000000', '2025-12-01T10:00:00Z');

-- Casual - Tomb Raider
INSERT INTO psn.earned_trophy (player_id, trophy_id, earned_at)
VALUES ('00000002-0000-0000-0000-000000000000', 'c0000003-0002-0000-0000-000000000000', '2025-12-01T12:00:00Z');

-- ============================================================================
-- 7. IGDB referential (igdb.collection, igdb.company, igdb.game, igdb.screenshot)
-- ============================================================================
-- Collections
INSERT INTO igdb.collection (id, name)
VALUES (1, 'Astro'),
       (2, 'The Last of Us');

-- Companies
INSERT INTO igdb.company (id, name, country_code)
VALUES (10, 'Sony Interactive Entertainment', 392),
       (11, 'Naughty Dog', 840);

-- IGDB games:
--  - 1001, 1002 and 1005 are linked to a PSN suite
--  - 1003 is only a candidate for Tomb Raider
--  - 1004 is not linked to any suite
INSERT INTO igdb.game (id, name, summary, game_type, release_date, genres, themes, platforms, igdb_cover_url,
                       publisher_id, developer_id, collection_id, official_website)
VALUES (1001, 'Astro Bot', 'A platform game with Astro Bot', 'main_game', '2024-09-06', '{Platform}',
        '{Action,Kids}', '{PS5}', 'https://igdb.example.com/covers/astro-bot.png',
        10, 10, 1, 'https://astrobot.example.com'),
       (1002, 'The Last of Us Part II', 'Post-apocalyptic adventure with Ellie', 'main_game', '2020-06-19',
        '{Adventure,Shooter}', '{Survival,Drama}', '{PS4,PS5}', 'https://igdb.example.com/covers/tlou2.png',
        10, 11, 2, 'https://tlou2.example.com'),
       (1003, 'Tomb Raider', 'Lara Croft Adventure', 'main_game', '2016-02-26',
        '{Simulator,RPG}', '{Sandbox}', '{PC,PS4,Switch}', 'https://igdb.example.com/covers/tomb-raider.png',
        NULL, NULL, NULL, 'https://tombraider.example.com'),
       (1004, 'Orphan game', 'An orphan game in database', 'main_game', '2010-01-01',
        '{Puzzle}', NULL, '{PC}', NULL, NULL, NULL, NULL, NULL),
       (1005, 'Control Resonant', 'Save Manhattan from the Hiss', 'main_game', '2026-09-24', '{Platform}',
        '{Action}', '{PS5}', 'https://igdb.example.com/covers/control.png',
        10, NULL, NULL, 'https://control.example.com');

-- Astrobot screenshots
INSERT INTO igdb.screenshot (id, game_id, igdb_url)
VALUES ('screenshot-astro-1', 1001, 'https://igdb.example.com/screenshots/astro-bot-1.png'),
       ('screenshot-astro-2', 1001, 'https://igdb.example.com/screenshots/astro-bot-2.png');


-- ============================================================================
-- 8. PSN / IGDB associations
-- ============================================================================
INSERT INTO app.igdb_matching (suite_id, matched_game_id, status)
VALUES ('a0000001-0000-0000-0000-000000000000', 1001, 'AUTO_MATCH'),     -- Astro Bot: matched automatically
       ('a0000002-0000-0000-0000-000000000000', 1002, 'MANUAL_MATCH'),   -- TLOU2: manually matched
       ('a0000003-0000-0000-0000-000000000000', NULL, 'UNCERTAIN'),      -- Tomb Raider: waiting for confirmation
       ('a0000004-0000-0000-0000-000000000000', NULL, 'NO_MATCH_FOUND'), -- Obscure: no match
       ('a0000005-0000-0000-0000-000000000000', 1005, 'AUTO_MATCH') -- Control: matched automatically
;


-- ============================================================================
-- 9. Candidates with scores
-- ============================================================================
-- Astro Bot
INSERT INTO app.igdb_candidate (suite_id, candidate_id, score, status)
VALUES ('a0000001-0000-0000-0000-000000000000', 1002, 0.97, 'VALIDATED'),
       ('a0000001-0000-0000-0000-000000000000', 1004, 0.21, 'REJECTED');

-- Tomb Raider
INSERT INTO app.igdb_candidate (suite_id, candidate_id, score, status)
VALUES ('a0000003-0000-0000-0000-000000000000', 1003, 0.88, 'PENDING'),
       ('a0000003-0000-0000-0000-000000000000', 1004, 0.35, 'PENDING');

-- Control Resonant
INSERT INTO app.igdb_candidate (suite_id, candidate_id, score, status)
VALUES ('a0000005-0000-0000-0000-000000000000', 1005, 1.0, 'VALIDATED');
