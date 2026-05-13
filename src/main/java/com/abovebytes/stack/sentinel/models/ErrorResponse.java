package com.abovebytes.stack.sentinel.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String status;
    private String code;
    private String message;
    private String title;
    private String token;
    private String path;
}
