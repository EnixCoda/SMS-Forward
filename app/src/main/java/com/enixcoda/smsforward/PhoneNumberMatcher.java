package com.enixcoda.smsforward;

final class PhoneNumberMatcher {
    interface Normalizer {
        String normalize(String phoneNumber);
    }

    private PhoneNumberMatcher() {
    }

    static boolean areSame(String phoneNumber1, String phoneNumber2, Normalizer normalizer) {
        if (phoneNumber1 == null || phoneNumber2 == null)
            return false;

        String normalizedNumber1 = normalizer.normalize(phoneNumber1);
        String normalizedNumber2 = normalizer.normalize(phoneNumber2);

        if (normalizedNumber1 != null && normalizedNumber2 != null)
            return normalizedNumber1.equals(normalizedNumber2);

        return phoneNumber1.equals(phoneNumber2);
    }
}
