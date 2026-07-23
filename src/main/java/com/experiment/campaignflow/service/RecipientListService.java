package com.experiment.campaignflow.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.experiment.campaignflow.domain.Recipient;
import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.dto.CreateRecipientListRequest;
import com.experiment.campaignflow.dto.CreateRecipientRequest;
import com.experiment.campaignflow.dto.PageResponse;
import com.experiment.campaignflow.dto.RecipientListResponse;
import com.experiment.campaignflow.dto.RecipientResponse;
import com.experiment.campaignflow.exception.DuplicateRecipientException;
import com.experiment.campaignflow.exception.DuplicateRecipientListNameException;
import com.experiment.campaignflow.exception.RecipientListNotFoundException;
import com.experiment.campaignflow.repository.RecipientListRepository;
import com.experiment.campaignflow.repository.RecipientRepository;

@Service
public class RecipientListService {

    private final RecipientListRepository recipientListRepository;
    private final RecipientRepository recipientRepository;

    public RecipientListService(
            RecipientListRepository recipientListRepository,
            RecipientRepository recipientRepository) {
        this.recipientListRepository = recipientListRepository;
        this.recipientRepository = recipientRepository;
    }

    @Transactional
    public RecipientListResponse createRecipientList(
            CreateRecipientListRequest request) {
        if (recipientListRepository.existsByName(request.name())) {
            throw new DuplicateRecipientListNameException(request.name());
        }

        RecipientList recipientList = new RecipientList(
                request.name(),
                request.description());

        return RecipientListResponse.from(
                recipientListRepository.save(recipientList));
    }

    @Transactional(readOnly = true)
    public List<RecipientListResponse> getRecipientLists() {
        return recipientListRepository.findAll()
                .stream()
                .map(RecipientListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecipientListResponse getRecipientList(UUID recipientListId) {
        return RecipientListResponse.from(
                findRecipientList(recipientListId));
    }

    @Transactional
    public RecipientResponse addRecipient(
            UUID recipientListId,
            CreateRecipientRequest request) {
        RecipientList recipientList = findRecipientList(recipientListId);

        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (recipientRepository.existsByRecipientListIdAndEmail(
                recipientListId,
                normalizedEmail)) {
            throw new DuplicateRecipientException(normalizedEmail);
        }

        Recipient recipient = new Recipient(
                recipientList,
                normalizedEmail,
                request.firstName(),
                request.lastName());

        Recipient savedRecipient = recipientRepository.save(recipient);

        recipientList.incrementRecipientCount();

        return RecipientResponse.from(savedRecipient);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecipientResponse> getRecipients(
            UUID recipientListId,
            Pageable pageable) {
        findRecipientList(recipientListId);

        Page<Recipient> recipients = recipientRepository.findByRecipientListId(
                recipientListId,
                pageable);

        return PageResponse.from(
                recipients,
                RecipientResponse::from);
    }

    private RecipientList findRecipientList(UUID recipientListId) {
        return recipientListRepository.findById(recipientListId)
                .orElseThrow(
                        () -> new RecipientListNotFoundException(recipientListId));
    }
}