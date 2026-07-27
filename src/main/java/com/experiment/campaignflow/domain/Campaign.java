package com.experiment.campaignflow.domain;

import java.time.Instant;
import java.util.UUID;

import com.experiment.campaignflow.exception.InvalidCampaignStateException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campaigns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CampaignStatus status;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private MessageTemplate messageTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_list_id")
    private RecipientList recipientList;

    public Campaign(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.status = CampaignStatus.DRAFT;
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (status == null) {
            status = CampaignStatus.DRAFT;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void schedule(Instant scheduledAt) {
        if (status != CampaignStatus.DRAFT) {
            throw new InvalidCampaignStateException(
                    "Only a draft campaign can be scheduled");
        }

        if (messageTemplate == null) {
            throw new InvalidCampaignStateException(
                    "A campaign must have a message template before scheduling");
        }

        if (recipientList == null) {
            throw new InvalidCampaignStateException(
                    "A campaign must have a recipient list before scheduling");
        }

        if (recipientList.getRecipientCount() == 0) {
            throw new InvalidCampaignStateException(
                    "A campaign cannot be scheduled with an empty recipient list");
        }

        if (scheduledAt == null || !scheduledAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException(
                    "Scheduled time must be in the future");
        }

        this.scheduledAt = scheduledAt;
        this.status = CampaignStatus.SCHEDULED;
    }

    public void cancel() {
        if (status == CampaignStatus.COMPLETED) {
            throw new InvalidCampaignStateException(
                    "A completed campaign cannot be cancelled");
        }

        if (status == CampaignStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Campaign is already cancelled");
        }

        this.status = CampaignStatus.CANCELLED;
    }

    public void assignTemplate(MessageTemplate messageTemplate) {
        if (status != CampaignStatus.DRAFT) {
            throw new InvalidCampaignStateException(
                    "A template can only be assigned to a draft campaign");
        }

        if (messageTemplate == null) {
            throw new IllegalArgumentException(
                    "Message template is required");
        }

        this.messageTemplate = messageTemplate;
    }

    public void assignRecipientList(RecipientList recipientList) {
        if (status != CampaignStatus.DRAFT) {
            throw new InvalidCampaignStateException(
                    "A recipient list can only be assigned to a draft campaign");
        }

        if (recipientList == null) {
            throw new IllegalArgumentException(
                    "Recipient list is required");
        }

        if (recipientList.getRecipientCount() == 0) {
            throw new InvalidCampaignStateException(
                    "An empty recipient list cannot be assigned to a campaign");
        }

        this.recipientList = recipientList;
    }
}