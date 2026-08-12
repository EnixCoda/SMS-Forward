package com.enixcoda.smsforward;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ReverseMessageParser {
    private static final Pattern ATTEMPT_PATTERN = Pattern.compile(
            "^to(?=\\s|[+\\d(:]|$)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^to[ \\t]+([^:\\r\\n]+):(?:\\r?\\n)?([\\s\\S]*)$",
            Pattern.CASE_INSENSITIVE
    );

    private ReverseMessageParser() {
    }

    static boolean isAttemptingReverseMessage(String content) {
        return content != null && ATTEMPT_PATTERN.matcher(content).find();
    }

    static Result parse(String content) {
        if (content == null)
            return Result.invalid();

        Matcher matcher = MESSAGE_PATTERN.matcher(content);
        if (!matcher.matches())
            return Result.invalid();

        return Result.valid(matcher.group(1).trim(), matcher.group(2));
    }

    static boolean isBlank(String content) {
        if (content == null || content.isEmpty())
            return true;

        for (int i = 0; i < content.length(); i++) {
            if (!Character.isWhitespace(content.charAt(i)))
                return false;
        }
        return true;
    }

    static final class Result {
        private final boolean valid;
        private final String phoneNumber;
        private final String messageContent;

        private Result(boolean valid, String phoneNumber, String messageContent) {
            this.valid = valid;
            this.phoneNumber = phoneNumber;
            this.messageContent = messageContent;
        }

        static Result invalid() {
            return new Result(false, null, null);
        }

        static Result valid(String phoneNumber, String messageContent) {
            return new Result(true, phoneNumber, messageContent);
        }

        boolean isValid() {
            return valid;
        }

        String getPhoneNumber() {
            return phoneNumber;
        }

        String getMessageContent() {
            return messageContent;
        }
    }
}
