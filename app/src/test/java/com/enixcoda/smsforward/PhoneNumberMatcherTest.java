package com.enixcoda.smsforward;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberMatcherTest {
    @Test
    public void comparesCanonicalNumbersWhenBothNormalize() {
        Map<String, String> normalizedNumbers = new HashMap<>();
        normalizedNumbers.put("(202) 555-0123", "+12025550123");
        normalizedNumbers.put("202.555.0123", "+12025550123");

        assertTrue(PhoneNumberMatcher.areSame(
                "(202) 555-0123",
                "202.555.0123",
                normalizedNumbers::get
        ));
    }

    @Test
    public void rejectsDifferentCanonicalNumbers() {
        Map<String, String> normalizedNumbers = new HashMap<>();
        normalizedNumbers.put("202-555-0123", "+12025550123");
        normalizedNumbers.put("202-555-0199", "+12025550199");

        assertFalse(PhoneNumberMatcher.areSame(
                "202-555-0123",
                "202-555-0199",
                normalizedNumbers::get
        ));
    }

    @Test
    public void fallsBackToExactComparisonWhenNormalizationFails() {
        PhoneNumberMatcher.Normalizer failedNormalizer = number -> null;

        assertTrue(PhoneNumberMatcher.areSame("55501", "55501", failedNormalizer));
        assertFalse(PhoneNumberMatcher.areSame("55501", "55502", failedNormalizer));
    }

    @Test
    public void fallsBackToExactComparisonWhenOnlyOneNumberNormalizes() {
        PhoneNumberMatcher.Normalizer partialNormalizer = number ->
                number.startsWith("+") ? number : null;

        assertFalse(PhoneNumberMatcher.areSame("+12025550123", "2025550123", partialNormalizer));
    }

    @Test
    public void nullNumbersNeverMatchOrReachTheNormalizer() {
        PhoneNumberMatcher.Normalizer unexpectedNormalizer = number -> {
            throw new AssertionError("Normalizer should not be called for null input");
        };

        assertFalse(PhoneNumberMatcher.areSame(null, "+12025550123", unexpectedNormalizer));
        assertFalse(PhoneNumberMatcher.areSame("+12025550123", null, unexpectedNormalizer));
        assertFalse(PhoneNumberMatcher.areSame(null, null, unexpectedNormalizer));
    }
}
