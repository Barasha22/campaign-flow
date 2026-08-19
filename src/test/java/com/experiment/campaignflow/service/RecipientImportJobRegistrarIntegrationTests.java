package com.experiment.campaignflow.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.repository.RecipientListRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = "campaignflow.outbox.dispatcher.enabled=false")
@Transactional
class RecipientImportJobRegistrarIntegrationTests {

    @Autowired
    private RecipientImportJobRegistrar registrar;

    @Autowired
    private RecipientListRepository recipientListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void registrationCreatesJobAndOutboxEventTogether() {
        RecipientList recipientList = recipientListRepository.saveAndFlush(
                new RecipientList(
                        "outbox-test-" + UUID.randomUUID(),
                        "Transactional outbox test"));
        UUID jobId = UUID.randomUUID();

        registrar.register(
                jobId,
                recipientList.getId(),
                "recipients.csv",
                "recipient-imports/pending/" + jobId + "/recipients.csv");

        entityManager.flush();

        Integer jobCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipient_import_jobs WHERE id = ?",
                Integer.class,
                jobId);
        Integer eventCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM outbox_events
                WHERE aggregate_id = ?
                  AND event_type = 'RECIPIENT_IMPORT_REQUESTED'
                  AND status = 'PENDING'
                """,
                Integer.class,
                jobId);

        assertThat(jobCount).isEqualTo(1);
        assertThat(eventCount).isEqualTo(1);
    }
}
