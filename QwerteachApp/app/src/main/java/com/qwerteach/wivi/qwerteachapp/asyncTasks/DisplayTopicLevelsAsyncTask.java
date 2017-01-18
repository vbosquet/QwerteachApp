package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.qwerteach.wivi.qwerteachapp.models.SmallAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wivi on 22/11/16.
 */

public class DisplayTopicLevelsAsyncTask extends AsyncTask<Object, String, String> {

    private IDisplayTopicLevels callback;

    public DisplayTopicLevelsAsyncTask(IDisplayTopicLevels callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int topicId = (int) objects[0];

        try {

            JSONObject json = new JSONObject();
            JSONObject topicJson = new JSONObject();
            json.put("topic_id", topicId);
            topicJson.put("topic", json);

            URL url = new URL("http://192.168.0.125:3000/api/find_topics/find_levels");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(topicJson.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputStream.close();

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayTopicLevels(string);
    }

    public interface IDisplayTopicLevels {
        void displayTopicLevels(String string);
    }
}
