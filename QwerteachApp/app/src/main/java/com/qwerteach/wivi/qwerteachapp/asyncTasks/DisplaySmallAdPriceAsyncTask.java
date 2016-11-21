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
 * Created by wivi on 9/11/16.
 */

public class DisplaySmallAdPriceAsyncTask extends AsyncTask<Object, String, String> {

    private IDisplaySmallAdPrice callback;

    public DisplaySmallAdPriceAsyncTask(IDisplaySmallAdPrice callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        int smallAdId = (int) objects[0];

        try {

            JSONObject json = new JSONObject();
            JSONObject userJson = new JSONObject();

            json.put("advert_id", smallAdId);
            userJson.put("advert_price", json);

            URL url = new URL("http://192.168.0.111:3000/api/adverts/find_advert_prices");
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

            return stringBuilder.toString();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displaySmallAdPrice(string);
    }

    public interface IDisplaySmallAdPrice {
        void displaySmallAdPrice(String string);
    }
}
