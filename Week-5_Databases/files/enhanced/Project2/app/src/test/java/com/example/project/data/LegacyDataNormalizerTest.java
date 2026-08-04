package com.example.project.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class LegacyDataNormalizerTest {
    @Test
    public void supportedLegacyDatesNormalizeToIso() {
        assertEquals("2026-06-22", LegacyDataNormalizer.normalizeDate("June 22, 2026"));
        assertEquals("2026-06-22", LegacyDataNormalizer.normalizeDate("6/22/2026"));
        assertEquals("2026-06-22", LegacyDataNormalizer.normalizeDate("2026-06-22"));
    }

    @Test
    public void validLegacyWeightIsConvertedToNumericValue() {
        assertEquals(138.5, LegacyDataNormalizer.parseWeight(" 138.5 "), 0.0001);
    }

    @Test
    public void invalidLegacyValuesAreNotSilentlyConverted() {
        assertNull(LegacyDataNormalizer.parseWeight("not-a-number"));
        assertNull(LegacyDataNormalizer.parseWeight("0"));
        assertNull(LegacyDataNormalizer.normalizeDate("February 30, 2026"));
    }
}
