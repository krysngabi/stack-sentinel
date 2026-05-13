package com.abovebytes.stack.sentinel.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;



import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StackSentinelException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String title;
    private final String message;
    private final String token;

    public StackSentinelException(HttpStatus status, String message, String code) {
        super(message);
        this.message = message;
        this.status = status;
        this.code = code;
        this.token = null;
        this.title = null;
    }

    public StackSentinelException(HttpStatus status, String message, String code, String title) {
        super(message);
        this.message = message;
        this.status = status;
        this.code = code;
        this.token = null;
        this.title = title;
    }

    public StackSentinelException(HttpStatus status, String message, String title, boolean isInvalidValue) {
        super(message);
        this.message = message;
        this.status = status;
        this.code = null;
        this.token = null;
        this.title = title;
    }

    public StackSentinelException(HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
        this.code = null;
        this.token = null;
        this.title = null;
    }

    public StackSentinelException(HttpStatus status, String message, String code, String token, String title) {
        super(message);
        this.message = message;
        this.status = status;
        this.code = code;
        this.token = token;
        this.title = title;
    }
}
