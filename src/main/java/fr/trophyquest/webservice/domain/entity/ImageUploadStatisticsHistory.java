package fr.trophyquest.webservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "image_upload_statistics_history", schema = "metadata")
@IdClass(ImageUploadStatisticsHistory.Key.class)
@Getter
@Immutable
public class ImageUploadStatisticsHistory {
    @Id
    private Instant sampledAt;

    @Id
    private String imageType;

    private long pendingCount;

    public record Key(Instant sampledAt, String imageType) implements Serializable {
    }
}
