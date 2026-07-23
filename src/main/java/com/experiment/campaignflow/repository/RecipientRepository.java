package com.experiment.campaignflow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.experiment.campaignflow.domain.Recipient;

public interface RecipientRepository
        extends JpaRepository<Recipient, UUID> {

    boolean existsByRecipientListIdAndEmail(
            UUID recipientListId,
            String email);

    Page<Recipient> findByRecipientListId(
            UUID recipientListId,
            Pageable pageable);
}