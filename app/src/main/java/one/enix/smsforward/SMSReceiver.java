package one.enix.smsforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
    static final String SMS_RECEIVED_ACTION = android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
    static final SmsManager smsManager = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (! intent.getAction().equals(SMS_RECEIVED_ACTION)) return;

        final Bundle bundle = intent.getExtras();
        final Object[] pduObjects = (Object[]) bundle.get("pdus");
        if (pduObjects == null) return;

        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_shared_preferences_key), Context.MODE_PRIVATE);
        final String targetNumber = sharedPreferences.getString(context.getString(R.string.target_phone_number_key), context.getString(R.string.default_forward_number));

        for (Object messageObj : pduObjects) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) messageObj, (String) bundle.get("format"));
            String senderNumber = currentMessage.getDisplayOriginatingAddress();
            String rawMessageContent = currentMessage.getDisplayMessageBody();

            /**
             * when receive a message from ordinary numbers
             * forward the message to target number in this format:
             *      'from `senderNumber` :`messageContent`'
             *
             * when receive a message from the target number
             *      e.g. target phone replying the message
             * the format should be:
             *      'to `toNumber` :`messageContent`'
             * then forward the message to 'toNumber'
             */
            String forwardNumber = null;
            String forwardPrefix = null;
            String forwardContent = null;
            if (senderNumber.matches(targetNumber)) {
                // it's a message from target number
                String formatRegex = "to (\\+?\\d+?):\\n((.|\\n)*)";
                if (rawMessageContent.matches(formatRegex)) {
                    // it'a message need to be forwarded
                    forwardNumber = rawMessageContent.replaceFirst(formatRegex, "$1");
                    forwardPrefix = "";
                    forwardContent = rawMessageContent.replaceFirst(formatRegex, "$2");
                }
            } else {
                // it's a normal message, need to be forwarded
                forwardNumber = targetNumber;
                forwardPrefix = "from " + senderNumber + ":\n";
                forwardContent = rawMessageContent;
            }

            if (forwardNumber != null && forwardContent != null) {
                if ((forwardPrefix + forwardContent).getBytes().length > 120) {
                    // there is a length limit in SMS, if the message length exceeds it, separate the meta data and content
                    smsManager.sendTextMessage(forwardNumber, null, forwardPrefix, null, null);
                    smsManager.sendTextMessage(forwardNumber, null, forwardContent, null, null);
                } else {
                    // if it's not too long, combine meta data and content to save money
                    smsManager.sendTextMessage(forwardNumber, null, forwardPrefix + forwardContent, null, null);
                }
            }
        }
    }
}
