package com.abovebytes.stack.sentinel.utils;

import com.abovebytes.stack.sentinel.exception.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.OffsetDateTime;

public class HttpResponseBuilder<CONTENT_TYPE> {

    private CONTENT_TYPE content;
    private HttpStatus statusCode = HttpStatus.OK;
    private final HttpHeaders headers = new HttpHeaders();
    private String code;
    private String message;
    private String title;

    public HttpResponseBuilder<CONTENT_TYPE> status(HttpStatus status) {
        this.statusCode = status;
        return this;
    }

    public HttpResponseBuilder<CONTENT_TYPE> authorization(String token) {
        return customHeader(HttpHeaders.AUTHORIZATION, token);
    }

    public HttpResponseBuilder<CONTENT_TYPE> etag(OffsetDateTime timestamp) {
        return customHeader(HttpHeaders.ETAG, timestamp.toString());
    }

    public HttpResponseBuilder<CONTENT_TYPE> contentRange(int start, int end, long total) {
        return customHeader(HttpHeaders.CONTENT_RANGE, start + "-" + end + "/" + total);
    }

    public HttpResponseBuilder<CONTENT_TYPE> customHeader(String header, String value) {
        this.headers.add(header, value);
        return this;
    }

    public HttpResponseBuilder<CONTENT_TYPE> content(CONTENT_TYPE content) {
        this.content = content;
        return this;
    }

    public HttpResponseBuilder<CONTENT_TYPE> code(String code) {
        this.code = code;
        return this;
    }

    public HttpResponseBuilder<CONTENT_TYPE> message(String message) {
        this.message = message;
        return this;
    }

    public HttpResponseBuilder<CONTENT_TYPE> title(String title) {
        this.title = title;
        return this;
    }

    public ResponseEntity<ApiError<CONTENT_TYPE>> build() {
        final ResponseEntity.BodyBuilder entityBodyBuilder = ResponseEntity.status(this.statusCode);

        final ApiError<CONTENT_TYPE> response = new ApiError<>();
        response.setContent(this.content);
        response.setCode(code);
        response.setMessage(message);
        response.setTitle(title);

        return entityBodyBuilder
                .headers(headers)
                .body(response);
    }

}
