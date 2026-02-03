package za.co.kpolit.document_service.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record DocumentUploadedEvent(
        int version,
        UUID documentId,
        String blobName,
        UUID ownerId,
        String mimeType,
        Instant uploadedAt,
        String extractedTextLocation,   // e.g. s3://bucket/docs/uuid.txt
        long extractedTextSize
) {}
