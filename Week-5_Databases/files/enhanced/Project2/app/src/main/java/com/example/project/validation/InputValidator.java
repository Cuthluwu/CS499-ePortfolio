package com.example.project.validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Shared validation used before data reaches SQLite. */
public final class InputValidator {
    public static final int MAX_USERNAME_LENGTH = 40;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MAX_NOTE_LENGTH = 250;
    public static final double MAX_WEIGHT = 1500.0;

    private InputValidator() {
    }

    public static String username(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Username is required.");
        }
        String normalized = value.trim();
        if (normalized.length() < 3 || normalized.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username must contain 3 to 40 characters.");
        }
        if (!normalized.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException(
                    "Username may use letters, numbers, periods, underscores, and hyphens.");
        }
        return normalized;
    }

    public static char[] password(char[] value) {
        if (value == null || value.length < MIN_PASSWORD_LENGTH || value.length > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must contain 8 to 128 characters.");
        }
        return value;
    }

    public static long positiveId(long value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number.");
        }
        return value;
    }

    public static double weight(double value) {
        if (!Double.isFinite(value) || value <= 0 || value > MAX_WEIGHT) {
            throw new IllegalArgumentException("Weight must be greater than 0 and no more than 1500.");
        }
        return value;
    }

    public static String isoDate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Entry date is required in YYYY-MM-DD format.");
        }
        String normalized = value.trim();
        try {
            return LocalDate.parse(normalized).toString();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Entry date must use YYYY-MM-DD format.");
        }
    }

    public static String note(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException("Note must contain 250 characters or fewer.");
        }
        return normalized;
    }
}
