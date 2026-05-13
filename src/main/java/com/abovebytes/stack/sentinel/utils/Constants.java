package com.abovebytes.stack.sentinel.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String CENTRAL_APP_NAME = "central-app-name";
    private static final String PAYMENT_STATUS_VALUE = "Paiement";
    public static final String SMTP_USERNAME = "smtp-username";
    public static final String SMTP_PASSWORD = "smtp-password";
    public static final String SMTP_HOST = "smtp-host";
    public static final String DOCKER_CONTAINER_DOWN_TEMPLATE = "docker-container-down";
    public static final String FORGOT_PASSWORD_TEMPLATE = "forgot-password-";
    public static final String TEMP_PASSWORD_TEMPLATE = "temp-password-";
    public static final String DOCKER_CONTAINER_LIST = "docker-container-to-monitor";
    public static final String CRITICAL_NOTIFICATION = "critical-email-to";
    public static final String MONITOR_INTERVAL_HOURS = "monitor-interval-in-hours";
    public static final String DEFAULT_OTP_LENGTH = "6";

    public static String[] AUTHORIZED_PATHS = new String[] {
            "/users/login", "/twilio/validate-otp", "/twilio/send-otp",
            "/email/ccpay", "/twilio/receive/**", "/twilio/fallback", "/actuator/**", "/email/docker/container", "/email/central/forgot-password",
            "/email/central/temp-password",  "/properties/search-name", "/properties/search-app",
            "/authenticate","/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui.html",  "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**"};

    public static final Map<String, String> OPERATIONS_MAP = new HashMap<>();
    static{
        OPERATIONS_MAP.put("2", PAYMENT_STATUS_VALUE);
    }
}
