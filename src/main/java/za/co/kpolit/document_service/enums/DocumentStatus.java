package za.co.kpolit.document_service.enums;

public enum DocumentStatus {
    UPLOADING,
    UPLOADED,
    PROCESSING_CHUNKS,
    AI_PROCESSING,
    FINALIZING,
    COMPLETED,
    FAILED
}
