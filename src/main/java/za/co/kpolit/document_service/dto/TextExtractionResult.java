package za.co.kpolit.document_service.dto;

import java.time.Instant;
import java.util.Objects;

public final class TextExtractionResult {

    private final String extractedTextLocation;
    private final long extractedTextSize;
    private final Instant extractedAt;

    public TextExtractionResult(
            String extractedTextLocation,
            long extractedTextSize,
            Instant extractedAt
    ) {
        this.extractedTextLocation = Objects.requireNonNull(
                extractedTextLocation,
                "extractedTextLocation must not be null"
        );
        this.extractedTextSize = extractedTextSize;
        this.extractedAt = Objects.requireNonNull(
                extractedAt,
                "extractedAt must not be null"
        );
    }

    public String getExtractedTextLocation() {
        return extractedTextLocation;
    }

    public long getExtractedTextSize() {
        return extractedTextSize;
    }

    public Instant getExtractedAt() {
        return extractedAt;
    }

    @Override
    public String toString() {
        return "TextExtractionResult{" +
                "extractedTextLocation='" + extractedTextLocation + '\'' +
                ", extractedTextSize=" + extractedTextSize +
                ", extractedAt=" + extractedAt +
                '}';
    }
}
