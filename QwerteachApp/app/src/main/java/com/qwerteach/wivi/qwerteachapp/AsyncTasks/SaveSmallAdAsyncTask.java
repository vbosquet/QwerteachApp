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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by wivi on 31/10/16.
 */

public class SaveSmallAdAsyncTask extends AsyncTask<String, String, String> {

    private ISaveSmallAdInfos callback;

    public SaveSmallAdAsyncTask(ISaveSmallAdInfos callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String courseCategoryName = strings[0];
        String courseMaterialName = strings[1];
        String otherCourseMaterialName = strings[2];
        String description = strings[3];
        String userId = strings[4];

            try {

                JSONObject json = new JSONObject();
                JSONObject smallAdJson = new JSONObject();

                if (courseCategoryName != null) {
                    json.put("topic_group", courseCategoryName);
                } else {
                    json.put("topic_group", "");
                }

                if (courseMaterialName != null) {
                    json.put("topic", courseMaterialName);
                } else {
                    json.put("topic", "");
                }

                if (otherCourseMaterialName != null) {
                    json.put("other_name", otherCourseMaterialName);
                } else {
                    json.put("other_name", "");
                }

                if (description != null) {
                    json.put("description", description);
                } else {
                    json.put("description", "");
                }

                json.put("id", userId);
                smallAdJson.put("advert", json);

                URL url = new URL("http://10.1.10.10:3000/api/save_advert");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream os = httpURLConnection.getOutputStream();
                os.write(smallAdJson.toString().getBytes("UTF-8"));
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
        callback.displayRegistrationConfirmationMessage(string);
    }

    public interface ISaveSmallAdInfos {
        void displayRegistrationConfirmationMessage(String string);
    }
}
