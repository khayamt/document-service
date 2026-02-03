package za.co.kpolit.document_service.service;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.WriteChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service("GCS")
public class GcsService implements StorageService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Value("${gcs.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String blobName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try (WriteChannel writer = storage.writer(blobInfo);
             InputStream inputStream = file.getInputStream()) {

            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int limit;
            while ((limit = inputStream.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
            }
        }

        return String.format("gs://%s/%s", bucketName, blobName);
    }

    public byte[] downloadFile(String blobName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, blobName);
        return storage.readAllBytes(blobId);
    }
    @Override
    public String readText(String key) {
        return "TOBEIMPLEMENTED";
    }
}
