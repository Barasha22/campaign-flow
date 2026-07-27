package com.experiment.campaignflow.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.campaignflow.dto.CreateRecipientListRequest;
import com.experiment.campaignflow.dto.CreateRecipientRequest;
import com.experiment.campaignflow.dto.PageResponse;
import com.experiment.campaignflow.dto.RecipientImportResponse;
import com.experiment.campaignflow.dto.RecipientListResponse;
import com.experiment.campaignflow.dto.RecipientResponse;
import com.experiment.campaignflow.service.RecipientCsvImportService;
import com.experiment.campaignflow.service.RecipientListService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipient-lists")
public class RecipientListController {

        private static final int DEFAULT_PAGE_SIZE = 20;
        private static final int MAX_PAGE_SIZE = 100;

        private final RecipientListService recipientListService;
        private final RecipientCsvImportService recipientCsvImportService;

        public RecipientListController(
                        RecipientListService recipientListService,
                        RecipientCsvImportService recipientCsvImportService) {
                this.recipientListService = recipientListService;
                this.recipientCsvImportService = recipientCsvImportService;
        }

        @PostMapping
        public ResponseEntity<RecipientListResponse> createRecipientList(
                        @Valid @RequestBody CreateRecipientListRequest request) {
                RecipientListResponse response = recipientListService.createRecipientList(request);

                return ResponseEntity
                                .created(
                                                URI.create("/api/recipient-lists/" + response.id()))
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<List<RecipientListResponse>> getRecipientLists() {

                return ResponseEntity.ok(
                                recipientListService.getRecipientLists());
        }

        @GetMapping("/{recipientListId}")
        public ResponseEntity<RecipientListResponse> getRecipientList(
                        @PathVariable UUID recipientListId) {
                return ResponseEntity.ok(
                                recipientListService.getRecipientList(recipientListId));
        }

        @PostMapping("/{recipientListId}/recipients")
        public ResponseEntity<RecipientResponse> addRecipient(
                        @PathVariable UUID recipientListId,
                        @Valid @RequestBody CreateRecipientRequest request) {
                RecipientResponse response = recipientListService.addRecipient(recipientListId, request);

                return ResponseEntity
                                .created(
                                                URI.create(
                                                                "/api/recipient-lists/"
                                                                                + recipientListId
                                                                                + "/recipients/"
                                                                                + response.id()))
                                .body(response);
        }

        @GetMapping("/{recipientListId}/recipients")
        public ResponseEntity<PageResponse<RecipientResponse>> getRecipients(
                        @PathVariable UUID recipientListId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {
                int validatedPage = Math.max(page, 0);
                int validatedSize = Math.min(
                                Math.max(size, 1),
                                MAX_PAGE_SIZE);

                Pageable pageable = PageRequest.of(
                                validatedPage,
                                validatedSize,
                                Sort.by("createdAt").descending());

                return ResponseEntity.ok(
                                recipientListService.getRecipients(
                                                recipientListId,
                                                pageable));
        }

        @PostMapping(value = "/{recipientListId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<RecipientImportResponse> uploadRecipients(
                        @PathVariable UUID recipientListId,
                        @RequestParam("file") MultipartFile file) {
                RecipientImportResponse response = recipientCsvImportService.importRecipients(
                                recipientListId,
                                file);

                return ResponseEntity.ok(response);
        }
}