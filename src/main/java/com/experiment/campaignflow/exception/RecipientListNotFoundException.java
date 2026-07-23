package com.experiment.campaignflow.exception;

import java.util.UUID;

public class RecipientListNotFoundException extends RuntimeException {

    public RecipientListNotFoundException(UUID recipientListId) {
        super("Recipient list not found: " + recipientListId);
    }
}