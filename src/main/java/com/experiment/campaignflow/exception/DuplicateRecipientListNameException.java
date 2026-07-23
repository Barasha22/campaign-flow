package com.experiment.campaignflow.exception;

public class DuplicateRecipientListNameException extends RuntimeException {

    public DuplicateRecipientListNameException(String name) {
        super("A recipient list already exists with name: " + name);
    }
}