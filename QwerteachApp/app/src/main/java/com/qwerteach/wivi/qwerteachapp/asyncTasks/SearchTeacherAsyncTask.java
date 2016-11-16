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
        String userId = strings[1];
        String email = strings[2];
        String token = strings[3];

        try {

            /*JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("topic", query);
            userJson.put("user", json);*/

            URL url = new URL("http://10.1.10.7:3000/profs?topic=" + query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");

            /*OutputStream os = httpURLConnection.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.flush();
            os.close();*/

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
