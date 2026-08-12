package com.enixcoda.smsforward;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReverseMessageParserTest {
    @Test
    public void detectsCommandsCaseInsensitively() {
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to +12025550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("TO +12025550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("To\t+12025550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to"));
    }

    @Test
    public void detectsLikelyCommandsThatNeedBadFormatFeedback() {
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to+12025550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to12025550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to(202)5550123: hello"));
        assertTrue(ReverseMessageParser.isAttemptingReverseMessage("to: hello"));
    }

    @Test
    public void doesNotTreatOrdinaryWordsAsCommands() {
        String[] ordinaryMessages = {
                null,
                "",
                "today should work",
                "tomorrow should work",
                "token expired",
                "toward the station",
                " to +12025550123: leading whitespace",
                "to-do list",
                "to.be continued"
        };

        for (String message : ordinaryMessages)
            assertFalse(message, ReverseMessageParser.isAttemptingReverseMessage(message));
    }

    @Test
    public void parsesDottedAndFormattedPhoneNumbers() {
        assertParsedMessage(
                "to 202.555.0123:\nhello",
                "202.555.0123",
                "hello"
        );
        assertParsedMessage(
                "TO   +1 (202) 555-0123:\nhello",
                "+1 (202) 555-0123",
                "hello"
        );
    }

    @Test
    public void trimsOnlyThePhoneNumber() {
        assertParsedMessage(
                "to   +12025550123   :  preserve message spacing  ",
                "+12025550123",
                "  preserve message spacing  "
        );
    }

    @Test
    public void acceptsSameLineLfAndCrLfBodies() {
        assertParsedMessage("to +12025550123:hello", "+12025550123", "hello");
        assertParsedMessage("to +12025550123:\nhello", "+12025550123", "hello");
        assertParsedMessage("to +12025550123:\r\nhello", "+12025550123", "hello");
    }

    @Test
    public void preservesMultilineBodiesAndColons() {
        assertParsedMessage(
                "to +12025550123:\nMeet at 10:30\nBring:\n- ID\n- ticket",
                "+12025550123",
                "Meet at 10:30\nBring:\n- ID\n- ticket"
        );
    }

    @Test
    public void emptyAndWhitespaceOnlyBodiesStillParseForSpecificFeedback() {
        String[] blankBodies = {
                "to +12025550123:",
                "to +12025550123:\n",
                "to +12025550123:\r\n",
                "to +12025550123: \t\n",
                "to +12025550123:\n\u2003"
        };

        for (String command : blankBodies) {
            ReverseMessageParser.Result result = ReverseMessageParser.parse(command);
            assertTrue(command, result.isValid());
            assertTrue(command, ReverseMessageParser.isBlank(result.getMessageContent()));
        }
    }

    @Test
    public void nonWhitespaceBodyIsNotBlank() {
        assertFalse(ReverseMessageParser.isBlank(" \n message \t"));
        assertFalse(ReverseMessageParser.isBlank("0"));
    }

    @Test
    public void rejectsStructurallyInvalidCommands() {
        String[] invalidCommands = {
                null,
                "",
                "today",
                "to+12025550123: hello",
                "to +12025550123",
                "to\n+12025550123: hello",
                "to +12025550123\nextra: hello"
        };

        for (String command : invalidCommands) {
            ReverseMessageParser.Result result = ReverseMessageParser.parse(command);
            assertFalse(command, result.isValid());
            assertNull(command, result.getPhoneNumber());
            assertNull(command, result.getMessageContent());
        }
    }

    @Test
    public void blankPhoneIsLeftForPhoneNumberValidation() {
        ReverseMessageParser.Result result = ReverseMessageParser.parse("to    : hello");

        assertTrue(result.isValid());
        assertEquals("", result.getPhoneNumber());
        assertEquals(" hello", result.getMessageContent());
    }

    private static void assertParsedMessage(String command, String phoneNumber, String content) {
        ReverseMessageParser.Result result = ReverseMessageParser.parse(command);

        assertTrue(command, result.isValid());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(content, result.getMessageContent());
    }
}
