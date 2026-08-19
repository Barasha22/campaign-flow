package com.experiment.campaignflow.service;

public record RecipientImportRow(
        long rowNumber,
        String email,
        String firstName,
        String lastName) {
}