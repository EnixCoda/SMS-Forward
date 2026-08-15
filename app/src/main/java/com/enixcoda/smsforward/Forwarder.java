package com.enixcoda.smsforward;

import android.content.Context;
import android.telephony.SmsManager;

public class Forwarder {

    public static void forwardViaSMS(String senderNumber, String message, String targetNumber) {
        String prefix = "From " + senderNumber + ":\n";
        int maxLen = 160 - prefix.length();
        if (maxLen <= 0) {
            sendSMS(targetNumber, prefix + message.substring(0, Math.min(message.length(), 160 - 10)));
            return;
        }
        if (message.length() <= maxLen) {
            sendSMS(targetNumber, prefix + message);
        } else {
            sendSMS(targetNumber, prefix + message.substring(0, maxLen));
            sendSMS(targetNumber, message.substring(maxLen));
        }
    }

    public static void forwardViaTelegram(String senderNumber, String message, String chatId, String token) {
        new ForwardTaskForTelegram(senderNumber, message, chatId, token).execute();
    }

    public static void forwardViaWeb(String senderNumber, String message, String targetWeb) {
        new ForwardTaskForWeb(senderNumber, message, targetWeb).execute();
    }

    public static void sendSMS(String number, String message) {
        try {
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
        } catch (Exception ignored) {}
    }
}