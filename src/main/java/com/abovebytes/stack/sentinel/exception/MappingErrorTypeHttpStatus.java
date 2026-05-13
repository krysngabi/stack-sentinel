package com.abovebytes.stack.sentinel.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mohamed.dantch
 */
@Data
public class MappingErrorTypeHttpStatus {

    private final Map<ErrorTypeEnum, HttpStatus> mapping = new HashMap<>();

    public MappingErrorTypeHttpStatus() {
        this.mapping.put(ErrorTypeEnum.INVALID_EMAIL, HttpStatus.BAD_REQUEST);
        this.mapping.put(ErrorTypeEnum.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
        this.mapping.put(ErrorTypeEnum.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN);
        this.mapping.put(ErrorTypeEnum.MAPPING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        this.mapping.put(ErrorTypeEnum.SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);;

    }
    public HttpStatus getHttpStatus(ErrorTypeEnum errorType) {
        return mapping.get(errorType);
    }
}
