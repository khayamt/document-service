package za.co.kpolit.document_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.kpolit.document_service.model.DocumentMetadataEntity;

import java.util.Optional;
import java.util.UUID;

public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadataEntity, UUID> {

    Optional<DocumentMetadataEntity> findByDocumentId(UUID documentId);
}
