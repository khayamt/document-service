package za.co.kpolit.document_service.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobStorageException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TextExtractorService {
    private static final Logger logger = LoggerFactory.getLogger(TextExtractorService.class);

    private final Tika tika = new Tika();
    private final BlobServiceClient blobServiceClient;
    private final String containerName;

    public TextExtractorService(
            @Value("${app.storage.azure-connection-string:}") String azureConnString,
            @Value("${app.storage.azure-container-name:documents}") String containerName
    ) {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureConnString)
                .buildClient();
        this.containerName = containerName;
    }

    /**
     * Extracts text from a blob stored in Azure Blob Storage.
     * @param blobName the name of the file inside the container (e.g., "myfile.pdf")
     * @return the extracted text
     */
    public String extractTextFromBlob(String blobName) {
        logger.info("extractTextFromBlob...: " + blobName);
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);

            if (!blobClient.exists()) {
                throw new RuntimeException("Blob not found: " + blobName);
            }

            try (InputStream inputStream = blobClient.openInputStream()) {
                return tika.parseToString(inputStream);
            }
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Failed to extract text from blob: " + blobName, e);
        } catch (BlobStorageException e) {
            throw new RuntimeException("Azure Blob Storage error: " + e.getMessage(), e);
        }
        catch (Exception e)
        {
            logger.info("Error...: " + e.getMessage());
        }
        return "";
    }
}
