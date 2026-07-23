package com.experiment.campaignflow.exception;

public class DuplicateRecipientException extends RuntimeException {

    public DuplicateRecipientException(String email) {
        super("Recipient already exists in this list: " + email);
    }
}