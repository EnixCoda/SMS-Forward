package com.enixcoda.smsforward;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForwardTaskForTelegram extends AsyncTask<Void, Void, Void> {
    String senderNumber;
    String message;
    String chatId;
    String token;

    public ForwardTaskForTelegram(String senderNumber, String message, String chatId, String token) {
        this.senderNumber = senderNumber;
        this.message = message;
        this.chatId = chatId;
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            sendViaTelegram(
                    chatId,
                    String.format("Message from %s:\n%s", senderNumber, message),
                    token
                    );
        } catch (IOException e) {
            Log.d(Forwarder.class.toString(), e.toString());
        }
        return null;
    }

    private void sendViaTelegram(String chatId, String message, String token) throws IOException {
       TaskForWeb.httpRequest(new Uri.Builder()
                .scheme("https")
                .authority("api.telegram.org")
                .appendPath(String.format("bot%s", token))
                .appendPath("sendMessage")
                .appendQueryParameter("chat_id", chatId)
                .appendQueryParameter("text", message)
                .build().toString(), message);
    }
}