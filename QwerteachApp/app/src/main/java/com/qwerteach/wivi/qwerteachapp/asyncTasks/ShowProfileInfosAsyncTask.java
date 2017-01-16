package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 10/01/17.
 */

public class ShowProfileInfosAsyncTask extends AsyncTask<String, String, String> {

    private IShowProfileInfos callback;

    public ShowProfileInfosAsyncTask(IShowProfileInfos callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String userId = strings[0];
        String email = strings[1];
        String token = strings[2];

        try {

            URL url = new URL("http://192.168.0.101:3000/api/users/" + userId);
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
        callback.showProfileInfos(string);
    }

    public interface IShowProfileInfos {
        void showProfileInfos(String string);
    }
}
