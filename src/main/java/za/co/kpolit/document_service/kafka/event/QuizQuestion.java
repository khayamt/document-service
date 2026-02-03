package za.co.kpolit.document_service.kafka.event;

import java.util.List;

public record QuizQuestion(
        String question,
        List<String> options,
        int correctOptionIndex
) {}
