package com.experiment.campaignflow.dto;

public record RecipientImportError(
        long rowNumber,
        String email,
        String reason) {
}