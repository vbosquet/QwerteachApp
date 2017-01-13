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
 * Created by wivi on 7/12/16.
 */

public class GetAllMyLessonsAsyncTask extends AsyncTask<String, String, String> {

    private IGetAllLessons callback;

    public GetAllMyLessonsAsyncTask(IGetAllLessons callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String email = strings[0];
        String token = strings[1];

        try {

            URL url = new URL("http://192.168.0.108:3000/api/cours");
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
        callback.getAllMyLessons(string);
    }

    public interface IGetAllLessons {
        void getAllMyLessons(String string);
    }
}