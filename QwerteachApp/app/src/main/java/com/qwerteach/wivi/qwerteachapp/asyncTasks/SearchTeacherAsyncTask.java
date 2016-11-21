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
 * Created by wivi on 16/11/16.
 */

public class SearchTeacherAsyncTask extends AsyncTask<String, String, String> {

    private ISearchTeacher callback;

    public SearchTeacherAsyncTask(ISearchTeacher callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(String... strings) {
        String query = strings[0];

        try {

            URL url = new URL("http://192.168.0.111:3000/api/profiles?topic=" + query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displaySearchResults(string);
    }

    public interface ISearchTeacher {
        void displaySearchResults(String string);
    }
}
