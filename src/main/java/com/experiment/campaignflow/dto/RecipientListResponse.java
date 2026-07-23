package com.experiment.campaignflow.dto;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.domain.RecipientList;

public record RecipientListResponse(
        UUID id,
        String name,
        String description,
        long recipientCount,
        Instant createdAt,
        Instant updatedAt) {

    public static RecipientListResponse from(RecipientList recipientList) {
        return new RecipientListResponse(
                recipientList.getId(),
                recipientList.getName(),
                recipientList.getDescription(),
                recipientList.getRecipientCount(),
                recipientList.getCreatedAt(),
                recipientList.getUpdatedAt());
    }
}