package za.co.kpolit.document_service.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record DocumentUploadedEvent(
        UUID documentId,
        String blobName,
        UUID ownerId,
        String mimeType,
        Instant uploadedAt
) {}
