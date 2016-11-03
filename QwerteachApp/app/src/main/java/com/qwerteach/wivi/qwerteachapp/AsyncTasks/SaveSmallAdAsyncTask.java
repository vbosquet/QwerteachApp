package com.qwerteach.wivi.qwerteachapp.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wivi on 31/10/16.
 */

public class SaveSmallAdAsyncTask extends AsyncTask<Object, String, String> {

    private ISaveSmallAdInfos callback;

    public SaveSmallAdAsyncTask(ISaveSmallAdInfos callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {

        String courseCategoryName = (String) objects[0];
        String courseMaterialName = (String) objects[1];
        String otherCourseMaterialName = (String) objects[2];
        String description = (String) objects[3];
        String userId = (String) objects[4];
        Map<Integer, Double> coursePrices = (HashMap) objects[5];


        try {

            JSONArray pricesJsonArray = new JSONArray();
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

            if (coursePrices != null) {
                for (Map.Entry<Integer, Double> entry : coursePrices.entrySet()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("level_id", entry.getKey());
                    jsonObject.put("price", entry.getValue());
                    pricesJsonArray.put(jsonObject);
                }
            }


            json.put("advert_price", pricesJsonArray);
            json.put("id", userId);
            smallAdJson.put("advert", json);

            URL url = new URL("http://10.1.10.5:3000/api/save_advert");
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
