package za.co.kpolit.document_service.service.extractor;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class PdfTextExtractor implements TextExtractor {

    private static final Tika TIKA = new Tika();

    @Override
    public String extract(InputStream inputStream) {
        try {
            return TIKA.parseToString(inputStream);
        } catch (Exception e) {
            log.error("PDF text extraction failed", e);
            throw new IllegalStateException("Unable to extract text from PDF", e);
        }
    }
}
