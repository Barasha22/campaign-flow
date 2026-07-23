package com.experiment.campaignflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMessageTemplateRequest(

        @NotBlank(message = "Template name is required") @Size(max = 150, message = "Template name must not exceed 150 characters") String name,

        @Size(max = 250, message = "Subject must not exceed 250 characters") String subject,

        @NotBlank(message = "Template body is required") String body

) {
}