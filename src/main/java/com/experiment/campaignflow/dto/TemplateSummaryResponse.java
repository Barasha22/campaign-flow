package com.experiment.campaignflow.dto;

import java.util.UUID;

import com.experiment.campaignflow.domain.MessageChannel;
import com.experiment.campaignflow.domain.MessageTemplate;

public record TemplateSummaryResponse(
        UUID id,
        String name,
        String subject,
        MessageChannel channel) {

    public static TemplateSummaryResponse from(
            MessageTemplate template) {
        if (template == null) {
            return null;
        }

        return new TemplateSummaryResponse(
                template.getId(),
                template.getName(),
                template.getSubject(),
                template.getChannel());
    }
}