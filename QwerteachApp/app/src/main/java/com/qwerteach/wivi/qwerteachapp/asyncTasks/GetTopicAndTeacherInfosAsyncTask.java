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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wivi on 8/12/16.
 */

public class GetTopicAndTeacherInfosAsyncTask extends AsyncTask<Object, String, String> {

    private IGetTopicAndTeacherInfos callback;

    public GetTopicAndTeacherInfosAsyncTask(IGetTopicAndTeacherInfos callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Object... objects) {
        int teacherId = (int) objects[0];
        int topicId = (int) objects[1];
        String email = (String) objects[2];
        String token = (String) objects[3];
        int lessonId = (int) objects[4];

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("teacher_id", teacherId);
            jsonObject.put("topic_id", topicId);
            jsonObject.put("lesson_id", lessonId);

            URL url = new URL("http://192.168.0.108:3000/api/lessons/find_topic_and_teacher");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
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
        callback.getTopicAndTeacherInfos(string);
    }


    public interface IGetTopicAndTeacherInfos {
        void getTopicAndTeacherInfos(String string);
    }
}
