package com.experiment.campaignflow.dto;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.domain.Campaign;
import com.experiment.campaignflow.domain.CampaignStatus;

public record CampaignResponse(
        UUID id,
        String name,
        String description,
        CampaignStatus status,
        Instant scheduledAt,
        TemplateSummaryResponse template,
        Instant createdAt,
        Instant updatedAt) {

    public static CampaignResponse from(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getDescription(),
                campaign.getStatus(),
                campaign.getScheduledAt(),
                TemplateSummaryResponse.from(
                        campaign.getMessageTemplate()),
                campaign.getCreatedAt(),
                campaign.getUpdatedAt());
    }
}