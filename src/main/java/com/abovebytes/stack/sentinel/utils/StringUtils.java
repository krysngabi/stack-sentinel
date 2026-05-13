package com.abovebytes.stack.sentinel.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public class StringUtils {
    public static String capitalizeFirstCharacter(String val) {

        if (val == null || val.isEmpty()) {
            return val;
        }

        val = val.toLowerCase();

        char[] arr = val.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);

        return new String(arr);
    }

    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder camelCaseString = new StringBuilder();
        boolean nextWord = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                nextWord = true; // Capitalize next word
            } else {
                if (nextWord) {
                    camelCaseString.append(Character.toUpperCase(currentChar));
                    nextWord = false;
                } else {
                    camelCaseString.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return camelCaseString.toString();
    }

    public static String firstLetterCap(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder camelCaseString = new StringBuilder();
        boolean capitalizeNext = true;

        for (char currentChar : input.toCharArray()) {
            if (Character.isWhitespace(currentChar)) {
                camelCaseString.append(currentChar);
                capitalizeNext = true; // Capitalize next non-space character
            } else if (capitalizeNext) {
                camelCaseString.append(Character.toUpperCase(currentChar));
                capitalizeNext = false;
            } else {
                camelCaseString.append(Character.toLowerCase(currentChar));
            }
        }

        return camelCaseString.toString();
    }

    public static boolean isDigit(String str) {
        return str != null && str.matches("\\d+");
    }

    public static String formatDistance(double distanceMeters) {
        if (distanceMeters < 0) {
            return "0m";
        }

        int kilometers = (int) (distanceMeters / 1000);
        int meters = (int) (distanceMeters % 1000);

        if (kilometers > 0 && meters > 0) {
            return String.format("%dkm %dm", kilometers, meters);
        } else if (kilometers > 0) {
            return String.format("%dkm", kilometers);
        } else {
            return String.format("%dm", meters);
        }
    }

    public static String generateDirectionsLink(BigDecimal fromLat, BigDecimal fromLng, double toLat, double toLng) {
        return String.format(
                "https://www.google.com/maps/dir/%s,%s/%s,%s",
                fromLat, fromLng, toLat, toLng
        );
    }

    public static Map<String, Object> safeReadJson(ObjectMapper mapper, String json) throws JsonProcessingException {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        return mapper.readValue(json, new TypeReference<>() {
        });
    }

}
