package com.qwerteach.wivi.qwerteachapp.AsyncTasks;

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
 * Created by wivi on 27/10/16.
 */

public class DisplayInfosProfileAsyncTask extends AsyncTask<String, String, String> {

    private IDisplayInfosProfile callback;

    public DisplayInfosProfileAsyncTask(IDisplayInfosProfile callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String userId = strings[0];

        try {

            JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("id", userId);
            userJson.put("user", json);

            URL url = new URL("http://10.1.10.10:3000/api/display_profile");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(userJson.toString().getBytes("UTF-8"));
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

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayUserInfosProfile(string);
    }

    public interface IDisplayInfosProfile {
        void displayUserInfosProfile(String string);
    }
}