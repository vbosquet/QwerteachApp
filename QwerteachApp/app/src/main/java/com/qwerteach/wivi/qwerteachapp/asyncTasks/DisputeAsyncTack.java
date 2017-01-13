package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 4/01/17.
 */

public class DisputeAsyncTack extends AsyncTask<Object, String, String> {

    private IDispute callback;

    public DisputeAsyncTack(IDispute callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int lessonId = (int) objects[0];
        String email = (String) objects[1];
        String token = (String) objects[2];

        try {

            URL url = new URL("http://192.168.0.108:3000/api/lessons/" + lessonId + "/dispute");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("X-User-Email", email);
            connection.addRequestProperty("X-User-Token", token);
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputStream.close();

            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.disputeConfirmationMessage(string);
    }

    public interface IDispute {
        void disputeConfirmationMessage(String string);
    }
}