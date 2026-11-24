package fr.trophyquest.webservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "image_upload_statistics", schema = "metadata")
@Getter
@Immutable
public class ImageUploadStatistics {
    @Id
    private String imageType;

    private long sourceCount;
    private long cachedCount;
    private long pendingCount;
}
