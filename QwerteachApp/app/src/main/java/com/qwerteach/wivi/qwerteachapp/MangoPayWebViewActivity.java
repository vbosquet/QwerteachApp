package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.qwerteach.wivi.qwerteachapp.models.Teacher;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Set;

public class MangoPayWebViewActivity extends AppCompatActivity implements RedirectURLAsyncTask.IRedirectURL,
        BancontactProcessAsyncTask.IBancontactProcess {

    String url, email, token;

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
        myWebView.loadUrl(url);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
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
                Toast.makeText(this, "Merci ! Votre demande a bien été envoyée au professeur.", Toast.LENGTH_LONG).show();
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

            Log.i("URL", url);

            if (url.equals("https://homologation-secure-p.payline.com/webpayment/mpiServletProxy.do?reqCode=enrollment")) {
                Intent intent = new Intent(getApplicationContext(), MangoPayWebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            } else if (url.equals("https://homologation-secure-p.payline.com/webpayment/step2.do?reqCode=doStep2")) {
                Intent intent = new Intent(getApplicationContext(), MangoPayWebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            } else if (url.equals("https://homologation-secure-p.payline.com/webpayment/step1.do?reqCode=prepareStep1")) {
                Toast.makeText(getApplicationContext(), "Il y a eu un problème lors de la réservation. Le cours n\'a pas été réservé.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else {
                startRedirectURLAsyncTask(url);
            }

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
}
