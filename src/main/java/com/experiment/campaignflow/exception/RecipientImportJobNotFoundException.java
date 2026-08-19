package com.experiment.campaignflow.exception;

import java.util.UUID;

public class RecipientImportJobNotFoundException
        extends RuntimeException {

    public RecipientImportJobNotFoundException(UUID jobId) {
        super("Recipient import job not found: " + jobId);
    }
}