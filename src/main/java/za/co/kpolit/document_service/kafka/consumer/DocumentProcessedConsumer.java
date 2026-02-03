package za.co.kpolit.document_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import za.co.kpolit.document_service.kafka.event.DocumentProcessedEvent;
import za.co.kpolit.document_service.service.DocumentAiResultService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessedConsumer {

    private final DocumentAiResultService aiResultService;

    @KafkaListener(
            topics = "document-processed",
            groupId = "document-service"
    )
    public void onDocumentProcessed(
            @Payload DocumentProcessedEvent event
    ) {
        log.info(
                "Received document-processed event for document {} (version={})",
                event.documentId(),
                event.version()
        );

        aiResultService.saveAiResult(event);
    }
}
