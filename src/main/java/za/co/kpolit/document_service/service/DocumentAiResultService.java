package za.co.kpolit.document_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.kpolit.document_service.kafka.event.DocumentProcessedEvent;
import za.co.kpolit.document_service.model.DocumentEntity;
import za.co.kpolit.document_service.repository.DocumentRepository;

@Service
@RequiredArgsConstructor
public class DocumentAiResultService {

    private final DocumentRepository documentRepository;

    @Transactional
    public void saveAiResult(DocumentProcessedEvent event) {

        DocumentEntity document = documentRepository.findById(event.documentId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Document not found: " + event.documentId()
                        )
                );

        document.setShortSummary(event.shortSummary());
        document.setMediumSummary(event.mediumSummary());
        document.setLongSummary(event.longSummary());
        document.setNotes(event.notes());
        document.setKeyPoints(event.keyPoints());
        document.setFlashcards(event.flashcards());
        document.setQuizQuestions(event.quiz());
        document.setVersion(event.version());
        document.setStatus(DocumentEntity.Status.PROCESSED);

        documentRepository.save(document);
    }
}
