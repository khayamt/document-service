package za.co.kpolit.document_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import za.co.kpolit.document_service.enums.StorageType;

@Component
public class StorageConfig {

    @Value("${app.storage.type:AZURE}")
    private StorageType storageType;

    public StorageType getStorageType() {
        return storageType;
    }
}
