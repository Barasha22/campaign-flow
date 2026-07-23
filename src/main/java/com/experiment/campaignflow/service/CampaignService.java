package com.experiment.campaignflow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.Campaign;
import com.experiment.campaignflow.domain.MessageTemplate;
import com.experiment.campaignflow.dto.CampaignResponse;
import com.experiment.campaignflow.dto.CreateCampaignRequest;
import com.experiment.campaignflow.dto.ScheduleCampaignRequest;
import com.experiment.campaignflow.dto.UpdateCampaignRequest;
import com.experiment.campaignflow.exception.CampaignNotFoundException;
import com.experiment.campaignflow.exception.MessageTemplateNotFoundException;
import com.experiment.campaignflow.repository.CampaignRepository;
import com.experiment.campaignflow.repository.MessageTemplateRepository;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final MessageTemplateRepository templateRepository;

    public CampaignService(
            CampaignRepository campaignRepository,
            MessageTemplateRepository templateRepository) {
        this.campaignRepository = campaignRepository;
        this.templateRepository = templateRepository;
    }

    @Transactional
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        Campaign campaign = new Campaign(
                request.name(),
                request.description());

        Campaign savedCampaign = campaignRepository.save(campaign);

        return CampaignResponse.from(savedCampaign);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> getCampaigns() {
        return campaignRepository.findAll()
                .stream()
                .map(CampaignResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaign(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        return CampaignResponse.from(campaign);
    }

    @Transactional
    public CampaignResponse updateCampaign(
            UUID campaignId,
            UpdateCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        campaign.updateDetails(
                request.name(),
                request.description());

        return CampaignResponse.from(campaign);
    }

    @Transactional
    public CampaignResponse scheduleCampaign(
            UUID campaignId,
            ScheduleCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        campaign.schedule(request.scheduledAt());

        return CampaignResponse.from(campaign);
    }

    @Transactional
    public CampaignResponse cancelCampaign(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));

        campaign.cancel();

        return CampaignResponse.from(campaign);
    }

    @Transactional
    public CampaignResponse assignTemplate(
            UUID campaignId,
            UUID templateId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(
                        () -> new CampaignNotFoundException(campaignId));

        MessageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(
                        () -> new MessageTemplateNotFoundException(templateId));

        campaign.assignTemplate(template);

        return CampaignResponse.from(campaign);
    }
}