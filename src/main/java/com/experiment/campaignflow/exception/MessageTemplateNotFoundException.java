package com.experiment.campaignflow.exception;

import java.util.UUID;

public class MessageTemplateNotFoundException
        extends RuntimeException {

    public MessageTemplateNotFoundException(UUID templateId) {
        super("Message template not found: " + templateId);
    }
}