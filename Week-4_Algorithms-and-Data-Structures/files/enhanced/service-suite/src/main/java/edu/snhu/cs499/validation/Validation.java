package edu.snhu.cs499.validation;

import java.time.Clock;
import java.util.Date;

/** Shared validation rules used by the three model classes. */
public final class Validation {
    private Validation() {
    }

    public static String requiredText(String value, String fieldName, int maximumLength) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be longer than " + maximumLength + " characters.");
        }
        return normalized;
    }

    public static String identifier(String value, String fieldName) {
        return requiredText(value, fieldName, 10);
    }

    public static String searchTerm(String value, String fieldName) {
        return requiredText(value, fieldName, 100);
    }

    public static String phone(String value) {
        String normalized = requiredText(value, "Phone number", 10);
        if (!normalized.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        return normalized;
    }

    public static Date presentOrFutureDate(Date value, Clock clock) {
        if (value == null) {
            throw new IllegalArgumentException("Appointment date is required.");
        }
        if (value.toInstant().isBefore(clock.instant())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }
        return new Date(value.getTime());
    }
}
