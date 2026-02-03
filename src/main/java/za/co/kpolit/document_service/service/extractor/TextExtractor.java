package za.co.kpolit.document_service.service.extractor;

import java.io.InputStream;

public interface TextExtractor {
    String extract(InputStream inputStream);
}
