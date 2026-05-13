package com.abovebytes.stack.sentinel.enums;

import com.abovebytes.stack.sentinel.exception.StackSentinelException;
import com.abovebytes.stack.sentinel.utils.MessageUtils;
import org.springframework.http.HttpStatus;

import java.util.Locale;

public enum AllowedApps {
    GLOBAL,
    CC_PAY,
    CENTRAL,
    SYSTEM;

    public static AllowedApps fromString(String allowedApp, MessageUtils messageUtils, Locale locale) {
        try {
            return AllowedApps.valueOf(allowedApp.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String msg = messageUtils.message(
                    "invalid.value",   // key in messages.properties
                    allowedApp, // params for {0} in messages
                    "Invalid value",    // default
                    locale
            );
            throw new StackSentinelException(HttpStatus.BAD_REQUEST, msg, allowedApp, "400");
        }
    }
}
