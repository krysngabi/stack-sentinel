package com.abovebytes.stack.sentinel.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;


@Component
@Slf4j
public class HttpHelper {
    private final MessageUtils messageUtils;

    public HttpHelper(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public ResponseEntity<?> notFoundResponse(String title, String messageKey, Object arg) {
        return HttpResponseUtils.toResponse(title, "404", HttpStatus.NOT_FOUND, messageUtils.message(messageKey, arg));
    }

    public ResponseEntity<?> notFoundResponse(String title, String message) {
        return HttpResponseUtils.toResponse(title, "404", HttpStatus.NOT_FOUND, message);
    }

    public ResponseEntity<?> internalError(String title, String messageKey, Object arg) {
        return HttpResponseUtils.toResponse(title, "500", HttpStatus.INTERNAL_SERVER_ERROR, messageUtils.message(messageKey, arg));
    }

    public ResponseEntity<?> internalError(String title, String message) {
        return HttpResponseUtils.toResponse(title, "500", HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public ResponseEntity<?> conflictResponse(String title, String messageKey, Object arg) {
        return HttpResponseUtils.toResponse(title, "409", HttpStatus.CONFLICT, messageUtils.message(messageKey, arg));
    }

    public ResponseEntity<?> conflictResponse(String title, String messageKey, Object... args) {
        return HttpResponseUtils.toResponse(
                title,
                "409",
                HttpStatus.CONFLICT,
                messageUtils.message(messageKey, args)
        );
    }

    public ResponseEntity<?> conflictResponse(String title, String message) {
        return HttpResponseUtils.toResponse(
                title,
                "409",
                HttpStatus.CONFLICT,
                message
        );
    }

    public ResponseEntity<?> toResponseHtt(String title, String messageKey, Object... args) {
        return HttpResponseUtils.toResponse(
                title,
                "409",
                HttpStatus.CONFLICT,
                messageUtils.message(messageKey, args)
        );
    }

    public ResponseEntity<?> badRequestResponse(String title, String messageKey) {
        return HttpResponseUtils.toResponse(title, "400", HttpStatus.BAD_REQUEST, messageUtils.message(messageKey));
    }

    public ResponseEntity<?> badRequestResponse(String title, String messageKey, Object params) {
        return HttpResponseUtils.toResponse(
                title,
                "400",
                HttpStatus.BAD_REQUEST,
                messageUtils.message(messageKey, params)
        );
    }

    public ResponseEntity<?> handleValidationErrors(BindingResult result, String title) {
        log.error("Validation errors: {}", result.getAllErrors());
        String errors = result.getAllErrors().stream()
                .map(error -> messageUtils.message(error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return HttpResponseUtils.toResponse(title, "400", HttpStatus.BAD_REQUEST, errors);
    }
}
