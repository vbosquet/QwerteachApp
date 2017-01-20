package com.qwerteach.wivi.qwerteachapp.asyncTasks;

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
 * Created by wivi on 17/01/17.
 */

public class UpdateBankAccountAsyncTask extends AsyncTask<Object, String, String> {

    private IUpdateBankAccount callback;

    public UpdateBankAccountAsyncTask(IUpdateBankAccount callback) {
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Object... objects) {
        String email = (String) objects[0];
        String token = (String) objects[1];
        String type = (String) objects[2];
        String iban = (String) objects[3];
        String bic = (String) objects[4];
        String accountNumber = (String) objects[5];
        String sortCode = (String) objects[6];
        String aba = (String) objects[7];
        String depositAccountType = (String) objects[8];
        String bankName = (String) objects[9];
        String institutionNumber = (String) objects[10];
        String branchCode = (String) objects[10];
        String country = (String) objects[11];

        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject accountJson = new JSONObject();

            switch (type) {
                case "iban":
                    jsonObject.put("iban", iban);
                    jsonObject.put("bic", bic);
                    accountJson.put("iban_account", jsonObject);
                    break;
                case "gb":
                    jsonObject.put("account_number", accountNumber);
                    jsonObject.put("sort_code", sortCode);
                    accountJson.put("gb_account", jsonObject);
                    break;
                case "us":
                    jsonObject.put("account_number", accountNumber);
                    jsonObject.put("aba", aba);
                    jsonObject.put("deposit_account_type", depositAccountType);
                    accountJson.put("us_account", jsonObject);
                    break;
                case "ca":
                    jsonObject.put("bank_name", bankName);
                    jsonObject.put("institution_number", institutionNumber);
                    jsonObject.put("branch_code", branchCode);
                    jsonObject.put("account_number", accountNumber);
                    accountJson.put("ca_account", jsonObject);
                    break;
                case "other":
                    jsonObject.put("country", country);
                    jsonObject.put("bic", bic);
                    jsonObject.put("account_number", accountNumber);
                    accountJson.put("other_account", jsonObject);
                    break;
            }

            JSONObject typeJson = new JSONObject();
            typeJson.put("type", type);
            accountJson.put("bank_account", typeJson);

            URL url = new URL("http://192.168.0.101:3000/api/user/mangopay/update_bank_accounts");
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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.updateBankAccountConfirmationMessage(string);
    }

    public interface IUpdateBankAccount {
        void updateBankAccountConfirmationMessage(String string);
    }
}
