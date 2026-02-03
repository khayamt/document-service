package za.co.kpolit.document_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.model.DocumentMetadataEntity;
import za.co.kpolit.document_service.repository.DocumentMetadataRepository;
import za.co.kpolit.document_service.service.StorageService;
import za.co.kpolit.document_service.exception.DocumentNotFoundException;


import java.util.UUID;
import java.util.Map;

@Service
public class DocumentTextService {

    private final DocumentMetadataRepository metadataRepository;
    private final StorageService storageService;
    
    public DocumentTextService(
            DocumentMetadataRepository metadataRepository,
            Map<String, StorageService> storageServices,
            @Value("${app.storage.type:AWS}") String  storageType
    ) {
        this.metadataRepository = metadataRepository;
        this.storageService = storageServices.get(storageType.toUpperCase());
        if (this.storageService == null) {
            throw new IllegalArgumentException("No storage service found for type: " + storageType);
        }
    }

    public String getExtractedText(UUID documentId) {
        DocumentMetadataEntity metadata = metadataRepository.findByDocumentId(documentId)
                .orElseThrow(() ->
                        new DocumentNotFoundException(
                                "Metadata not found for document " + documentId
                        )
                );

        return storageService.readText(
                metadata.getExtractedTextKey()
        );
    }
}
