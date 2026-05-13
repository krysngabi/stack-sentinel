package com.abovebytes.stack.sentinel.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class ClientCredentialsGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateClientId() {
        return "cli_" + randomString(20);
    }

    public static String generateClientSecret() {
        return "sec_" + randomString(40);
    }

    private static String randomString(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
                .substring(0, length);
    }
}