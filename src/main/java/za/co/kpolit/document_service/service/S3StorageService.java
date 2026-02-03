package za.co.kpolit.document_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service("AWS")
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @PostConstruct
    public void validateAwsCredentials() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            log.info("AWS credentials validated successfully");
        } catch (Exception e) {
            throw new IllegalStateException(
                    "AWS credentials misconfigured or missing. Document-service cannot start.",
                    e
            );
        }
    }
    public String uploadFile(MultipartFile file) throws IOException {
        String key = "documents/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        log.info("AWS  uploadFile: " + key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return key;
    }
    public InputStream download(String blobName) {
            try {
                log.debug("Downloading object from S3: bucket={}, key={}",
                        bucketName,
                        blobName
                );

                GetObjectRequest request = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(blobName)
                        .build();

                ResponseInputStream<GetObjectResponse> stream =
                        s3Client.getObject(request);

                return stream;

            } catch (Exception e) {
                log.error("Failed to download S3 object: {}", blobName, e);
                throw new IllegalStateException(
                        "Unable to download document from storage",
                        e
                );
            }
        }

    public String uploadExtractedText(String sourceBlob, byte[] content) {
        String extractedBlob =
                sourceBlob.replace("documents/", "extracted-text/")
                        .replaceAll("\\.[^.]+$", ".txt");

        // putObject(extractedBlob, content)
        return extractedBlob;
    }
    public void upload(String blobName, byte[] data, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(blobName)
                .contentType(contentType)
                .contentLength((long) data.length)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(data));
    }
    
    @Override
    public String readText(String key) {
        try {
            log.debug("Reading text from S3: bucket={}, key={}",
                    bucketName,
                    key);
            return new String(
                    s3Client.getObject(
                            GetObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(key)
                                    .build()
                    ).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            log.error("Failed to read text from S3 object: {}", key, e);
            throw new IllegalStateException(
                    "Unable to read text from storage",
                    e
            );
        }
        
    }


}
