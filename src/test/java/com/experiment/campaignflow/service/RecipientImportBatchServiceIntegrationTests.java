package com.experiment.campaignflow.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.repository.RecipientListRepository;

@SpringBootTest
@Transactional
class RecipientImportBatchServiceIntegrationTests {

    @Autowired
    private RecipientImportBatchService batchService;

    @Autowired
    private RecipientListRepository recipientListRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void duplicateBatchIsIgnoredWithoutInflatingRecipientCount() {
        RecipientList recipientList = recipientListRepository.saveAndFlush(
                new RecipientList(
                        "concurrency-test-" + UUID.randomUUID(),
                        "Batch conflict test"));

        List<RecipientImportRow> rows = List.of(
                new RecipientImportRow(
                        2,
                        "first@example.com",
                        "First",
                        "Recipient"),
                new RecipientImportRow(
                        3,
                        "second@example.com",
                        "Second",
                        "Recipient"));

        int firstImportCount = batchService.saveBatch(recipientList, rows);
        int duplicateImportCount = batchService.saveBatch(recipientList, rows);

        Integer storedRecipientCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM recipients WHERE recipient_list_id = ?",
                Integer.class,
                recipientList.getId());

        Long recordedRecipientCount = jdbcTemplate.queryForObject(
                "SELECT recipient_count FROM recipient_lists WHERE id = ?",
                Long.class,
                recipientList.getId());

        assertThat(firstImportCount).isEqualTo(2);
        assertThat(duplicateImportCount).isZero();
        assertThat(storedRecipientCount).isEqualTo(2);
        assertThat(recordedRecipientCount).isEqualTo(2);
    }
}
