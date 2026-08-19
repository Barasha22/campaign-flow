package com.experiment.campaignflow.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.repository.RecipientListRepository;
import com.experiment.campaignflow.storage.FileStorageService;

@Service
public class RecipientImportProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            RecipientImportProcessor.class);

    private static final int BATCH_SIZE = 500;

    private final RecipientImportJobService importJobService;
    private final RecipientImportJobUpdater jobUpdater;
    private final RecipientListRepository recipientListRepository;
    private final RecipientImportBatchService batchService;
    private final FileStorageService fileStorageService;
    private final RecipientCsvImportService csvImportService;

    public RecipientImportProcessor(
            RecipientImportJobService importJobService,
            RecipientImportJobUpdater jobUpdater,
            RecipientListRepository recipientListRepository,
            RecipientImportBatchService batchService,
            FileStorageService fileStorageService,
            RecipientCsvImportService csvImportService) {
        this.importJobService = importJobService;
        this.jobUpdater = jobUpdater;
        this.recipientListRepository = recipientListRepository;
        this.batchService = batchService;
        this.fileStorageService = fileStorageService;
        this.csvImportService = csvImportService;
    }

    public void process(UUID jobId) {

        try {

            if (!jobUpdater.tryMarkStarted(jobId)) {
                return;
            }

            RecipientImportJobDetails details = importJobService.getJobDetails(jobId);

            processFile(details);

            jobUpdater.markCompleted(jobId);

        } catch (Exception exception) {

            jobUpdater.markFailed(
                    jobId,
                    exception.getMessage());

            return;
        }

        try {
            RecipientImportJobDetails details = importJobService.getJobDetails(jobId);
            fileStorageService.delete(details.storedFilePath());
        } catch (Exception exception) {
            LOGGER.warn(
                    "Import {} completed, but its source file could not be deleted",
                    jobId,
                    exception);
        }
    }

    private void processFile(
            RecipientImportJobDetails details) throws Exception {

        RecipientList recipientList = recipientListRepository
                .findById(details.recipientListId())
                .orElseThrow();

        long totalRows = 0;
        long processedRows = 0;
        long importedRows = 0;
        long duplicateRows = 0;
        long invalidRows = 0;

        List<RecipientImportRow> batch = new ArrayList<>(BATCH_SIZE);

        Set<String> emailsSeenInFile = new HashSet<>();

        try (
                InputStream inputStream = fileStorageService.open(
                        details.storedFilePath());

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                inputStream,
                                StandardCharsets.UTF_8));

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .get()
                        .parse(reader)) {

            csvImportService.validateHeaders(parser);

            for (CSVRecord record : parser) {

                totalRows++;

                String email = csvImportService.normalize(record.get("email"));

                String firstName = csvImportService.nullableTrim(
                        record.get("firstName"));

                String lastName = csvImportService.nullableTrim(
                        record.get("lastName"));

                processedRows++;

                if (!csvImportService.isValidEmail(email)) {
                    invalidRows++;
                    continue;
                }

                if (!emailsSeenInFile.add(email)) {
                    duplicateRows++;
                    continue;
                }

                batch.add(
                        new RecipientImportRow(
                                record.getRecordNumber() + 1,
                                email,
                                firstName,
                                lastName));

                if (batch.size() >= BATCH_SIZE) {
                    int candidateRows = batch.size();
                    int savedRows = batchService.saveBatch(
                            recipientList,
                            batch);

                    importedRows += savedRows;
                    duplicateRows += candidateRows - savedRows;

                    batch.clear();

                    jobUpdater.updateProgress(
                            details.jobId(),
                            totalRows,
                            processedRows,
                            importedRows,
                            duplicateRows,
                            invalidRows);
                }
            }

            if (!batch.isEmpty()) {
                int candidateRows = batch.size();
                int savedRows = batchService.saveBatch(
                        recipientList,
                        batch);

                importedRows += savedRows;
                duplicateRows += candidateRows - savedRows;

                batch.clear();
            }

            jobUpdater.updateProgress(
                    details.jobId(),
                    totalRows,
                    processedRows,
                    importedRows,
                    duplicateRows,
                    invalidRows);
        }
    }
}
