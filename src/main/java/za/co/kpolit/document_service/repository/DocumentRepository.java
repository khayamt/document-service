package za.co.kpolit.document_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.kpolit.document_service.model.DocumentEntity;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByOwnerId(UUID ownerId);
}
