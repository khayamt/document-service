package za.co.kpolit.document_service.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import za.co.kpolit.document_service.kafka.event.Flashcard;
import za.co.kpolit.document_service.kafka.event.QuizQuestion;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

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
    private int version;
    private String originalFileName;

    private String storagePath; // blob path or local path
    private String blobName;

    private String mimeType;

    private OffsetDateTime uploadedAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.UPLOADED;

    //@Lob
    @Column(columnDefinition = "text")
    private String shortSummary;
    @Lob
    @Column(columnDefinition = "text")
    private String mediumSummary;
    @Lob
    @Column(columnDefinition = "text")
    private String longSummary;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> notes;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> keyPoints;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Flashcard> flashcards;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<QuizQuestion> quizQuestions;

    @Lob
    @Column(columnDefinition = "text")
    private String testJson;

    public enum Status {
        UPLOADED, PROCESSING, PROCESSED, FAILED
    }

}
