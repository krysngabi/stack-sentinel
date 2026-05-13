package com.abovebytes.stack.sentinel.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContainerFailure {
    private final String name;
    private final String state;
    private final String status;
    private final String reason;
}