package com.qwerteach.wivi.qwerteachapp;
;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreditCardProcessAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class RegisterNewCardActivity extends AppCompatActivity implements CreditCardProcessAsyncTask.ICreditCardProcess {

    String url, email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = getIntent().getStringExtra("url");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(url);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new RegisterNewCardActivity.MyWebViewClient());
    }

    @Override
    public void getCreditCardProcess(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("success");

            if (message.equals("true")) {
                Toast.makeText(this, R.string.payment_success_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Il y a eu un problème lors de la réservation. Le cours n\'a pas été réservé.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Uri uri = Uri.parse(url);
            String protocol = uri.getScheme();
            String server = uri.getAuthority();
            String path = uri.getPath();
            Set<String> args = uri.getQueryParameterNames();
            String transactionId = uri.getQueryParameter("transactionId");

            String newURL = protocol + "://" + server + "/api" + path + "?transactionId=" + transactionId;
            startCreditCardAsyncTask(newURL);

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson_reservation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startCreditCardAsyncTask(String url) {
        CreditCardProcessAsyncTask creditCardProcessAsyncTask = new CreditCardProcessAsyncTask(this);
        creditCardProcessAsyncTask.execute(email, token, url);
    }
}
