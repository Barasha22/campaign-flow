package com.experiment.campaignflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCampaignRequest(

    @NotBlank(message = "Campaign name is required")
    @Size(max = 150, message = "Campaign name must not exceed 150 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description

) {
}