package com.enixcoda.smsforward;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ForwarderTest {
    @Test
    public void shortMessagesStayTogetherInEitherMode() {
        assertMessages(false, "From Alice:\nHello", "Alice", "Hello");
        assertMessages(true, "From Alice:\nHello", "Alice", "Hello");
    }

    @Test
    public void longMessagesUseLegacySeparationByDefault() {
        String content = repeat('x', 121);

        assertMessages(false, "From Alice:\n", content, "Alice", content);
    }

    @Test
    public void longMessagesStayLogicallyTogetherWhenRequested() {
        String content = repeat('x', 121);

        assertMessages(true, "From Alice:\n" + content, "Alice", content);
    }

    @Test
    public void legacyModeKeepsExactly120Utf8BytesTogether() {
        String prefix = "From Alice:\n";
        String content = repeat('x', Forwarder.MAX_SMS_LENGTH - prefix.length());

        assertMessages(false, prefix + content, "Alice", content);
    }

    @Test
    public void legacyModeSeparatesAt121Utf8Bytes() {
        String prefix = "From Alice:\n";
        String content = repeat('x', Forwarder.MAX_SMS_LENGTH - prefix.length() + 1);

        assertMessages(false, prefix, content, "Alice", content);
    }

    @Test
    public void legacyThresholdKeepsExactly120UnicodeBytesTogether() {
        String prefix = "From Alice:\n";
        String content = repeat('你', 36);

        assertMessages(false, prefix + content, "Alice", content);
    }

    @Test
    public void legacyThresholdSeparatesUnicodeOver120Bytes() {
        String prefix = "From Alice:\n";
        String content = repeat('你', 37);

        assertMessages(false, prefix, content, "Alice", content);
    }

    private static void assertMessages(boolean keepTogether, String expected,
                                       String sender, String content) {
        ArrayList<String> messages = Forwarder.buildSmsMessages(sender, content, keepTogether);

        assertEquals(1, messages.size());
        assertEquals(expected, messages.get(0));
    }

    private static void assertMessages(boolean keepTogether, String first, String second,
                                       String sender, String content) {
        ArrayList<String> messages = Forwarder.buildSmsMessages(sender, content, keepTogether);

        assertEquals(2, messages.size());
        assertEquals(first, messages.get(0));
        assertEquals(second, messages.get(1));
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++)
            result.append(value);
        return result.toString();
    }
}
