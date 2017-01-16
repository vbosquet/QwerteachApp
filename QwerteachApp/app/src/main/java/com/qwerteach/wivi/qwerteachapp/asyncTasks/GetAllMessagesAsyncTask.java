package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 26/12/16.
 */

public class GetAllMessagesAsyncTask extends AsyncTask<Object, String, String> {

    private IGetAllMessages callback;

    public GetAllMessagesAsyncTask(IGetAllMessages callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String email = (String) objects[0];
        String token = (String) objects[1];
        int conversationId = (int) objects[2];

        try {

            URL url = new URL("http://192.168.0.101:3000/api/conversations/" + conversationId);
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
        callback.getAllMessages(string);
    }

    public interface IGetAllMessages {
        void getAllMessages(String string);
    }
}
