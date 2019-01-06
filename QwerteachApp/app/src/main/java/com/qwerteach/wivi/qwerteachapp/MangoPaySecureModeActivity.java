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

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RedirectURLAsyncTask;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangoPaySecureModeActivity extends AppCompatActivity implements RedirectURLAsyncTask.IRedirectURL {

    String url;
    ProgressDialog progressDialog;
    QwerteachService service;
    User user;

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
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(MangoPaySecureModeActivity.this);
        progressDialog.setMessage("Loading...");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(url);
    }

    @Override
    public void redirectURL(String string) {
        Document doc = Jsoup.parse(string);
        Element link = doc.select("a").first();
        String linkHref = link.attr("href");
        finalizePaymentWithCard(formatingUrl(linkHref));
    }

    public void startRedirectURL(String newURL) {
        RedirectURLAsyncTask redirectURLAsyncTask = new RedirectURLAsyncTask(this);
        redirectURLAsyncTask.execute(user.getEmail(), user.getToken(), newURL);
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

    public String formatingUrl(String url) {
        Uri uri = Uri.parse(url);
        String protocol = uri.getScheme();
        String server = uri.getAuthority();
        String path = uri.getPath();
        String transactionId = uri.getQueryParameter("transactionId");
        return protocol + "://" + server + "/api" + path + "?transactionId=" + transactionId;
    }

    public void finalizePaymentWithCard(String url) {
        Log.d("FORMATED_URL", url);
        Call<JsonResponse> call = service.finalizePaymentWithCard(url, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.isSuccessful()) {
                    String message = null;
                    String success = response.body().getSuccess();
                    Transaction transaction = response.body().getTransaction();

                    if (transaction != null) {
                        if (transaction.getStatus().equals("FAILED")) {
                            message = "L'authentification par 3DSecure a échoué. Vous n'avez pas été débité, et votre portefeuille virtuel Qwerteach n'a pas été chargé.";
                        } else {
                            message = "Votre portefeuille virtuel a bien été rechargé.";
                        }
                    }

                    switch (success) {
                        case "true": {
                            Toast.makeText(getApplication(), R.string.payment_success_toast_message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MyLessonsActivity.class);
                            intent.putExtra("position", 1);
                            startActivity(intent);
                            break;
                        }
                        case "loaded": {
                            Intent intent = new Intent(getApplicationContext(), VirtualWalletActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            break;
                        }
                        default: {
                            Toast.makeText(getApplicationContext(), R.string.payment_error_toast_message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), PaymentMethodActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("URL", url);

            if (url.contains("reqCode=enrollment") || url.contains("reqCode=doStep2") || url.contains("3dsecure")) {
                Intent intent = new Intent(getApplicationContext(), MangoPaySecureModeActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            } else if (url.contains("reqCode=prepareStep1")) {
                Toast.makeText(getApplicationContext(), R.string.payment_error_toast_message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PaymentMethodActivity.class);
                startActivity(intent);
            } else {
                finalizePaymentWithCard(formatingUrl(url));
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
