package com.devinhe.svm_server;

import android.os.AsyncTask;

import com.devinhe.svm_server.models.ModelType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by devin on 4/15/16.
 */
public class PostSender extends AsyncTask<PostSenderParams, Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(PostSender.class);

    @Override
    protected String doInBackground(PostSenderParams... params) {
        // should we batch all the requests at once? it seems like in most cases they are done one at a time anyway...
        assert params.length == 1;
        String response = new String();
        String serverIP = params[0].serverIP;
        String features = params[0].features;
        int n = params[0].n;
        ModelType modelType = params[0].modelType;
        try {
            URL url = new URL("http", serverIP, ModelType.getPath(modelType));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            String[] splitFeatures = features.split(",");
            float[] floatFeatures = new float[splitFeatures.length];
            for (int i = 0; i < splitFeatures.length; i++) {
                floatFeatures[i] = Float.parseFloat(splitFeatures[i]);
            }
            JSONArray arr =new JSONArray(Arrays.asList(floatFeatures));
            json.put("x", arr);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "HTTP response was " + Integer.toString(responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Request " + Integer.toString(n) + ": " + response;
    }
}
