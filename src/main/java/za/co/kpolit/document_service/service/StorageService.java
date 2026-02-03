package za.co.kpolit.document_service.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String uploadFile(MultipartFile file) throws IOException;
    String readText(String key);
}
