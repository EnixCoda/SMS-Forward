package com.enixcoda.smsforward;

import android.telephony.SmsManager;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Forwarder {
    static final int MAX_SMS_LENGTH = 120;

    public static void sendSMS(String number, String content) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> fragments = smsManager.divideMessage(content);
        if (fragments.size() > 1)
            smsManager.sendMultipartTextMessage(number, null, fragments, null, null);
        else
            smsManager.sendTextMessage(number, null, content, null, null);
    }

    public static void forwardViaSMS(String senderNumber, String forwardContent, String forwardNumber,
                                     boolean keepTogether) {
        try {
            for (String message : buildSmsMessages(senderNumber, forwardContent, keepTogether))
                sendSMS(forwardNumber, message);
        } catch (RuntimeException e) {
            Log.d(Forwarder.class.toString(), e.toString());
        }
    }

    static ArrayList<String> buildSmsMessages(String senderNumber, String forwardContent,
                                              boolean keepTogether) {
        String forwardPrefix = String.format("From %s:\n", senderNumber);
        ArrayList<String> messages = new ArrayList<>();

        if (!keepTogether && (forwardPrefix + forwardContent)
                .getBytes(StandardCharsets.UTF_8).length > MAX_SMS_LENGTH) {
            messages.add(forwardPrefix);
            messages.add(forwardContent);
        } else {
            messages.add(forwardPrefix + forwardContent);
        }
        return messages;
    }

    public static void forwardViaTelegram(String senderNumber, String message, String targetTelegramID, String telegramToken) {
        new ForwardTaskForTelegram(senderNumber, message, targetTelegramID, telegramToken).execute();
    }
    public static void forwardViaWeb(String senderNumber, String message, String endpoint) {
        new ForwardTaskForWeb(senderNumber, message, endpoint).execute();
    }
}
