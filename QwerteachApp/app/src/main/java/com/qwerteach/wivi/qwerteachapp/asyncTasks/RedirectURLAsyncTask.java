package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wivi on 5/12/16.
 */

public class RedirectURLAsyncTask extends AsyncTask<Object, String, String> {

    private IRedirectURL callback;
    private ProgressDialog progressDialog;

    public RedirectURLAsyncTask(IRedirectURL callback) {
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

        String email = (String) objects[0];
        String token = (String) objects[1];
        String newURL = (String) objects[2];

        try {

            URL url = new URL(newURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setRequestMethod("GET");

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        progressDialog.dismiss();
        callback.redirectURL(string);
    }

    public interface IRedirectURL {
        void redirectURL(String string);
    }
}
