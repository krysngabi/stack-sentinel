package com.abovebytes.stack.sentinel.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import org.springframework.http.HttpStatus;

@XmlRootElement
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private boolean status = false;
    private String message;
    private HttpStatus httpStatus;
}
