package com.experiment.campaignflow.service;

import java.util.UUID;

public record RecipientImportJobDetails(
        UUID jobId,
        UUID recipientListId,
        String storedFilePath) {
}