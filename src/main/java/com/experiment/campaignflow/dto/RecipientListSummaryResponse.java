package com.experiment.campaignflow.dto;

import java.util.UUID;

import com.experiment.campaignflow.domain.RecipientList;

public record RecipientListSummaryResponse(
        UUID id,
        String name,
        long recipientCount) {

    public static RecipientListSummaryResponse from(
            RecipientList recipientList) {
        if (recipientList == null) {
            return null;
        }

        return new RecipientListSummaryResponse(
                recipientList.getId(),
                recipientList.getName(),
                recipientList.getRecipientCount());
    }
}