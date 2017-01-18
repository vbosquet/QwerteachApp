package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;

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
 * Created by wivi on 15/12/16.
 */

public class UpdateLessonAsyncTask extends AsyncTask<Object, String, String> {

    private IUpdateLesson callback;

    public UpdateLessonAsyncTask(IUpdateLesson callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int lessonId = (int) objects[0];
        String email = (String) objects[1];
        String token = (String) objects[2];
        String timeStart = (String) objects[3];

        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject lessonJson = new JSONObject();

            jsonObject.put("time_start", timeStart);
            lessonJson.put("lesson", jsonObject);

            URL url = new URL("http://192.168.0.125:3000/api/lessons/" + lessonId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("PUT");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(lessonJson.toString().getBytes("UTF-8"));
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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.updateConfirmationMessage(string);
    }

    public interface IUpdateLesson {
        void updateConfirmationMessage(String string);
    }
}