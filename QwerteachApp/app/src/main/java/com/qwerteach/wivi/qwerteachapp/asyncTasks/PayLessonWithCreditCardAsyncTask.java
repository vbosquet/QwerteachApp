package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mangopay.android.sdk.model.CardRegistration;

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
 * Created by wivi on 2/12/16.
 */

public class PayLessonWithCreditCardAsyncTask extends AsyncTask<Object, String, String> {

    private IPayWithCreditCard callback;

    public PayLessonWithCreditCardAsyncTask(IPayWithCreditCard callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {

        String email = (String) objects[0];
        String token = (String) objects[1];
        int teacherId = (int) objects[2];
        String paymentMode = (String) objects[3];
        String cardId = (String) objects[4];

        try {

            URL url = new URL("http://192.168.0.108:3000/api/users/" + teacherId + "/lesson_requests/payment?card_id="+ cardId
                    +"&mode=" + paymentMode);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("PUT");

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.confirmationMessagePaymentWithCreditCard(string);
    }

    public interface IPayWithCreditCard {
        void confirmationMessagePaymentWithCreditCard(String string);
    }
}
