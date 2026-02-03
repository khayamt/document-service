package za.co.kpolit.document_service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import za.co.kpolit.document_service.kafka.event.DocumentUploadedEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class DocumentEventProducer {

    private final KafkaTemplate<String, DocumentUploadedEvent> kafkaTemplate;

    @Value("${kafka.topics.document-uploaded}")
    private String topic;

    public DocumentEventProducer(KafkaTemplate<String, DocumentUploadedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        try {
            // Send event asynchronously using CompletableFuture
            CompletableFuture<SendResult<String, DocumentUploadedEvent>> future =
                    kafkaTemplate.send(topic, event.documentId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish DocumentUploadedEvent: {}", event, ex);
                    // TODO: optionally push to dead-letter topic or retry
                } else {
                    log.info("DocumentUploadedEvent successfully published: {}", event.documentId());
                }
            });

        } catch (Exception e) {
            log.error("Unexpected error while publishing DocumentUploadedEvent: {}", event, e);
        }
    }
}
