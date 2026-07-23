package com.experiment.campaignflow.dto;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.domain.MessageChannel;
import com.experiment.campaignflow.domain.MessageTemplate;

public record MessageTemplateResponse(
        UUID id,
        String name,
        String subject,
        String body,
        MessageChannel channel,
        Instant createdAt,
        Instant updatedAt) {

    public static MessageTemplateResponse from(
            MessageTemplate template) {
        return new MessageTemplateResponse(
                template.getId(),
                template.getName(),
                template.getSubject(),
                template.getBody(),
                template.getChannel(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }
}