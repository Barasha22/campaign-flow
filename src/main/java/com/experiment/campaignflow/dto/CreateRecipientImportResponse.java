package com.experiment.campaignflow.dto;

import java.util.UUID;

import com.experiment.campaignflow.domain.RecipientImportStatus;

public record CreateRecipientImportResponse(
        UUID jobId,
        RecipientImportStatus status) {
}