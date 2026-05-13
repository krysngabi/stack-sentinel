package com.abovebytes.stack.sentinel.utils;

import com.abovebytes.stack.sentinel.exception.StackSentinelException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.text.MessageFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * Message utils
 */
@Slf4j
@Component
public class MessageUtils {
    private final MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String message(String key) {
        try {

            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("An error occurred while trying to get the message for {}", key, e);
            throw new StackSentinelException(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
        }
    }



    public String message(String key, Object... args) {
        try {
            String template = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
            return MessageFormat.format(template, args);
        } catch (Exception e) {
            throw new StackSentinelException(HttpStatus.INTERNAL_SERVER_ERROR, messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
        }
    }

    public String messageWithLocale(String key, java.util.Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            log.error("Translation error for key: {}", key);
            return key; // Fallback to key instead of crashing
        }
    }

}

