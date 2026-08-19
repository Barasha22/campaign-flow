package com.experiment.campaignflow.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.campaignflow.domain.RecipientImportJob;
import com.experiment.campaignflow.dto.CreateRecipientImportResponse;
import com.experiment.campaignflow.dto.RecipientImportJobResponse;
import com.experiment.campaignflow.exception.RecipientImportJobNotFoundException;
import com.experiment.campaignflow.repository.RecipientImportJobRepository;
import com.experiment.campaignflow.storage.FileStorageService;

@Service
public class RecipientImportJobService {

    private final RecipientImportJobRepository importJobRepository;
    private final FileStorageService fileStorageService;
    private final RecipientImportJobRegistrar importJobRegistrar;

    public RecipientImportJobService(
            RecipientImportJobRepository importJobRepository,
            FileStorageService fileStorageService,
            RecipientImportJobRegistrar importJobRegistrar) {
        this.importJobRepository = importJobRepository;
        this.fileStorageService = fileStorageService;
        this.importJobRegistrar = importJobRegistrar;
    }

    public CreateRecipientImportResponse createImport(
            UUID recipientListId,
            MultipartFile file) {
        UUID jobId = UUID.randomUUID();
        String storedPath = fileStorageService.store(jobId, file);

        try {
            return importJobRegistrar.register(
                    jobId,
                    recipientListId,
                    file.getOriginalFilename(),
                    storedPath);
        } catch (RuntimeException exception) {
            try {
                fileStorageService.delete(storedPath);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public RecipientImportJobResponse getImportJob(UUID jobId) {
        RecipientImportJob job = importJobRepository.findById(jobId)
                .orElseThrow(
                        () -> new RecipientImportJobNotFoundException(jobId));

        return RecipientImportJobResponse.from(job);
    }

    @Transactional(readOnly = true)
    public RecipientImportJobDetails getJobDetails(UUID jobId) {

        RecipientImportJob job = importJobRepository.findById(jobId)
                .orElseThrow(
                        () -> new RecipientImportJobNotFoundException(jobId));

        return new RecipientImportJobDetails(
                job.getId(),
                job.getRecipientList().getId(),
                job.getStoredFilePath());
    }
}
