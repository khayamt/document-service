package za.co.kpolit.document_service.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentResponseDto {
    private UUID id;
    private String status;
    private String summary;
    private String quizJson;
    private String testJson;

}
