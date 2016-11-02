package com.qwerteach.wivi.qwerteachapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 31/10/16.
 */

public class DisplayInfosGroupTopicsAsyncTack extends AsyncTask<String, String, String> {

    private IDisplayInfosGroupTopics callback;

    public DisplayInfosGroupTopicsAsyncTack(IDisplayInfosGroupTopics callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {

            URL url = new URL("http://10.1.10.10:3000/api/find_group_topics");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

            Log.i("STRINGBUILDER", stringBuilder.toString());

            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayInfosGroupTopics(string);
    }

    public interface IDisplayInfosGroupTopics {
        void displayInfosGroupTopics(String string);
    }
}
