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

public class TaskForWeb extends AsyncTask<Void, Void, Void> {
    static public void httpRequest(String endpoint, String content) throws IOException {
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
        return null;
    }
}