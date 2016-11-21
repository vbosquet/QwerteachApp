package com.qwerteach.wivi.qwerteachapp.asyncTasks;

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
 * Created by wivi on 10/11/16.
 */

public class EditSmallAdAsyncTask extends AsyncTask<Object, String, String> {

    private IEditSmallAd callback;

    public  EditSmallAdAsyncTask(IEditSmallAd callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        TopicGroup topicGroup = (TopicGroup) objects[0];
        Topic topic = (Topic) objects[1];
        ArrayList<Level> levels = (ArrayList<Level>) objects[2];
        String otherCourseMaterialName = (String) objects[3];
        String description = (String) objects[4];
        int advertId = (int) objects[5];

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
            json.put("id", advertId);
            smallAdJson.put("advert", json);

            URL url = new URL("http://192.168.0.111:3000/api/adverts/update");
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

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.confirmationRegsitrationMessage(string);
    }

    public interface IEditSmallAd {
        void confirmationRegsitrationMessage(String string);
    }
}
