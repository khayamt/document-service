package za.co.kpolit.document_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.kpolit.document_service.dto.DocumentResponseDto;
import za.co.kpolit.document_service.model.DocumentEntity;
import za.co.kpolit.document_service.repository.DocumentRepository;
import za.co.kpolit.document_service.service.DocumentProcessingService;
import za.co.kpolit.document_service.service.StorageService;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@Validated
@CrossOrigin(origins = "*")
public class DocumentController {

    private final StorageService storageService;
    private final DocumentRepository documentRepository;
    private final DocumentProcessingService processingService;

    public DocumentController(StorageService storageService,
                              DocumentRepository documentRepository,
                              DocumentProcessingService processingService) {
        this.storageService = storageService;
        this.documentRepository = documentRepository;
        this.processingService = processingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "ownerId", required = false) UUID ownerId) {
        log.info("Uploading : {} , ownerId: {} ", file.getName(), ownerId);
        try {
            String blobName = storageService.store(file);

            DocumentEntity doc = new DocumentEntity();
            doc.setOriginalFileName(file.getOriginalFilename());
            doc.setMimeType(file.getContentType());
            //doc.setStoragePath(storagePath);
            doc.setBlobName(blobName);
            doc.setOwnerId(ownerId != null ? ownerId : UUID.fromString("00000000-0000-0000-0000-000000000000"));
            doc.setStatus(DocumentEntity.Status.UPLOADED);
            documentRepository.save(doc);

            // process async
            processingService.processAsync(doc.getId());

            return ResponseEntity.ok(doc.getId());
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
        dto.setSummary(doc.getSummary());
        dto.setQuizJson(doc.getQuizJson());
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
