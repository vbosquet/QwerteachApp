package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 29/11/16.
 */

public class NewLessonRequestAsyncTask extends AsyncTask<String, String, String>{

    private INewLessonRequest callback;

    public NewLessonRequestAsyncTask(INewLessonRequest callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String teacherId = strings[0];
        String email = strings[1];
        String token = strings [2];

        try {

            URL url = new URL("http://192.168.0.102:3000/api/users/" + teacherId + "/lesson_requests/new");
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
        callback.lessonRequest(string);
    }

    public interface INewLessonRequest {
        void lessonRequest(String string);
    }
}