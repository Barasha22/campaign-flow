package com.experiment.campaignflow.dto;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.domain.Recipient;
import com.experiment.campaignflow.domain.RecipientStatus;

public record RecipientResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        RecipientStatus status,
        Instant createdAt) {

    public static RecipientResponse from(Recipient recipient) {
        return new RecipientResponse(
                recipient.getId(),
                recipient.getEmail(),
                recipient.getFirstName(),
                recipient.getLastName(),
                recipient.getStatus(),
                recipient.getCreatedAt());
    }
}