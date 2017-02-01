package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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
 * Created by wivi on 5/01/17.
 */

public class CreateReviewAsyncTask extends AsyncTask<Object, String, String> {

    private ICreateReview callback;

    public CreateReviewAsyncTask(ICreateReview callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Object... objects) {
        int teacherId = (int) objects[0];
        String email = (String) objects[1];
        String token = (String) objects[2];
        String reviewText = (String) objects[3];
        String note = (String) objects[4];

        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject reviewJson = new JSONObject();
            jsonObject.put("review_text", reviewText);
            jsonObject.put("note", note);
            reviewJson.put("review", jsonObject);

            URL url = new URL("http://192.168.0.103:3000/api/users/" + teacherId + "/reviews");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(reviewJson.toString().getBytes("UTF-8"));
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
        callback.createReviewConfirmationMessage(string);
    }

    public interface ICreateReview {
        void createReviewConfirmationMessage(String string);
    }
}
