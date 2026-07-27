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

import com.experiment.campaignflow.dto.CampaignResponse;
import com.experiment.campaignflow.dto.CreateCampaignRequest;
import com.experiment.campaignflow.dto.ScheduleCampaignRequest;
import com.experiment.campaignflow.dto.UpdateCampaignRequest;
import com.experiment.campaignflow.service.CampaignService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

        private final CampaignService campaignService;

        public CampaignController(CampaignService campaignService) {
                this.campaignService = campaignService;
        }

        @PostMapping
        public ResponseEntity<CampaignResponse> createCampaign(
                        @Valid @RequestBody CreateCampaignRequest request) {
                CampaignResponse response = campaignService.createCampaign(request);

                return ResponseEntity
                                .created(URI.create("/api/campaigns/" + response.id()))
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<List<CampaignResponse>> getCampaigns() {
                return ResponseEntity.ok(
                                campaignService.getCampaigns());
        }

        @GetMapping("/{campaignId}")
        public ResponseEntity<CampaignResponse> getCampaign(
                        @PathVariable UUID campaignId) {
                return ResponseEntity.ok(
                                campaignService.getCampaign(campaignId));
        }

        @PutMapping("/{campaignId}")
        public ResponseEntity<CampaignResponse> updateCampaign(
                        @PathVariable UUID campaignId,
                        @Valid @RequestBody UpdateCampaignRequest request) {
                return ResponseEntity.ok(
                                campaignService.updateCampaign(campaignId, request));
        }

        @PostMapping("/{campaignId}/schedule")
        public ResponseEntity<CampaignResponse> scheduleCampaign(
                        @PathVariable UUID campaignId,
                        @Valid @RequestBody ScheduleCampaignRequest request) {
                return ResponseEntity.ok(
                                campaignService.scheduleCampaign(campaignId, request));
        }

        @PostMapping("/{campaignId}/cancel")
        public ResponseEntity<CampaignResponse> cancelCampaign(
                        @PathVariable UUID campaignId) {
                return ResponseEntity.ok(
                                campaignService.cancelCampaign(campaignId));
        }

        @PutMapping("/{campaignId}/template/{templateId}")
        public ResponseEntity<CampaignResponse> assignTemplate(
                        @PathVariable UUID campaignId,
                        @PathVariable UUID templateId) {
                return ResponseEntity.ok(
                                campaignService.assignTemplate(campaignId, templateId));
        }

        @PutMapping("/{campaignId}/recipient-list/{recipientListId}")
        public ResponseEntity<CampaignResponse> assignRecipientList(
                        @PathVariable UUID campaignId,
                        @PathVariable UUID recipientListId) {
                return ResponseEntity.ok(
                                campaignService.assignRecipientList(
                                                campaignId,
                                                recipientListId));
        }
}