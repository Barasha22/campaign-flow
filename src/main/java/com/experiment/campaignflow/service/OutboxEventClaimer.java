package com.experiment.campaignflow.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventClaimer {

    private static final int MAX_ATTEMPTS = 5;

    private static final String CLAIM_SQL = """
            WITH candidates AS (
                SELECT id
                FROM outbox_events
                WHERE (
                    status = 'PENDING'
                    AND available_at <= NOW()
                ) OR (
                    status = 'PROCESSING'
                    AND updated_at < NOW() - INTERVAL '5 minutes'
                )
                ORDER BY created_at
                FOR UPDATE SKIP LOCKED
                LIMIT ?
            )
            UPDATE outbox_events event
            SET status = 'PROCESSING',
                attempts = attempts + 1,
                updated_at = NOW()
            FROM candidates
            WHERE event.id = candidates.id
            RETURNING event.id,
                      event.aggregate_id,
                      event.event_type,
                      event.payload,
                      event.attempts
            """;

    private final JdbcTemplate jdbcTemplate;

    public OutboxEventClaimer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<ClaimedOutboxEvent> claim(int batchSize) {
        return jdbcTemplate.query(
                CLAIM_SQL,
                statement -> statement.setInt(1, batchSize),
                (resultSet, rowNumber) -> new ClaimedOutboxEvent(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("aggregate_id", UUID.class),
                        resultSet.getString("event_type"),
                        resultSet.getString("payload"),
                        resultSet.getInt("attempts")));
    }

    public void markPublished(UUID eventId) {
        jdbcTemplate.update(
                """
                UPDATE outbox_events
                SET status = 'PUBLISHED',
                    published_at = NOW(),
                    updated_at = NOW(),
                    last_error = NULL
                WHERE id = ?
                """,
                eventId);
    }

    public void markDeliveryFailed(ClaimedOutboxEvent event, Exception exception) {
        boolean permanentlyFailed = event.attempts() >= MAX_ATTEMPTS;
        Duration retryDelay = Duration.ofSeconds(
                Math.min(300, 1L << Math.min(event.attempts(), 8)));
        Instant availableAt = Instant.now().plus(retryDelay);

        jdbcTemplate.update(
                """
                UPDATE outbox_events
                SET status = ?,
                    available_at = ?,
                    updated_at = NOW(),
                    last_error = ?
                WHERE id = ?
                """,
                permanentlyFailed ? "FAILED" : "PENDING",
                Timestamp.from(availableAt),
                errorMessage(exception),
                event.id());
    }

    private String errorMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? exception.getClass().getSimpleName()
                : message;
    }
}
