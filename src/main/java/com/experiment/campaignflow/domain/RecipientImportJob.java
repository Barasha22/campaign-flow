package com.experiment.campaignflow.domain;

import java.time.Instant;
import java.util.UUID;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipient_import_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipientImportJob {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_list_id", nullable = false)
    private RecipientList recipientList;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "stored_file_path", nullable = false, length = 1000)
    private String storedFilePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecipientImportStatus status;

    @Column(name = "total_rows", nullable = false)
    private long totalRows;

    @Column(name = "processed_rows", nullable = false)
    private long processedRows;

    @Column(name = "imported_rows", nullable = false)
    private long importedRows;

    @Column(name = "duplicate_rows", nullable = false)
    private long duplicateRows;

    @Column(name = "invalid_rows", nullable = false)
    private long invalidRows;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RecipientImportJob(
        UUID id,
        RecipientList recipientList,
        String originalFileName,
        String storedFilePath
    ) {
        this.id = id;
        this.recipientList = recipientList;
        this.originalFileName = originalFileName;
        this.storedFilePath = storedFilePath;
        this.status = RecipientImportStatus.PENDING;
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

    public void start() {
        if (status != RecipientImportStatus.PENDING) {
            throw new IllegalStateException(
                "Only a pending import job can be started"
            );
        }

        status = RecipientImportStatus.PROCESSING;
        startedAt = Instant.now();
        errorMessage = null;
    }

    public void updateProgress(
        long totalRows,
        long processedRows,
        long importedRows,
        long duplicateRows,
        long invalidRows
    ) {
        this.totalRows = totalRows;
        this.processedRows = processedRows;
        this.importedRows = importedRows;
        this.duplicateRows = duplicateRows;
        this.invalidRows = invalidRows;
    }

    public void complete() {
        status = RecipientImportStatus.COMPLETED;
        completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        status = RecipientImportStatus.FAILED;
        this.errorMessage = errorMessage;
        completedAt = Instant.now();
    }
}
