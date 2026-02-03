package za.co.kpolit.document_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.dto.TextExtractionResult;
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
    private final TextExtractionService extractor;
    private final OpenAiWrapper ai;
    private final AzureStorageService storageService;

    public DocumentProcessingService(DocumentRepository documentRepository,
                                     TextExtractionService extractor,
                                     OpenAiWrapper ai,
                                     AzureStorageService storageService) {
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
            TextExtractionResult textExtractionResult = extractor.extractText(doc.getBlobName());


            logger.info("Extracted text: " + textExtractionResult.getExtractedTextLocation());
            logger.info("getStoragePath: " + doc.getBlobName());

            logger.info("AI Processing Summary +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            // consider chunking for large text (omitted here)
            String summary = ai.summarize(textExtractionResult.getExtractedTextLocation());
            logger.info("Summary: " + summary);

            logger.info("AI Processing Quiz +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            String quizJson = ai.generateQuiz(textExtractionResult.getExtractedTextLocation(), 5);
            logger.info("quizJson: " + quizJson);

            logger.info("AI Processing  Test +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            String testJson = ai.generateTest(textExtractionResult.getExtractedTextLocation(), 10);
            logger.info("testJson: " + testJson);



            doc.setShortSummary(summary);
            //doc.setQuiz(quizJson);
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
