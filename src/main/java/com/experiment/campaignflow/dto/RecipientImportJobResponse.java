package com.experiment.campaignflow.dto;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.domain.RecipientImportJob;
import com.experiment.campaignflow.domain.RecipientImportStatus;

public record RecipientImportJobResponse(
        UUID jobId,
        UUID recipientListId,
        String originalFileName,
        RecipientImportStatus status,
        long totalRows,
        long processedRows,
        long importedRows,
        long duplicateRows,
        long invalidRows,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        Instant updatedAt) {

    public static RecipientImportJobResponse from(
            RecipientImportJob job) {
        return new RecipientImportJobResponse(
                job.getId(),
                job.getRecipientList().getId(),
                job.getOriginalFileName(),
                job.getStatus(),
                job.getTotalRows(),
                job.getProcessedRows(),
                job.getImportedRows(),
                job.getDuplicateRows(),
                job.getInvalidRows(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getUpdatedAt());
    }
}