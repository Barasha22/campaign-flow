package com.experiment.campaignflow.storage;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(UUID importJobId, MultipartFile file);

    InputStream open(String storedPath);

    void delete(String storedPath);
}
