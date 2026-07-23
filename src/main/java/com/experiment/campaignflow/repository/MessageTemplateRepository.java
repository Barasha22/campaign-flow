package com.experiment.campaignflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experiment.campaignflow.domain.MessageTemplate;

public interface MessageTemplateRepository
        extends JpaRepository<MessageTemplate, UUID> {

    boolean existsByName(String name);
}