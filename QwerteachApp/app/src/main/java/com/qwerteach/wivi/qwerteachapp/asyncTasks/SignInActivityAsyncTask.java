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
 * Created by wivi on 25/10/16.
 */

public class SignInActivityAsyncTask extends AsyncTask<String, String, String> {

    private ISignIn callback;

    public SignInActivityAsyncTask(ISignIn callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String email = strings[0];
        String password = strings[1];

        JSONObject json = new JSONObject();
        JSONObject userJson = new JSONObject();

        try {

            json.put("email", email);
            json.put("password", password);
            userJson.put("user", json);

            URL url = new URL("http://192.168.0.111:3000/api/sessions");
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

    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayConfirmationConnectionMessage(string);
    }

    public interface ISignIn {
        void displayConfirmationConnectionMessage(String string);
    }
}
