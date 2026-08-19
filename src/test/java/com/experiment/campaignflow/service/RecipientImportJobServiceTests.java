package com.experiment.campaignflow.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.campaignflow.repository.RecipientImportJobRepository;
import com.experiment.campaignflow.storage.FileStorageService;

class RecipientImportJobServiceTests {

    @Test
    void registrationFailureDeletesPreviouslyStoredFile() {
        RecipientImportJobRepository repository = mock(
                RecipientImportJobRepository.class);
        FileStorageService storage = mock(FileStorageService.class);
        RecipientImportJobRegistrar registrar = mock(
                RecipientImportJobRegistrar.class);
        MultipartFile file = mock(MultipartFile.class);
        UUID recipientListId = UUID.randomUUID();
        String storedPath = "recipient-imports/pending/job/recipients.csv";

        when(file.getOriginalFilename()).thenReturn("recipients.csv");
        when(storage.store(any(UUID.class), eq(file))).thenReturn(storedPath);
        when(registrar.register(
                any(UUID.class),
                eq(recipientListId),
                eq("recipients.csv"),
                eq(storedPath)))
                .thenThrow(new IllegalStateException("Database commit failed"));

        RecipientImportJobService service = new RecipientImportJobService(
                repository,
                storage,
                registrar);

        assertThatThrownBy(() -> service.createImport(recipientListId, file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Database commit failed");

        verify(storage).delete(storedPath);
    }
}
