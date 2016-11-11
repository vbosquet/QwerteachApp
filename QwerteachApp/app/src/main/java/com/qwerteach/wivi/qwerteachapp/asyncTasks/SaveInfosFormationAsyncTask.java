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
 * Created by wivi on 10/11/16.
 */

public class SaveInfosFormationAsyncTask extends AsyncTask<Object, String, String> {

    private ISaveInfosFormation callback;

    public SaveInfosFormationAsyncTask(ISaveInfosFormation callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Object... objects) {
        String userId = (String) objects[0];
        String token = (String) objects[1];
        String profession = (String) objects[2];
        String description = (String) objects[3];
        int levelId = (int) objects[4];

        try {

            JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("occupation", profession);
            json.put("description", description);
            json.put("user_id", userId);
            json.put("level_id", levelId);
            json.put("api_token", token);
            userJson.put("user", json);

            URL url = new URL("http://10.1.10.7:3000/users/" + userId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
        callback.confirmationRegistrationMessage(string);
    }

    public interface ISaveInfosFormation {
        void confirmationRegistrationMessage(String string);
    }
}
