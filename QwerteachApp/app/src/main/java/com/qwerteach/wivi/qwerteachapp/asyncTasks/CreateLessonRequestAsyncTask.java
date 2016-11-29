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
 * Created by wivi on 29/11/16.
 */

public class CreateLessonRequestAsyncTask extends AsyncTask<Object, String, String> {

    private ICreateLessonRequest callback;

    public CreateLessonRequestAsyncTask(ICreateLessonRequest callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int teacherId = (int) objects[0];
        String studentId = (String) objects[1];
        int levelId = (int) objects[2];
        int topicId = (int) objects[3];
        String timeStart = (String) objects[4];
        String hours = (String) objects[5];
        String minutes = (String) objects[6];
        Boolean freeLesson = (Boolean) objects[7];
        String email = (String) objects[8];
        String token = (String) objects[9];

        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject requestJson = new JSONObject();
            jsonObject.put("student_id", studentId);
            jsonObject.put("level_id", levelId);
            jsonObject.put("topic_id", topicId);
            jsonObject.put("time_start", timeStart);
            jsonObject.put("hours", hours);
            jsonObject.put("minutes", minutes);
            jsonObject.put("free_lesson", freeLesson);
            requestJson.put("request", jsonObject);

            URL url = new URL("http://192.168.0.111:3000/api/users/" + teacherId + "/lesson_requests");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(requestJson.toString().getBytes("UTF-8"));
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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.createLessonRequest(string);
    }

    public interface ICreateLessonRequest {
        void createLessonRequest(String string);
    }
}
