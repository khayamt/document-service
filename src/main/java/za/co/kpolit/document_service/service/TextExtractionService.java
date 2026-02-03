package za.co.kpolit.document_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.dto.TextExtractionResult;
import za.co.kpolit.document_service.service.S3StorageService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextExtractionService {

    private final S3StorageService storageService;
    private final Tika tika = new Tika();

    /**
     * Extracts text from a document stored in S3 and stores the extracted text back in S3.
     *
     * @param sourceBlobName S3 key of the original document
     * @return TextExtractionResult metadata
     */
    public TextExtractionResult extractText(String sourceBlobName) {

        String extractedTextBlobName = buildExtractedTextBlobName(sourceBlobName);

        try (InputStream documentStream = storageService.download(sourceBlobName)) {

            // 1️⃣ Extract text using Tika
            String extractedText = tika.parseToString(documentStream);
            // Remove later
            //log.info("Extracted {} characters from document: {}", extractedText, sourceBlobName);

            // 2️⃣ Convert to bytes
            byte[] extractedBytes = extractedText.getBytes(StandardCharsets.UTF_8);

            // 3️⃣ Upload extracted text to S3
            storageService.upload(
                    extractedTextBlobName,
                    extractedBytes,
                    "text/plain; charset=utf-8"
            );

            // 4️⃣ Return result metadata
            return new TextExtractionResult(
                    extractedTextBlobName,
                    extractedBytes.length,
                    Instant.now()
            );

        } catch (Exception e) {
            log.error("Text extraction failed for blob: {}", sourceBlobName, e);
            throw new IllegalStateException(
                    "Failed to extract text from document",
                    e
            );
        }
    }

    private String buildExtractedTextBlobName(String sourceBlobName) {
        return "extracted-text/"
                + UUID.randomUUID()
                + "-"
                + sourceBlobName
                + ".txt";
    }
}
