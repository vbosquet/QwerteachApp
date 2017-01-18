package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wivi on 31/10/16.
 */

public class SaveSmallAdAsyncTask extends AsyncTask<Object, String, String> {

    private ISaveSmallAdInfos callback;
    private ProgressDialog progressDialog;

    public SaveSmallAdAsyncTask(ISaveSmallAdInfos callback) {
        this.callback = callback;
        progressDialog = new ProgressDialog((Context) callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Object... objects) {

        TopicGroup topicGroup = (TopicGroup) objects[0];
        Topic topic = (Topic) objects[1];
        String otherCourseMaterialName = (String) objects[2];
        String description = (String) objects[3];
        String userId = (String) objects[4];
        ArrayList<Level> levels = (ArrayList<Level>) objects[5];
        String email = (String) objects[6];
        String token = (String) objects[7];


        try {

            JSONArray pricesJsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            JSONObject smallAdJson = new JSONObject();

            if (topicGroup != null) {
                json.put("topic_group_id", topicGroup.getTopicGroupId());
            } else {
                json.put("topic_group_id", "");
            }

            if (topic != null) {
                json.put("topic_id", topic.getTopicId());
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

            if (levels != null) {
                for(int i = 0; i< levels.size(); i++) {
                    if (levels.get(i).isChecked()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("level_id", levels.get(i).getLevelId());
                        jsonObject.put("price", levels.get(i).getPrice());
                        pricesJsonArray.put(jsonObject);
                    }
                }
            }


            json.put("advert_price", pricesJsonArray);
            json.put("id", userId);
            smallAdJson.put("advert", json);

            URL url = new URL("http://192.168.0.125:3000/api/adverts/create");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
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

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        progressDialog.dismiss();
        callback.displayRegistrationConfirmationMessage(string);
    }

    public interface ISaveSmallAdInfos {
        void displayRegistrationConfirmationMessage(String string);
    }
}
