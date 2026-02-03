package za.co.kpolit.document_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.dto.DocumentMetadataResponse;
import za.co.kpolit.document_service.model.DocumentMetadataEntity;
import za.co.kpolit.document_service.repository.DocumentMetadataRepository;
import za.co.kpolit.document_service.exception.DocumentNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentMetadataService {

    private final DocumentMetadataRepository repository;

    public DocumentMetadataResponse getMetadata(UUID documentId) {
        DocumentMetadataEntity metadata = repository.findByDocumentId(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException("Metadata not found for document " + documentId)
                );

        return new DocumentMetadataResponse(
                metadata.getDocumentId(),
                metadata.getExtractedTextKey(),
                metadata.getExtractedTextSize(),
                metadata.getMimeType(),
                metadata.getVersion()
        );
    }
}
