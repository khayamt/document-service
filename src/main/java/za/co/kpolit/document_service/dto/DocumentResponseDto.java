package za.co.kpolit.document_service.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import za.co.kpolit.document_service.kafka.event.Flashcard;
import za.co.kpolit.document_service.kafka.event.QuizQuestion;
//import za.co.kpolilt.kafka.event.

@Getter
@Setter
public class DocumentResponseDto {
    private UUID id;
    private int version;
    private String status;
    private String shortSummary;
    private String mediumSummary;
    private String longSummary;
    private List<String> keyPoints;
    private List<String> notes;
    private List<Flashcard> flashcards;
    private List<QuizQuestion> quizQuestions;
    private String testJson;

}
