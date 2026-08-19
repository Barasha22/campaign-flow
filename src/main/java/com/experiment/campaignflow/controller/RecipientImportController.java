package com.experiment.campaignflow.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.campaignflow.dto.CreateRecipientImportResponse;
import com.experiment.campaignflow.dto.RecipientImportJobResponse;
import com.experiment.campaignflow.service.RecipientImportJobService;

@RestController
@RequestMapping("/api")
public class RecipientImportController {

    private final RecipientImportJobService importJobService;

    public RecipientImportController(
            RecipientImportJobService importJobService) {
        this.importJobService = importJobService;
    }

    @PostMapping(value = "/recipient-lists/{recipientListId}/imports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateRecipientImportResponse> createImport(
            @PathVariable UUID recipientListId,
            @RequestParam("file") MultipartFile file) {
        CreateRecipientImportResponse response = importJobService.createImport(
                recipientListId,
                file);

        return ResponseEntity
                .accepted()
                .location(
                        URI.create(
                                "/api/recipient-imports/"
                                        + response.jobId()))
                .body(response);
    }

    @GetMapping("/recipient-imports/{jobId}")
    public ResponseEntity<RecipientImportJobResponse> getImportJob(
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(
                importJobService.getImportJob(jobId));
    }
}