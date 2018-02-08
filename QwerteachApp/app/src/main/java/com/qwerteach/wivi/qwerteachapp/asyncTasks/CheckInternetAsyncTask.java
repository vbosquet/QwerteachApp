package com.qwerteach.wivi.qwerteachapp.asyncTasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by virginie on 08/02/2018.
 */

public class CheckInternetAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private ICheckInternet callback;

    public CheckInternetAsyncTask(Context context, ICheckInternet callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();


        if (isConnected) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0)
                    return true;

            } catch (IOException e) {
                Log.e("TAG", "Error checking internet connection", e);
                return false;
            }
        } else {
            Log.d("TAG", "No network available!");
            return false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Log.d("TAG", "result" + result);
        callback.getResult(result);

    }

    public interface ICheckInternet {
        void getResult(Boolean result);
    }
}
