package za.co.kpolit.document_service.kafka.event;

public record Flashcard(
        String question,
        String answer
) {}
