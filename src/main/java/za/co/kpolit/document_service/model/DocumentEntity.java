package za.co.kpolit.document_service.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID ownerId; // associate to the user (optional)

    private String originalFileName;

    private String storagePath; // blob path or local path
    private String blobName;

    private String mimeType;

    private OffsetDateTime uploadedAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.UPLOADED;

    @Lob
    @Column(columnDefinition = "text")
    private String summary;

    @Lob
    @Column(columnDefinition = "text")
    private String quizJson;

    @Lob
    @Column(columnDefinition = "text")
    private String testJson;

    public enum Status {
        UPLOADED, PROCESSING, PROCESSED, FAILED
    }

}
