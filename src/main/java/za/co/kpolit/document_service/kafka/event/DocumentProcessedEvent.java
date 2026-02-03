package za.co.kpolit.document_service.kafka.event;

import java.util.List;
import java.util.UUID;

public record DocumentProcessedEvent(
        UUID documentId,
        int version,
        String shortSummary,
        String mediumSummary,
        String longSummary,
        List<String> notes,
        List<String> keyPoints,
        List<Flashcard> flashcards,
        List<QuizQuestion> quiz
        ) {
}
