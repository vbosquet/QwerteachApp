package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
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

import com.qwerteach.wivi.qwerteachapp.asyncTasks.BancontactProcessAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RedirectURLAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Set;

public class MangoPayWebViewActivity extends AppCompatActivity implements RedirectURLAsyncTask.IRedirectURL,
        BancontactProcessAsyncTask.IBancontactProcess {

    String url, email, token;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mango_pay_web_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = getIntent().getStringExtra("url");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(url);
    }

    @Override
    public void redirectURL(String string) {

        String html = string;
        Document doc = Jsoup.parse(html);
        Element link = doc.select("a").first();
        String linkHref = link.attr("href");

        Uri uri = Uri.parse(linkHref);
        String protocol = uri.getScheme();
        String server = uri.getAuthority();
        String path = uri.getPath();
        Set<String> args = uri.getQueryParameterNames();
        String transactionId = uri.getQueryParameter("transactionId");

        String newURL = protocol + "://" + server + "/api" + path + "?transactionId=" + transactionId;

        BancontactProcessAsyncTask bancontactProcessAsyncTask = new BancontactProcessAsyncTask(this);
        bancontactProcessAsyncTask.execute(email, token, newURL);

    }

    public void startRedirectURLAsyncTask(String newURL) {
        RedirectURLAsyncTask redirectURLAsyncTask = new RedirectURLAsyncTask(this);
        redirectURLAsyncTask.execute(email, token, newURL);
    }

    @Override
    public void getBancontactProcess(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("success");

            if (message.equals("true")) {
                Toast.makeText(this, R.string.payment_success_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MyLessonsActivity.class);
                startActivity(intent);

            } else if (message.equals("loaded")) {
                Toast.makeText(this, R.string.load_wallet_by_credit_card_sucess_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), VirtualWalletActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), R.string.payment_error_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethodActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.equals("https://homologation-secure-p.payline.com/webpayment/mpiServletProxy.do?reqCode=enrollment")) {
                Intent intent = new Intent(getApplicationContext(), MangoPayWebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            } else if (url.equals("https://homologation-secure-p.payline.com/webpayment/step2.do?reqCode=doStep2")) {
                Intent intent = new Intent(getApplicationContext(), MangoPayWebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            } else if (url.equals("https://homologation-secure-p.payline.com/webpayment/step1.do?reqCode=prepareStep1")) {
                Toast.makeText(getApplicationContext(), R.string.payment_error_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethodActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else {
                startRedirectURLAsyncTask(url);
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog = new ProgressDialog(MangoPayWebViewActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
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
}
