package fr.trophyquest.webservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "catalog_statistics", schema = "metadata")
@Getter
@Immutable
public class CatalogStatistics {
    @Id
    private Integer id;

    private long associatedSuites;
    private long unassociatedSuites;
    private long gamesWithCover;
    private long gamesWithBackground;
    private long gamesWithoutBackground;
    private long gamesWithScreenshots;
    private long players;
    private long trophies;
}
