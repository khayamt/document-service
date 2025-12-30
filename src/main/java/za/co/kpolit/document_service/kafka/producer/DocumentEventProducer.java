package za.co.kpolit.document_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import za.co.kpolit.document_service.kafka.event.DocumentUploadedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.document-uploaded}")
    private String topic;

    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        log.info("Publishing document-uploaded event: {}", event);
        kafkaTemplate.send(topic, event.documentId().toString(), event);
    }
}
