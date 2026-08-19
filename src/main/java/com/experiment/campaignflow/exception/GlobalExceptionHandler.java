package com.experiment.campaignflow.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(CampaignNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleCampaignNotFound(
                        CampaignNotFoundException exception,
                        WebRequest request) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exception.getMessage(),
                                extractPath(request));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(
                        MethodArgumentNotValidException exception,
                        WebRequest request) {
                String message = exception.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message,
                                extractPath(request));

                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(InvalidCampaignStateException.class)
        public ResponseEntity<ApiErrorResponse> handleInvalidCampaignState(
                        InvalidCampaignStateException exception,
                        WebRequest request) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                exception.getMessage(),
                                extractPath(request));

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(response);
        }

        private String extractPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }

        @ExceptionHandler(MessageTemplateNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleTemplateNotFound(
                        MessageTemplateNotFoundException exception,
                        WebRequest request) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                exception.getMessage(),
                                extractPath(request));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        @ExceptionHandler(DuplicateTemplateNameException.class)
        public ResponseEntity<ApiErrorResponse> handleDuplicateTemplateName(
                        DuplicateTemplateNameException exception,
                        WebRequest request) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                exception.getMessage(),
                                extractPath(request));

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(response);
        }

        @ExceptionHandler(RecipientListNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleRecipientListNotFound(
                        RecipientListNotFoundException exception,
                        WebRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiErrorResponse(
                                                Instant.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                exception.getMessage(),
                                                extractPath(request)));
        }

        @ExceptionHandler(DuplicateRecipientListNameException.class)
        public ResponseEntity<ApiErrorResponse> handleDuplicateRecipientListName(
                        DuplicateRecipientListNameException exception,
                        WebRequest request) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                new ApiErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                exception.getMessage(),
                                                extractPath(request)));
        }

        @ExceptionHandler(DuplicateRecipientException.class)
        public ResponseEntity<ApiErrorResponse> handleDuplicateRecipient(
                        DuplicateRecipientException exception,
                        WebRequest request) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                new ApiErrorResponse(
                                                Instant.now(),
                                                HttpStatus.CONFLICT.value(),
                                                HttpStatus.CONFLICT.getReasonPhrase(),
                                                exception.getMessage(),
                                                extractPath(request)));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
                        IllegalArgumentException exception,
                        WebRequest request) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                exception.getMessage(),
                                extractPath(request));

                return ResponseEntity
                                .badRequest()
                                .body(response);
        }

        @ExceptionHandler(RecipientImportJobNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleImportJobNotFound(
                        RecipientImportJobNotFoundException exception,
                        WebRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ApiErrorResponse(
                                                Instant.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                                exception.getMessage(),
                                                extractPath(request)));
        }
}