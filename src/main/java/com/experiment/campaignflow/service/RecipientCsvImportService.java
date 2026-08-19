package com.experiment.campaignflow.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.experiment.campaignflow.domain.Recipient;
import com.experiment.campaignflow.domain.RecipientList;
import com.experiment.campaignflow.dto.RecipientImportError;
import com.experiment.campaignflow.dto.RecipientImportResponse;
import com.experiment.campaignflow.exception.RecipientListNotFoundException;
import com.experiment.campaignflow.repository.RecipientListRepository;
import com.experiment.campaignflow.repository.RecipientRepository;

@Service
public class RecipientCsvImportService {

    private static final long MAX_ROWS = 5_000;

    private static final Set<String> REQUIRED_HEADERS = Set.of("email", "firstName", "lastName");

    private static final Pattern SIMPLE_EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final RecipientListRepository recipientListRepository;
    private final RecipientRepository recipientRepository;

    public RecipientCsvImportService(
            RecipientListRepository recipientListRepository,
            RecipientRepository recipientRepository) {
        this.recipientListRepository = recipientListRepository;
        this.recipientRepository = recipientRepository;
    }

    @Transactional
    public RecipientImportResponse importRecipients(
            UUID recipientListId,
            MultipartFile file) {
        RecipientList recipientList = recipientListRepository
                .findById(recipientListId)
                .orElseThrow(
                        () -> new RecipientListNotFoundException(recipientListId));

        validateFile(file);

        List<ParsedRecipientRow> parsedRows = new ArrayList<>();
        List<RecipientImportError> errors = new ArrayList<>();

        parseCsv(file, parsedRows, errors);

        Set<String> emailsFromFile = new HashSet<>();

        for (ParsedRecipientRow row : parsedRows) {
            emailsFromFile.add(row.email());
        }

        Set<String> existingEmails = recipientRepository
                .findByRecipientListIdAndEmailIn(
                        recipientListId,
                        emailsFromFile)
                .stream()
                .map(Recipient::getEmail)
                .collect(java.util.stream.Collectors.toSet());

        Set<String> emailsSeenInCurrentFile = new HashSet<>();
        List<Recipient> recipientsToSave = new ArrayList<>();

        long duplicateRows = 0;

        for (ParsedRecipientRow row : parsedRows) {
            boolean alreadyExistsInDatabase = existingEmails.contains(row.email());

            boolean duplicatedInsideFile = !emailsSeenInCurrentFile.add(row.email());

            if (alreadyExistsInDatabase || duplicatedInsideFile) {
                duplicateRows++;

                errors.add(
                        new RecipientImportError(
                                row.rowNumber(),
                                row.email(),
                                alreadyExistsInDatabase
                                        ? "Recipient already exists in this list"
                                        : "Duplicate email inside CSV file"));

                continue;
            }

            recipientsToSave.add(
                    new Recipient(
                            recipientList,
                            row.email(),
                            row.firstName(),
                            row.lastName()));
        }

        recipientRepository.saveAll(recipientsToSave);

        recipientList.incrementRecipientCountBy(
                recipientsToSave.size());

        long invalidRows = errors.stream()
                .filter(error -> !error.reason().contains("Duplicate")
                        && !error.reason().contains("already exists"))
                .count();

        return new RecipientImportResponse(
                recipientListId,
                file.getOriginalFilename(),
                parsedRows.size() + invalidRows,
                recipientsToSave.size(),
                duplicateRows,
                invalidRows,
                List.copyOf(errors));
    }

    private void parseCsv(
            MultipartFile file,
            List<ParsedRecipientRow> parsedRows,
            List<RecipientImportError> errors) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                StandardCharsets.UTF_8));

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .get()
                        .parse(reader)) {
            validateHeaders(parser);

            long rowCount = 0;

            for (CSVRecord record : parser) {
                rowCount++;

                if (rowCount > MAX_ROWS) {
                    throw new IllegalArgumentException(
                            "CSV file must not contain more than "
                                    + MAX_ROWS
                                    + " rows");
                }

                long displayedRowNumber = record.getRecordNumber() + 1;

                String email = normalize(record.get("email"));
                String firstName = nullableTrim(
                        record.get("firstName"));
                String lastName = nullableTrim(
                        record.get("lastName"));

                String validationError = validateRow(
                        email,
                        firstName,
                        lastName);

                if (validationError != null) {
                    errors.add(
                            new RecipientImportError(
                                    displayedRowNumber,
                                    email,
                                    validationError));

                    continue;
                }

                parsedRows.add(
                        new ParsedRecipientRow(
                                displayedRowNumber,
                                email,
                                firstName,
                                lastName));
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to read CSV file",
                    exception);
        }
    }

    private void validateFile(MultipartFile file) {
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

    void validateHeaders(CSVParser parser) {
        Set<String> headers = parser.getHeaderMap().keySet();

        if (!headers.containsAll(REQUIRED_HEADERS)) {
            throw new IllegalArgumentException(
                    "CSV must contain headers: email, firstName, lastName");
        }
    }

    private String validateRow(
            String email,
            String firstName,
            String lastName) {
        if (email == null || email.isBlank()) {
            return "Email is required";
        }

        if (email.length() > 320) {
            return "Email must not exceed 320 characters";
        }

        if (!isValidEmail(email)) {
            return "Invalid email address";
        }

        if (firstName != null && firstName.length() > 100) {
            return "First name must not exceed 100 characters";
        }

        if (lastName != null && lastName.length() > 100) {
            return "Last name must not exceed 100 characters";
        }

        return null;
    }

    String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    String nullableTrim(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    boolean isValidEmail(String email) {
        return email != null
                && !email.isBlank()
                && email.length() <= 320
                && SIMPLE_EMAIL_PATTERN.matcher(email).matches();
    }

    private record ParsedRecipientRow(
            long rowNumber,
            String email,
            String firstName,
            String lastName) {
    }
}
