package com.enixcoda.smsforward;

import static android.provider.ContactsContract.CommonDataKinds.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import android.telephony.TelephonyManager;
import androidx.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            return;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final boolean enableSMS = sharedPreferences.getBoolean(context.getString(R.string.key_enable_sms), false);
        final String targetNumber = sharedPreferences.getString(context.getString(R.string.key_target_sms), "");
        final String defaultCountryIso = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();

        final boolean enableWeb = sharedPreferences.getBoolean(context.getString(R.string.key_enable_web), false);
        final String targetWeb = sharedPreferences.getString(context.getString(R.string.key_target_web), "");

        final boolean enableTelegram = sharedPreferences.getBoolean(context.getString(R.string.key_enable_telegram), false);
        final String targetTelegram = sharedPreferences.getString(context.getString(R.string.key_target_telegram), "");
        final String telegramToken = sharedPreferences.getString(context.getString(R.string.key_telegram_apikey), "");

        if (!enableSMS && !enableTelegram && !enableWeb) return;

        final Bundle bundle = intent.getExtras();
        final Object[] pduObjects = (Object[]) bundle.get("pdus");
        if (pduObjects == null) return;

        for (Object messageObj : pduObjects) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) messageObj, (String) bundle.get("format"));
            String senderNumber = currentMessage.getDisplayOriginatingAddress();
            String senderNames = lookupContactName(context, senderNumber);
            String senderLabel = (senderNames.isEmpty() ? "" : senderNames + " ") + "(" + senderNumber + ")";
            String rawMessageContent = currentMessage.getDisplayMessageBody();

            if (areSamePhoneNumber(senderNumber, targetNumber, defaultCountryIso)) {
                // reverse message
                boolean isAttemptingReverseMessage = rawMessageContent.toLowerCase().startsWith("to");
                if (isAttemptingReverseMessage)
                    processReverseMessage(context, rawMessageContent, targetNumber, defaultCountryIso);
            } else {
                // normal message, forwarded
                if (enableSMS && !targetNumber.equals(""))
                    Forwarder.forwardViaSMS(senderLabel, rawMessageContent, targetNumber);
                if (enableTelegram && !targetTelegram.equals("") && !telegramToken.equals(""))
                    Forwarder.forwardViaTelegram(senderLabel, rawMessageContent, targetTelegram, telegramToken);
                if (enableWeb && !targetWeb.equals(""))
                    Forwarder.forwardViaWeb(senderLabel, rawMessageContent, targetWeb);
            }
        }
    }

    private String lookupContactName(Context context, String phoneNumber) {
        Uri filterUri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{Phone.DISPLAY_NAME};
        String[] senderContactNames = {};
        try (Cursor cur = context.getContentResolver().query(filterUri, projection, null, null, null)) {
            if (cur != null) {
                senderContactNames = new String[cur.getCount()];
                int i = 0;
                while (cur.moveToNext()) {
                    senderContactNames[i] = cur.getString(0);
                    i++;
                }
            }
        }
        return String.join(", ", senderContactNames);
    }

    private boolean areSamePhoneNumber(String phoneNumber1, String phoneNumber2,
                                       String defaultCountryIso) {
        return PhoneNumberUtils.formatNumberToE164(phoneNumber1, defaultCountryIso)
                .equals(PhoneNumberUtils.formatNumberToE164(phoneNumber2, defaultCountryIso));
    }

    private void processReverseMessage(Context context, String rawMessageContent,
                                              String targetNumber, String defaultCountryIso) {
        final String reverseMessageRegex = "^to\\s+([^:]+):\\n?([\\s\\S]+)$";
        Matcher matcher = Pattern
                .compile(reverseMessageRegex, Pattern.CASE_INSENSITIVE)
                .matcher(rawMessageContent);

        if (!matcher.find()) {
            Forwarder.sendSMS(targetNumber, context.getString(R.string.reverse_message_bad_format));
            return;
        }

        String forwardNumber = PhoneNumberUtils.formatNumberToE164(matcher.group(1), defaultCountryIso);
        if (forwardNumber == null) {
            Forwarder.sendSMS(targetNumber, context.getString(R.string.reverse_message_bad_phone_number));
            return;
        }

        String forwardContent = matcher.group(2);
        if (forwardContent == null || forwardContent.isBlank()) {
            Forwarder.sendSMS(targetNumber, context.getString(R.string.reverse_message_no_message_content));
            return;
        }

        Forwarder.sendSMS(forwardNumber, forwardContent);

        Forwarder.sendSMS(targetNumber, context.getString(R.string.reverse_message_successfully_sent_message_to) + forwardNumber);
    }
}
