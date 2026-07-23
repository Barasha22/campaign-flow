package com.experiment.campaignflow.exception;

public class DuplicateTemplateNameException
        extends RuntimeException {

    public DuplicateTemplateNameException(String name) {
        super("A message template already exists with name: " + name);
    }
}