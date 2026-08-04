package com.example.project.validation;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class InputValidatorTest {
    @Test
    public void validValuesAreNormalized() {
        assertEquals("Madison.Parker", InputValidator.username("  Madison.Parker  "));
        assertEquals("2026-08-01", InputValidator.isoDate("2026-08-01"));
        assertEquals(138.5, InputValidator.weight(138.5), 0.0001);
        assertEquals("weekly check", InputValidator.note("  weekly check  "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedUsernameCharacterIsRejected() {
        InputValidator.username("Madison Parker");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shortPasswordIsRejected() {
        InputValidator.password("short".toCharArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonIsoDateFormatIsRejected() {
        InputValidator.isoDate("August 1, 2026");
    }

    @Test(expected = IllegalArgumentException.class)
    public void impossibleIsoDateIsRejected() {
        InputValidator.isoDate("2026-02-30");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonFiniteWeightIsRejected() {
        InputValidator.weight(Double.NaN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void overlongNoteIsRejected() {
        InputValidator.note("x".repeat(251));
    }
}
