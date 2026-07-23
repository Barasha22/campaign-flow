package com.experiment.campaignflow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.MessageTemplate;
import com.experiment.campaignflow.dto.CreateMessageTemplateRequest;
import com.experiment.campaignflow.dto.MessageTemplateResponse;
import com.experiment.campaignflow.dto.UpdateMessageTemplateRequest;
import com.experiment.campaignflow.exception.DuplicateTemplateNameException;
import com.experiment.campaignflow.exception.MessageTemplateNotFoundException;
import com.experiment.campaignflow.repository.MessageTemplateRepository;

@Service
public class MessageTemplateService {

    private final MessageTemplateRepository templateRepository;

    public MessageTemplateService(
            MessageTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Transactional
    public MessageTemplateResponse createTemplate(
            CreateMessageTemplateRequest request) {
        if (templateRepository.existsByName(request.name())) {
            throw new DuplicateTemplateNameException(request.name());
        }

        MessageTemplate template = new MessageTemplate(
                request.name(),
                request.subject(),
                request.body(),
                request.channel());

        MessageTemplate savedTemplate = templateRepository.save(template);

        return MessageTemplateResponse.from(savedTemplate);
    }

    @Transactional(readOnly = true)
    public List<MessageTemplateResponse> getTemplates() {
        return templateRepository.findAll()
                .stream()
                .map(MessageTemplateResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MessageTemplateResponse getTemplate(UUID templateId) {
        MessageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(
                        () -> new MessageTemplateNotFoundException(templateId));

        return MessageTemplateResponse.from(template);
    }

    @Transactional
    public MessageTemplateResponse updateTemplate(
            UUID templateId,
            UpdateMessageTemplateRequest request) {
        MessageTemplate template = templateRepository.findById(templateId)
                .orElseThrow(
                        () -> new MessageTemplateNotFoundException(templateId));

        boolean nameChanged = !template.getName().equals(request.name());

        if (nameChanged &&
                templateRepository.existsByName(request.name())) {
            throw new DuplicateTemplateNameException(request.name());
        }

        template.updateDetails(
                request.name(),
                request.subject(),
                request.body());

        return MessageTemplateResponse.from(template);
    }
}