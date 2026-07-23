package com.experiment.campaignflow.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record ScheduleCampaignRequest(

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    Instant scheduledAt

) {
}