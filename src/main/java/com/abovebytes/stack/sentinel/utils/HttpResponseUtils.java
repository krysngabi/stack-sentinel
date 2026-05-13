package com.abovebytes.stack.sentinel.utils;

import com.abovebytes.stack.sentinel.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collection;

/**
 * Http response utils.
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseUtils {


    private final static ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    /**
     * Convert a code / message / httpStatus to a responseEntity
     *
     * @param code
     * @param status
     * @param message
     * @return
     */
    public static ResponseEntity<ApiError> toResponse(String code, HttpStatus status, String message) {
        return new HttpResponseBuilder()
                .status(status)
                .code(code)
                .message(message)
                .build();
    }

    public static ResponseEntity<ApiError> toResponse(String title, String code, HttpStatus status, String message) {
        return new HttpResponseBuilder()
                .title(title)
                .status(status)
                .code(code)
                .message(message)
                .build();
    }


    /**
     * Convert a collection to a responseEntity
     *
     * @param <T>
     * @param items
     * @return
     */
    public static <T> ResponseEntity<ApiError<Collection<T>>> toListResponse(Collection<T> items) {

        return new HttpResponseBuilder<Collection<T>>()
                .status(HttpStatus.OK)
                .content(items)
                .build();
    }
}
