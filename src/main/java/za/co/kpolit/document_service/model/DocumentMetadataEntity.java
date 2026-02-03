package za.co.kpolit.document_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "document_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMetadataEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID documentId;

    @Column(nullable = false)
    private String extractedTextKey;

    @Column(nullable = false)
    private Long extractedTextSize;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Integer version;
}
