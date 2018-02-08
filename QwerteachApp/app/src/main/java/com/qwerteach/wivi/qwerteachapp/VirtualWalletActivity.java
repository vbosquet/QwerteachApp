package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.fragments.IndexVirtualWalletFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VirtualWalletActivity extends AppCompatActivity {

    ActionBar actionBar;
    Intent intent;
    ProgressDialog progressDialog;
    double totalWallet;
    QwerteachService service;
    User user;
    TextView balanceWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_wallet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        balanceWallet = (TextView) findViewById(R.id.wallet_balance);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);
        getTotalWallet();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getTotalWallet() {
        startProgressDialog();
        Call<JsonResponse> call = service.getTotalWallet(user.getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    totalWallet = response.body().getTotalWallet();
                    balanceWallet.setText("Argent disponible pour réserver des cours : " + totalWallet/100 + "€");
                    displayIndexVirtualWalletFragment();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("failure", String.valueOf(t.getMessage()));
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayIndexVirtualWalletFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, IndexVirtualWalletFragment.newInstance(), "INDEX_WALLET");
        transaction.commit();
    }


    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
}
