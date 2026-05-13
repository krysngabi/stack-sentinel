package com.abovebytes.stack.sentinel.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApiModel(description = "Objet contenant les informations d'une erreur")
@Getter
@Setter
public class ErrorDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Horodatage de l'erreur")
    private final Date timestamp;

    @ApiModelProperty(value = "Code de l'erreur")
    private String code;

    @ApiModelProperty(value = "Messages fonctionnels")
    private final List<String> messages = new ArrayList<>();

    @ApiModelProperty(value = "Détails techniques")
    private String details;

    public ErrorDetails() {
        this(null, null);
    }

    public ErrorDetails(String code, String message) {
        this(code, message, null);
    }

    public ErrorDetails(String code, String message, String details) {
        this.code = code;
        this.timestamp = new Date();
        this.messages.add(message);
        this.details = details;
    }
}
