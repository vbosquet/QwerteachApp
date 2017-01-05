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
 * Created by wivi on 12/12/16.
 */

public class LoadWalletAsyncTask extends AsyncTask<String, String, String> {

    private ILoadWallet callback;
    private ProgressDialog progressDialog;

    public LoadWalletAsyncTask(ILoadWallet callback) {
        this.callback = callback;
        this.progressDialog = new ProgressDialog((Context) callback);
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
    protected String doInBackground(String... strings) {

        String email = strings[0];
        String token = strings[1];
        String amount = strings[2];
        String cardType = strings[3];
        String cardId = strings[4];

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", amount);

            if (cardId != null) {
                jsonObject.put("card", cardId);
            }

            jsonObject.put("card_type", cardType);

            URL url = new URL("http://192.168.0.108:3000/api/user/mangopay/direct_debit");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("PUT");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        progressDialog.dismiss();
        callback.loadWallet(string);
    }

    public interface ILoadWallet {
        void loadWallet(String string);
    }
}
