package za.co.kpolit.document_service.controller;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.kpolit.document_service.dto.DocumentResponseDto;
import za.co.kpolit.document_service.enums.StorageType;
import za.co.kpolit.document_service.kafka.event.DocumentUploadedEvent;
import za.co.kpolit.document_service.kafka.producer.DocumentEventProducer;
import za.co.kpolit.document_service.model.DocumentEntity;
import za.co.kpolit.document_service.repository.DocumentRepository;
import za.co.kpolit.document_service.service.DocumentProcessingService;
import za.co.kpolit.document_service.dto.TextExtractionResult;
import za.co.kpolit.document_service.service.StorageService;
import za.co.kpolit.document_service.service.TextExtractionService;
import za.co.kpolit.document_service.model.DocumentMetadataEntity;
import za.co.kpolit.document_service.repository.DocumentMetadataRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
//@CrossOrigin(origins = "*")
public class DocumentController {
    private final DocumentRepository documentRepository;
    private final DocumentProcessingService processingService;
    private final StorageService storageService;
    private final DocumentEventProducer eventProducer;
    private final TextExtractionService textExtractionService;
    private final DocumentMetadataRepository documentMetadataRepository;

    public DocumentController(DocumentRepository documentRepository,
                              DocumentProcessingService processingService,
                              @Value("${app.storage.type:AWS}") String  storageType,
                              @Autowired(required = false) java.util.Map<String, StorageService> storageServices,
                              DocumentEventProducer eventProducer,
                              TextExtractionService textExtractionService,
                              DocumentMetadataRepository documentMetadataRepository) {
        this.eventProducer = eventProducer;
        this.documentRepository = documentRepository;
        this.textExtractionService = textExtractionService;
        this.processingService = processingService;
        this.documentMetadataRepository = documentMetadataRepository;
        // Pick the correct storage service based on app.storage.type
        this.storageService = storageServices.get(storageType.toUpperCase());
        if (this.storageService == null) {
            throw new IllegalArgumentException("No storage service found for type: " + storageType);
        }

        log.info("Using storage service: {}", storageType);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "ownerId", required = false) UUID ownerId,
                                            @RequestParam(value = "version", defaultValue = "1") int version) {
        // blobName;
        log.info("Uploading : {} , ownerId: {} ", file.getName(), ownerId);
        try {
            // 1️⃣ Upload original document
            String  blobName = storageService.uploadFile(file);
            // 2️⃣ Persist base document record
            DocumentEntity doc = new DocumentEntity();
            doc.setOriginalFileName(file.getOriginalFilename());
            doc.setMimeType(file.getContentType());
            doc.setBlobName(blobName);
            doc.setOwnerId(ownerId != null ? ownerId : UUID.fromString("00000000-0000-0000-0000-000000000000"));
            doc.setStatus(DocumentEntity.Status.UPLOADED);
            documentRepository.save(doc);

            // 3️⃣ Extract text
            TextExtractionResult extractionResult = textExtractionService.extractText(blobName);

            // 4️⃣ 🔥 STORE METADATA
            DocumentMetadataEntity metadata = new DocumentMetadataEntity();
            metadata.setDocumentId(doc.getId());
            //metadata.setOriginalFileName(file.getOriginalFilename());
            metadata.setMimeType(file.getContentType());
            metadata.setVersion(version);
            //metadata.setBlobName(blobName);
            //metadata.setStorageType(StorageType.AWS.name()); // or injected value
            metadata.setExtractedTextKey(extractionResult.getExtractedTextLocation());
            metadata.setExtractedTextSize(extractionResult.getExtractedTextSize());
            //metadata.setOwnerId(doc.getOwnerId());
            //metadata.setCreatedAt(java.time.Instant.now());

            documentMetadataRepository.save(metadata);

            // process async
            //processingService.processAsync(doc.getId());
            // 5️⃣ 🔥 Publish Kafka event
            DocumentUploadedEvent event = new DocumentUploadedEvent(
                    version,
                    doc.getId(),
                    blobName,
                    ownerId,
                    file.getContentType(),
                    java.time.Instant.now(),
                    extractionResult.getExtractedTextLocation(),
                    extractionResult.getExtractedTextSize()
            );

            eventProducer.publishDocumentUploaded(event);

            return ResponseEntity.ok(Map.of(
                    "documentId", event.documentId(),
                    "blobName", blobName,
                    "extractedTextSize", extractionResult.getExtractedTextSize()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to store file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getDocument(@PathVariable("id") UUID id) {
        Optional<DocumentEntity> opt = documentRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        DocumentEntity doc = opt.get();
        DocumentResponseDto dto = new DocumentResponseDto();
        dto.setId(doc.getId());
        dto.setStatus(doc.getStatus().name());
        dto.setShortSummary(doc.getShortSummary());
        dto.setMediumSummary(doc.getMediumSummary());
        dto.setLongSummary(doc.getLongSummary());
        dto.setKeyPoints(doc.getKeyPoints());
        dto.setFlashcards(doc.getFlashcards());
        dto.setQuizQuestions(doc.getQuizQuestions());
        dto.setTestJson(doc.getTestJson());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable("id") UUID id) {
        return documentRepository.findById(id)
                .map(d -> ResponseEntity.ok(d.getStatus().name()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
