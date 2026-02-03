package za.co.kpolit.document_service.service;

import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.*;

@Slf4j
@Service("AZURE")
public class AzureStorageService implements StorageService {
    private final String azureConnString;
    private final String containerName;

    public AzureStorageService(
            @Value("${app.storage.azure-connection-string:}") String azureConnString,
            @Value("${app.storage.azure-container-name:documents}") String containerName) {
        this.azureConnString = azureConnString;
        this.containerName = containerName;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (azureConnString != null && !azureConnString.isBlank()) {
            return storeToAzure(file);
        } else {
            return storeLocally(file);
        }
    }

    private String storeLocally(MultipartFile file) throws IOException {
        Path folder = Paths.get("uploaded-documents");
        Files.createDirectories(folder);
        Path target = folder.resolve(System.currentTimeMillis() + "_" + file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.toAbsolutePath().toString();
    }

    private String storeToAzure(MultipartFile file) throws IOException {
        log.info("storeToAzure " + containerName);
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureConnString)
                .buildClient();

        BlobContainerClient containerClient;
        if (!blobServiceClient.getBlobContainerClient(containerName).exists()) {
            containerClient = blobServiceClient.createBlobContainer(containerName);
        } else {
            containerClient = blobServiceClient.getBlobContainerClient(containerName);
        }

        String blobName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        try (InputStream is = file.getInputStream()) {
            blobClient.upload(is, file.getSize(), true);
        }
        // return blob URL
       // return blobClient.getBlobUrl();
        return blobName;
    }
    
    @Override
    public String readText(String key) {
        return "TOBEIMPLEMENTED";
    }
}
