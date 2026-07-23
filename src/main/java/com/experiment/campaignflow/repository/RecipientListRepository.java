package com.experiment.campaignflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experiment.campaignflow.domain.RecipientList;

public interface RecipientListRepository
        extends JpaRepository<RecipientList, UUID> {

    boolean existsByName(String name);
}