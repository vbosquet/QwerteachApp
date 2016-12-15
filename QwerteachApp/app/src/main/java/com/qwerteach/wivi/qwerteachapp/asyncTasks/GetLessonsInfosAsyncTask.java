package com.qwerteach.wivi.qwerteachapp.asyncTasks;

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
 * Created by wivi on 15/12/16.
 */

public class GetLessonsInfosAsyncTask extends AsyncTask<Object, String, String> {

    IGetLessonInfos callback;

    public GetLessonsInfosAsyncTask(IGetLessonInfos callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String email = (String) objects[0];
        String token = (String) objects[1];
        int topicId = (int) objects[2];
        int topicGroupId = (int) objects[3];
        int levelId = (int) objects[4];
        int lessonId = (int) objects[5];

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("topic_id", topicId);
            jsonObject.put("topic_group_id", topicGroupId);
            jsonObject.put("level_id", levelId);
            jsonObject.put("lesson_id", lessonId);

            URL url = new URL("http://192.168.0.111:3000/api/lessons/find_lesson_infos");
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
        callback.displayLessonInfos(string);
    }

    public interface IGetLessonInfos {
        void displayLessonInfos(String string);
    }
}
