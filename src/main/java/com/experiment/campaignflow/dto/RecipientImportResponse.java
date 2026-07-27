package com.experiment.campaignflow.dto;

import java.util.List;
import java.util.UUID;

public record RecipientImportResponse(
        UUID recipientListId,
        String fileName,
        long totalRows,
        long importedRows,
        long duplicateRows,
        long invalidRows,
        List<RecipientImportError> errors) {
}