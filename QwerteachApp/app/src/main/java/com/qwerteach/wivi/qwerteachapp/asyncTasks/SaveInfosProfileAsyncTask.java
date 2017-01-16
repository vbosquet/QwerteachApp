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
 * Created by wivi on 27/10/16.
 */

public class SaveInfosProfileAsyncTask extends AsyncTask<String, String, String> {

    private ISaveInfosProfile callback;

    public SaveInfosProfileAsyncTask(ISaveInfosProfile callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(String... strings) {
        String firstName = strings[0];
        String lastName = strings[1];
        String birthDate = strings[2];
        String userId = strings[3];
        String phoneNumber = strings[4];
        String email = strings[5];
        String token = strings[6];

        if(birthDate.equals("")) {
            birthDate = null;
        }

        try {

            JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("firstname", firstName);
            json.put("lastname", lastName);
            json.put("birthdate", birthDate);
            json.put("phonenumber", phoneNumber);
            userJson.put("user", json);

            URL url = new URL("http://192.168.0.101:3000/api/profiles/" + userId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("PUT");

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

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayConfirmationRegistrationInfosProfile(string);
    }

    public interface ISaveInfosProfile {
        void displayConfirmationRegistrationInfosProfile(String string);
    }
}
