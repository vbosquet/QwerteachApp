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
 * Created by wivi on 10/11/16.
 */

public class DisplaySchoolLevelsAsyncTask extends AsyncTask<String, String, String> {

    private IDisplaySchoolLevels callback;

    public DisplaySchoolLevelsAsyncTask(IDisplaySchoolLevels callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {

            URL url = new URL("http://192.168.0.111:3000/api/profiles/find_level");
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
        callback.displaySchoolLevels(string);
    }

    public interface IDisplaySchoolLevels {
        void displaySchoolLevels(String string);
    }
}