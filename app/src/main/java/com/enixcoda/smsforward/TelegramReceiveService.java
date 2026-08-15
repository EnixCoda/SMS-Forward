package com.enixcoda.smsforward;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TelegramReceiveService extends Service {

    private static final String CHANNEL_ID = "TelegramReceiveServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static int lastUpdateId = 0;
    private final OkHttpClient client = new OkHttpClient();
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            while (isRunning) {
                try {
                    pollTelegramMessages(getApplicationContext());
                    Thread.sleep(3000);
                } catch (Exception e) {
                    Log.e("TelegramReceiveService", "Polling error", e);
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void pollTelegramMessages(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token = prefs.getString(context.getString(R.string.key_telegram_apikey), "");
        if (token.isEmpty()) return;

        try {
            String url = "https://api.telegram.org/bot" + token + "/getUpdates?offset=" + (lastUpdateId + 1) + "&timeout=30";
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) return;

                String json = response.body().string();
                JSONObject obj = new JSONObject(json);
                JSONArray results = obj.getJSONArray("result");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject update = results.getJSONObject(i);
                    lastUpdateId = update.getInt("update_id");

                    JSONObject message = update.optJSONObject("message");
                    if (message == null) continue;

                    String text = message.optString("text");
                    if (text == null || text.isEmpty()) continue;

                    // Check if this is a reply to a forwarded message
                    JSONObject replyTo = message.optJSONObject("reply_to_message");
                    if (replyTo != null) {
                        String repliedText = replyTo.optString("text");
                        String targetNumber = extractNumberFromRepliedMessage(repliedText);
                        if (targetNumber != null) {
                            // Send the reply as an SMS
                            Forwarder.sendSMS(targetNumber, text);
                            // Send confirmation back to Telegram
                            sendTelegramReply(message.getJSONObject("chat").getLong("id"),
                                    "✅ SMS sent to " + targetNumber);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TelegramReceiveService", "Error polling messages", e);
        }
    }

    private String extractNumberFromRepliedMessage(String repliedText) {
        if (repliedText == null) return null;
        Pattern p = Pattern.compile("\\(([^)]+)\\)");
        Matcher m = p.matcher(repliedText);
        return m.find() ? m.group(1) : null;
    }

    private void sendTelegramReply(long chatId, String message) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString(getString(R.string.key_telegram_apikey), "");
        if (token.isEmpty()) return;

        try {
            String url = "https://api.telegram.org/bot" + token + "/sendMessage";
            JSONObject json = new JSONObject();
            json.put("chat_id", chatId);
            json.put("text", message);

            okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder().url(url).post(body).build();
            client.newCall(request).execute();
        } catch (Exception e) {
            Log.e("TelegramReceiveService", "Error sending Telegram reply", e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Telegram Receive Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Forward")
                .setContentText("Listening for Telegram replies...")
                .setSmallIcon(android.R.drawable.ic_menu_send)
                .build();
    }
}