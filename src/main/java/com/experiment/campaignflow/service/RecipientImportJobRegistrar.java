package com.experiment.campaignflow.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.OutboxEvent;
import com.experiment.campaignflow.domain.RecipientImportJob;
import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.dto.CreateRecipientImportResponse;
import com.experiment.campaignflow.exception.RecipientListNotFoundException;
import com.experiment.campaignflow.repository.OutboxEventRepository;
import com.experiment.campaignflow.repository.RecipientImportJobRepository;
import com.experiment.campaignflow.repository.RecipientListRepository;

@Service
public class RecipientImportJobRegistrar {

    private final RecipientListRepository recipientListRepository;
    private final RecipientImportJobRepository importJobRepository;
    private final OutboxEventRepository outboxEventRepository;

    public RecipientImportJobRegistrar(
            RecipientListRepository recipientListRepository,
            RecipientImportJobRepository importJobRepository,
            OutboxEventRepository outboxEventRepository) {
        this.recipientListRepository = recipientListRepository;
        this.importJobRepository = importJobRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public CreateRecipientImportResponse register(
            UUID jobId,
            UUID recipientListId,
            String originalFileName,
            String storedPath) {
        RecipientList recipientList = recipientListRepository
                .findById(recipientListId)
                .orElseThrow(
                        () -> new RecipientListNotFoundException(
                                recipientListId));

        RecipientImportJob job = new RecipientImportJob(
                jobId,
                recipientList,
                originalFileName,
                storedPath);

        RecipientImportJob savedJob = importJobRepository.save(job);

        outboxEventRepository.save(
                new OutboxEvent(
                        "RECIPIENT_IMPORT_JOB",
                        savedJob.getId(),
                        "RECIPIENT_IMPORT_REQUESTED",
                        "{\"jobId\":\"" + savedJob.getId() + "\"}"));

        return new CreateRecipientImportResponse(
                savedJob.getId(),
                savedJob.getStatus());
    }
}
