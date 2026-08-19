package com.experiment.campaignflow.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(
        name = "campaignflow.storage.type",
        havingValue = "local",
        matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private final Path storageDirectory;

    public LocalFileStorageService(
            @Value("${campaignflow.storage.local.directory}") String storageDirectory) {
        this.storageDirectory = Path.of(storageDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to initialize import storage directory",
                    exception);
        }
    }

    @Override
    public String store(UUID importJobId, MultipartFile file) {
        validateFileName(file);

        String originalName = file.getOriginalFilename();

        String storedName = Path.of(originalName)
                .getFileName()
                .toString();

        Path destination = storageDirectory
                .resolve(importJobId.toString())
                .resolve(storedName)
                .normalize();

        if (!destination.startsWith(storageDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid file destination");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.createDirectories(destination.getParent());
            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return destination.toString();
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to store uploaded file",
                    exception);
        }
    }

    @Override
    public InputStream open(String storedPath) {
        try {
            return Files.newInputStream(Path.of(storedPath));
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to open stored import file",
                    exception);
        }
    }

    @Override
    public void delete(String storedPath) {
        try {
            Files.deleteIfExists(Path.of(storedPath));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to delete stored import file",
                    exception);
        }
    }

    private void validateFileName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "CSV file is required");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null
                || !fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "Only CSV files are supported");
        }
    }
}
