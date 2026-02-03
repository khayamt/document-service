package za.co.kpolit.document_service.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.kpolit.document_service.dto.DocumentMetadataResponse;
import za.co.kpolit.document_service.service.DocumentMetadataService;
import za.co.kpolit.document_service.service.DocumentTextService;
import org.springframework.http.MediaType;

import java.util.UUID;

@RestController
@RequestMapping("/internal/documents")
@RequiredArgsConstructor
public class InternalDocumentController {

    private final DocumentMetadataService metadataService;
    private final DocumentTextService documentTextService;

    @GetMapping("/{documentId}/metadata")
    public ResponseEntity<DocumentMetadataResponse> getMetadata(
            @PathVariable UUID documentId
    ) {
        return ResponseEntity.ok(metadataService.getMetadata(documentId));
    }
    @GetMapping(value = "/{documentId}/extracted-text",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getExtractedText(
            @PathVariable UUID documentId
    ) {
        String text = documentTextService.getExtractedText(documentId);
        return ResponseEntity.ok(text);
    }
}
