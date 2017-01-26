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
 * Created by wivi on 9/12/16.
 */

public class FindUsersByMangoIdAsyncTask extends AsyncTask<Object, String, String> {

    private IFindUsersByMangoId callback;

    public FindUsersByMangoIdAsyncTask(IFindUsersByMangoId callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String email = (String) objects[0];
        String token = (String) objects[1];
        String authorId = (String) objects[2];
        String creditedUserId = (String) objects[3];
        String transactionId = (String) objects[4];

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("author_id", authorId);
            jsonObject.put("credited_user_id", creditedUserId);
            jsonObject.put("transaction_id", transactionId);

            URL url = new URL("http://192.168.0.125:3000/api/wallets/find_users_by_mango_id");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.addRequestProperty("X-User-Email", email);
            httpURLConnection.addRequestProperty("X-User-Token", token);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");

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


        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        callback.displayUsersInfos(string);
    }

    public interface IFindUsersByMangoId {
        void displayUsersInfos(String string);
    }
}
