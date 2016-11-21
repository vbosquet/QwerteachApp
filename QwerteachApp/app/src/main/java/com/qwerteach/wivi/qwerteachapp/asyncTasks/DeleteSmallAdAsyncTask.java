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
 * Created by wivi on 4/11/16.
 */

public class DeleteSmallAdAsyncTask extends AsyncTask<Object, String, String> {

    private IDeleteSmallAd callback;

    public DeleteSmallAdAsyncTask(IDeleteSmallAd callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int advertId = (int) objects[0];


        try {

            JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("id", advertId);
            userJson.put("advert", json);

            URL url = new URL("http://192.168.0.111:3000/api/adverts/delete");
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
        callback.confirmationDeleteSmallAdMessage(string);
    }

    public interface IDeleteSmallAd {
        void confirmationDeleteSmallAdMessage(String string);
    }
}
