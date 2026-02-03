package za.co.kpolit.document_service.dto;

import java.util.UUID;

public record DocumentMetadataResponse(
        UUID documentId,
        String extractedTextKey,
        Long extractedTextSize,
        String mimeType,
        Integer version
) {}
