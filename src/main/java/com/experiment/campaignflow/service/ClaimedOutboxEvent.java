package com.experiment.campaignflow.service;

import java.util.UUID;

public record ClaimedOutboxEvent(
        UUID id,
        UUID aggregateId,
        String eventType,
        String payload,
        int attempts) {
}
