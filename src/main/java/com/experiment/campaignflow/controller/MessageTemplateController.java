package com.experiment.campaignflow.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.experiment.campaignflow.dto.CreateMessageTemplateRequest;
import com.experiment.campaignflow.dto.MessageTemplateResponse;
import com.experiment.campaignflow.dto.UpdateMessageTemplateRequest;
import com.experiment.campaignflow.service.MessageTemplateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/templates")
public class MessageTemplateController {

    private final MessageTemplateService templateService;

    public MessageTemplateController(
            MessageTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<MessageTemplateResponse> createTemplate(
            @Valid @RequestBody CreateMessageTemplateRequest request) {
        MessageTemplateResponse response = templateService.createTemplate(request);

        return ResponseEntity
                .created(URI.create("/api/templates/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MessageTemplateResponse>> getTemplates() {

        return ResponseEntity.ok(
                templateService.getTemplates());
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<MessageTemplateResponse> getTemplate(
            @PathVariable UUID templateId) {
        return ResponseEntity.ok(
                templateService.getTemplate(templateId));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<MessageTemplateResponse> updateTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody UpdateMessageTemplateRequest request) {
        return ResponseEntity.ok(
                templateService.updateTemplate(templateId, request));
    }
}