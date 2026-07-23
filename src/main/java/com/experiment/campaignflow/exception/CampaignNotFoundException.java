package com.experiment.campaignflow.exception;

import java.util.UUID;

public class CampaignNotFoundException extends RuntimeException {

    public CampaignNotFoundException(UUID campaignId) {
        super("Campaign not found: " + campaignId);
    }
}