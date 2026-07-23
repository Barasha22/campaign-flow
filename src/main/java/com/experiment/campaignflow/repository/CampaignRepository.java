package com.experiment.campaignflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experiment.campaignflow.domain.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
}