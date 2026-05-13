package com.abovebytes.stack.sentinel.exception;

import com.abovebytes.stack.sentinel.models.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.abovebytes.stack.sentinel.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@PropertySource(value = "classpath:messages.properties", encoding = "UTF-8")
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    private final MessageSource messageSource;
    private final MessageUtils messageUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private final MappingErrorTypeHttpStatus mappingErrorStatus = new MappingErrorTypeHttpStatus();

    public CustomExceptionHandler(MessageSource messageSource, MessageUtils messageUtils) {
        this.messageSource = messageSource;
        this.messageUtils = messageUtils;
    }

    private ResponseEntity<ErrorDetails> getResponseForException(Exception ex, String code, String customMessage, HttpStatus status, WebRequest request) {
        final String message = customMessage != null
                ? customMessage
                : ex.getMessage();
        log.error(message, ex);
        return new ResponseEntity<>(
                new ErrorDetails(code, message, request.getDescription(false)),
                status);
    }

    private ResponseEntity<ErrorDetails> getResponseForException(Exception ex, String code, HttpStatus status, WebRequest request) {
        return getResponseForException(ex, code, null, status, request);
    }

    @ExceptionHandler(StackSentinelException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(StackSentinelException ex, WebRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .status(ex.getStatus().getReasonPhrase())
                .code(ex.getCode())
                .message(ex.getMessage())
                .title(ex.getTitle())
                .token(ex.getToken())
                .path(request.getDescription(false).substring(4))
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    private HttpStatus getStatusFromErrorType(ErrorTypeEnum errorType) {
        final HttpStatus status = mappingErrorStatus.getHttpStatus(errorType);
        return status != null ? status : HttpStatus.BAD_REQUEST;
    }

    /**
     * Mapping exceptions
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({
        JsonMappingException.class,
        JsonParseException.class,
        MismatchedInputException.class})
    @ResponseBody
    public final ResponseEntity<ErrorDetails> handleMappingExceptions(Exception ex, WebRequest request) {
        final ErrorTypeEnum errorType = ErrorTypeEnum.MAPPING_ERROR;
        return getResponseForException(ex,
                errorType.name(),
                getStatusFromErrorType(errorType),
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(
            HttpMessageNotReadableException ex, WebRequest request, Locale locale) {

        log.error("HttpMessageNotReadableException", ex);

        String errorMessage = null;

        Throwable cause = ex.getCause();

        while (cause != null) {
            if (cause instanceof StackSentinelException ce) {
                Locale currentLocale = LocaleContextHolder.getLocale();
                errorMessage = messageSource.getMessage("invalid.value", new Object[]{ce.getMessage()}, currentLocale);
                break;
            }
            cause = cause.getCause();
        }

        if (errorMessage == null) {
            errorMessage = messageSource.getMessage("invalid.value", new Object[]{"Unknown"}, locale);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", HttpStatus.BAD_REQUEST.value());
        responseData.put("error", "Bad Request");
        responseData.put("message", errorMessage);
        responseData.put("path", request.getDescription(false).substring(4));

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
        final ErrorTypeEnum errorType = ErrorTypeEnum.SERVER_ERROR;
        final String message = messageUtils.message("internal.error");
        return getResponseForException(ex, errorType.name(), message, getStatusFromErrorType(errorType), request);
    }
}
