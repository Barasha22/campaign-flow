package com.experiment.campaignflow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "campaignflow.outbox.dispatcher.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class OutboxEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            OutboxEventDispatcher.class);
    private static final String IMPORT_REQUESTED = "RECIPIENT_IMPORT_REQUESTED";
    private static final int CLAIM_BATCH_SIZE = 10;

    private final OutboxEventClaimer eventClaimer;
    private final RecipientImportProcessor importProcessor;

    public OutboxEventDispatcher(
            OutboxEventClaimer eventClaimer,
            RecipientImportProcessor importProcessor) {
        this.eventClaimer = eventClaimer;
        this.importProcessor = importProcessor;
    }

    @Scheduled(
            fixedDelayString = "${campaignflow.outbox.dispatcher.delay-ms:1000}")
    public void dispatch() {
        List<ClaimedOutboxEvent> events = eventClaimer.claim(CLAIM_BATCH_SIZE);

        for (ClaimedOutboxEvent event : events) {
            try {
                dispatch(event);
                eventClaimer.markPublished(event.id());
            } catch (Exception exception) {
                LOGGER.error(
                        "Unable to dispatch outbox event {}",
                        event.id(),
                        exception);
                eventClaimer.markDeliveryFailed(event, exception);
            }
        }
    }

    private void dispatch(ClaimedOutboxEvent event) {
        if (!IMPORT_REQUESTED.equals(event.eventType())) {
            throw new IllegalArgumentException(
                    "Unsupported outbox event type: " + event.eventType());
        }

        importProcessor.process(event.aggregateId());
    }
}
