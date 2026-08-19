package com.experiment.campaignflow.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.domain.RecipientStatus;
import com.experiment.campaignflow.repository.RecipientListRepository;

@Service
public class RecipientImportBatchService {

    private static final String INSERT_RECIPIENT_SQL = """
            INSERT INTO recipients (
                id,
                recipient_list_id,
                email,
                first_name,
                last_name,
                status,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (recipient_list_id, email) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RecipientListRepository recipientListRepository;

    public RecipientImportBatchService(
            JdbcTemplate jdbcTemplate,
            RecipientListRepository recipientListRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.recipientListRepository = recipientListRepository;
    }

    @Transactional
    public int saveBatch(
            RecipientList recipientList,
            List<RecipientImportRow> rows) {
        Instant createdAt = Instant.now();

        int[] updateCounts = jdbcTemplate.batchUpdate(
                INSERT_RECIPIENT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(
                            PreparedStatement statement,
                            int index) throws java.sql.SQLException {
                        RecipientImportRow row = rows.get(index);

                        statement.setObject(1, UUID.randomUUID());
                        statement.setObject(2, recipientList.getId());
                        statement.setString(3, row.email());
                        statement.setString(4, row.firstName());
                        statement.setString(5, row.lastName());
                        statement.setString(6, RecipientStatus.ACTIVE.name());
                        statement.setTimestamp(7, Timestamp.from(createdAt));
                    }

                    @Override
                    public int getBatchSize() {
                        return rows.size();
                    }
                });

        int importedRows = 0;

        for (int updateCount : updateCounts) {
            if (updateCount > 0
                    || updateCount == Statement.SUCCESS_NO_INFO) {
                importedRows++;
            }
        }

        if (importedRows > 0) {
            recipientListRepository.incrementRecipientCount(
                    recipientList.getId(),
                    importedRows);
        }

        return importedRows;
    }
}
