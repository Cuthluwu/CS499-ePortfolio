package com.example.project.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/** Converts the original text-only records into version-two values when possible. */
public final class LegacyDataNormalizer {
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("M/d/uuuu", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT));

    private LegacyDataNormalizer() {
    }

    public static Double parseWeight(String rawWeight) {
        if (rawWeight == null) {
            return null;
        }
        try {
            double parsed = Double.parseDouble(rawWeight.trim());
            return Double.isFinite(parsed) && parsed > 0 && parsed <= 1500 ? parsed : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static String normalizeDate(String rawDate) {
        if (rawDate == null) {
            return null;
        }
        String trimmed = rawDate.trim();
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(trimmed, formatter).toString();
            } catch (DateTimeParseException ignored) {
                // Try the next explicitly supported legacy format.
            }
        }
        return null;
    }
}
