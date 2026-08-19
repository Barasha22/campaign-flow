package com.experiment.campaignflow.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.RecipientImportJob;
import com.experiment.campaignflow.exception.RecipientImportJobNotFoundException;
import com.experiment.campaignflow.repository.RecipientImportJobRepository;

@Service
public class RecipientImportJobUpdater {

    private final RecipientImportJobRepository repository;

    public RecipientImportJobUpdater(
            RecipientImportJobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean tryMarkStarted(UUID jobId) {
        return repository.tryMarkProcessing(
                jobId,
                Instant.now().minus(5, ChronoUnit.MINUTES)) == 1;
    }

    @Transactional
    public void updateProgress(
            UUID jobId,
            long totalRows,
            long processedRows,
            long importedRows,
            long duplicateRows,
            long invalidRows) {
        getJob(jobId).updateProgress(
                totalRows,
                processedRows,
                importedRows,
                duplicateRows,
                invalidRows);
    }

    @Transactional
    public void markCompleted(UUID jobId) {
        getJob(jobId).complete();
    }

    @Transactional
    public void markFailed(
            UUID jobId,
            String message) {
        getJob(jobId).fail(message);
    }

    private RecipientImportJob getJob(UUID jobId) {
        return repository.findById(jobId)
                .orElseThrow(
                        () -> new RecipientImportJobNotFoundException(jobId));
    }
}
