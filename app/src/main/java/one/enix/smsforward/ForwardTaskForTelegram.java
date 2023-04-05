package one.enix.smsforward;

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

    public ForwardTaskForTelegram(String senderNumber, String message, String chatId) {
        this.senderNumber = senderNumber;
        this.message = message;
        this.chatId = chatId;
    }

    static private void httpRequest(String endpoint, String content) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");

        connection.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(content);
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        Log.d(Forwarder.class.toString(), String.valueOf(connection.getResponseCode()));
        Log.d(Forwarder.class.toString(), response.toString());

        connection.disconnect();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            sendViaTelegram(
                    chatId,
                    String.format("Message from %s:\n%s", senderNumber, message));
        } catch (IOException e) {
            Log.d(Forwarder.class.toString(), e.toString());
        }
        return null;
    }

    private void sendViaTelegram(String chatId, String message) throws IOException {
        String token = "";
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder
                .scheme("https")
                .authority("api.telegram.org")
                .appendPath(String.format("bot%s", token))
                .appendPath("sendMessage")
                .appendQueryParameter("chat_id", chatId)
                .appendQueryParameter("text", message)
                .build();

        httpRequest(uri.toString(), message);
    }
}