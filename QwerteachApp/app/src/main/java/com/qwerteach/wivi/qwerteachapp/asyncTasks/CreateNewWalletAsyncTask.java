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
 * Created by wivi on 29/11/16.
 */

public class CreateNewWalletAsyncTask extends AsyncTask<String, String, String> {

    private ICreateNewWallet callback;

    public CreateNewWalletAsyncTask(ICreateNewWallet callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        String email = strings[0];
        String token = strings[1];
        String firstName = strings[2];
        String lastName = strings[3];
        String address = strings[4];
        String streetNumber = strings[5];
        String postalCode = strings[6];
        String city = strings[7];
        String region = strings[8];
        String country = strings[9];
        String residencePlace = strings[10];
        String nationality = strings[11];

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject accountJson = new JSONObject();

            jsonObject.put("first_name", firstName);
            jsonObject.put("last_name", lastName);
            jsonObject.put("address_line1", address);
            jsonObject.put("address_line2", streetNumber);
            jsonObject.put("postal_code", postalCode);
            jsonObject.put("city", city);
            jsonObject.put("country", country);
            jsonObject.put("region", region);
            jsonObject.put("country_of_residence", residencePlace);
            jsonObject.put("nationality", nationality);

            accountJson.put("account", jsonObject);

            URL url = new URL("http://192.168.0.125:3000/api/user/mangopay/edit_wallet");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("PUT");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(accountJson.toString().getBytes("UTF-8"));
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
        callback.confirmationCreationNewWallet(string);
    }

    public interface ICreateNewWallet {
        void confirmationCreationNewWallet(String string);
    }
}
