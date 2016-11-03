package com.qwerteach.wivi.qwerteachapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 31/10/16.
 */

public class DisplayInfosTopicsAsyncTask extends AsyncTask<String, String, String> {

    IDisplayTopicInfos callback;

    public DisplayInfosTopicsAsyncTask(IDisplayTopicInfos callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String courseCategoryName = strings[0];

        try {

            JSONObject json = new JSONObject();
            JSONObject topicJson = new JSONObject();

            json.put("title", courseCategoryName);
            topicJson.put("topic", json);

            URL url = new URL("http://10.1.10.5:3000/api/find_topics");
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

            Log.i("STRINGBUILDER", stringBuilder.toString());

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayInfosTopics(string);
    }

    public interface IDisplayTopicInfos {
        void displayInfosTopics(String string);
    }
}
