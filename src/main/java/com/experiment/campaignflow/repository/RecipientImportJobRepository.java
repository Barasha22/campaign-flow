package com.experiment.campaignflow.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.experiment.campaignflow.domain.RecipientImportJob;

public interface RecipientImportJobRepository
        extends JpaRepository<RecipientImportJob, UUID> {

    @Modifying
    @Query(value = """
            UPDATE recipient_import_jobs
            SET status = 'PROCESSING',
                started_at = COALESCE(started_at, NOW()),
                error_message = NULL,
                updated_at = NOW()
            WHERE id = :jobId
              AND (
                  status = 'PENDING'
                  OR (
                      status = 'PROCESSING'
                      AND updated_at < :staleBefore
                  )
              )
            """, nativeQuery = true)
    int tryMarkProcessing(
            @Param("jobId") UUID jobId,
            @Param("staleBefore") Instant staleBefore);
}
