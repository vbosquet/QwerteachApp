package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by wivi on 2/12/16.
 */

public class GetPreRegistrationDataForCardRegistrationAsyncTask extends AsyncTask<String, String, String> {

    private IGetPreRegistrationData callback;

    public GetPreRegistrationDataForCardRegistrationAsyncTask(IGetPreRegistrationData callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String url = "http://demo-mangopay.rhcloud.com/card-registration";

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                connection.disconnect();
                return response.toString();
            } else {
                connection.disconnect();
                return "";
            }
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.getPreRegistrationData(string);
    }

    public interface IGetPreRegistrationData {
        void getPreRegistrationData(String string);
    }
}
