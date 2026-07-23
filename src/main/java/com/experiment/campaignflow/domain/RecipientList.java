package com.experiment.campaignflow.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipient_lists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipientList {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "recipient_count", nullable = false)
    private long recipientCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RecipientList(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.recipientCount = 0;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void incrementRecipientCount() {
        recipientCount++;
    }

    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
    }
}