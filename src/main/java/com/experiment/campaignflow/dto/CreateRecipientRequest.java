package com.experiment.campaignflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRecipientRequest(

        @NotBlank(message = "Email is required") @Email(message = "Email address is invalid") @Size(max = 320, message = "Email must not exceed 320 characters") String email,

        @Size(max = 100, message = "First name must not exceed 100 characters") String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters") String lastName

) {
}