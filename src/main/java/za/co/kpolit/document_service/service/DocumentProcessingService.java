package za.co.kpolit.document_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.model.DocumentEntity;
import za.co.kpolit.document_service.model.DocumentEntity.Status;
import za.co.kpolit.document_service.repository.DocumentRepository;

import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class DocumentProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    private final DocumentRepository documentRepository;
    private final TextExtractorService extractor;
    private final OpenAiWrapper ai;
    private final StorageService storageService;

    public DocumentProcessingService(DocumentRepository documentRepository,
                                     TextExtractorService extractor,
                                     OpenAiWrapper ai,
                                     StorageService storageService) {
        this.documentRepository = documentRepository;
        this.extractor = extractor;
        this.ai = ai;
        this.storageService = storageService;
    }

    @Async("documentProcessorExecutor")
    public void processAsync(UUID documentId) {
        DocumentEntity doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null) return;

        try {
            doc.setStatus(Status.PROCESSING);
            documentRepository.save(doc);

            logger.info("Extracting text...: ");

            // extract text
            String text = extractor.extractTextFromBlob(doc.getBlobName());


            logger.info("Extracted text: " + text);
            logger.info("getStoragePath: " + doc.getBlobName());

            logger.info("AI Processing Summary +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            // consider chunking for large text (omitted here)
            String summary = ai.summarize(text);
            logger.info("Summary: " + summary);

            logger.info("AI Processing Quiz +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            String quizJson = ai.generateQuiz(text, 5);
            logger.info("quizJson: " + quizJson);

            logger.info("AI Processing  Test +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            String testJson = ai.generateTest(text, 10);
            logger.info("testJson: " + testJson);



            doc.setSummary(summary);
            doc.setQuizJson(quizJson);
            doc.setTestJson(testJson);
            doc.setStatus(Status.PROCESSED);
            documentRepository.save(doc);
        }  catch (Exception e) {
            logger.info("ERROR:   :" + e.getMessage());
            doc.setStatus(Status.FAILED);
            documentRepository.save(doc);
        }
    }
}
