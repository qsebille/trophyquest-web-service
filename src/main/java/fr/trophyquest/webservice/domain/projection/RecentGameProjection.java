package fr.trophyquest.webservice.domain.projection;

public interface RecentGameProjection {
    Long getGameId();

    String getTitle();

    String getCoverUrl();

    String getBackgroundUrl();

    Integer getNbRecentPlayers();
}