--liquibase formatted sql

--changeset trophyquest:009-psn-reconciliation/01-add-player-sync-state
ALTER TABLE psn.player
    ADD COLUMN last_synced_title_at           timestamptz,
    ADD COLUMN last_reconciled_at             timestamptz,
    ADD COLUMN last_reconciliation_attempt_at timestamptz;

--changeset trophyquest:009-psn-reconciliation/02-index-reconciliation runInTransaction:false
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_played_suite_player_last_played_at
    ON psn.played_suite (player_id, last_played_at DESC);

--changeset trophyquest:009-psn-reconciliation/03-index-trophies-by-suite runInTransaction:false
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_psn_trophy_suite_id ON psn.trophy (suite_id);
