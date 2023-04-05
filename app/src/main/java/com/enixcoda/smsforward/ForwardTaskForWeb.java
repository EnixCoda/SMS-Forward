package com.enixcoda.smsforward;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ForwardTaskForWeb extends AsyncTask<Void, Void, Void> {
    String senderNumber;
    String message;
    String endpoint;

    public ForwardTaskForWeb(String senderNumber, String message, String endpoint) {
        this.senderNumber = senderNumber;
        this.message = message;
        this.endpoint = endpoint;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONObject body = new JSONObject();
            body.put("from", senderNumber);
            body.put("message", message);
            TaskForWeb.httpRequest(endpoint, body.toString());
        } catch (IOException e) {
            Log.d(Forwarder.class.toString(), e.toString());
        } catch (JSONException e) {
            Log.d(Forwarder.class.toString(), e.toString());
        }
        return null;
    }

}