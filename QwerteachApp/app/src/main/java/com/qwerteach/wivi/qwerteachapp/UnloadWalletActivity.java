package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RedirectURLAsyncTask;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnloadWalletActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        RedirectURLAsyncTask.IRedirectURL {

    ActionBar actionBar;
    ArrayList<UserBankAccount> userBankAccounts;
    TextView amountToBeTransferred;
    Spinner bankAccountSpinner;
    ArrayList<String> accountNumbers;
    ArrayAdapter<String> bankAccountAdapter;
    String currentBankAccount;
    QwerteachService service;
    ProgressDialog progressDialog;
    User user;
    double totalWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unload_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userBankAccounts = (ArrayList<UserBankAccount>) getIntent().getSerializableExtra("userBankAccounts");
        }

        accountNumbers = new ArrayList<>();
        for (int i = 0; i < userBankAccounts.size(); i++) {
            String bankAccount;
            if (userBankAccounts.get(i).getType().equals("IBAN")) {
                bankAccount = userBankAccounts.get(i).getIban();
            } else {
                bankAccount = userBankAccounts.get(i).getAccountNumber();
            }
            accountNumbers.add(bankAccount);
        }

        amountToBeTransferred = (TextView) findViewById(R.id.amount_to_be_transferred);
        bankAccountSpinner = (Spinner) findViewById(R.id.bank_accounts);
        bankAccountSpinner.setOnItemSelectedListener(this);
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);
        getTotalWallet();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reload_wallet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.cancel_button:
                finish();
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
                totalWallet = response.body().getTotalWallet();
                amountToBeTransferred.setText("Montant à transférer : " + totalWallet/100 + " €");
                bankAccountAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_spinner_item, accountNumbers);
                bankAccountAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                bankAccountSpinner.setAdapter(bankAccountAdapter);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.getMessage());

            }
        });
    }

    public void didTouchUnloadWalletButton(View view) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", currentBankAccount);

        startProgressDialog();
        Call<JsonResponse> call = service.makePayout(requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess();

                if (success.equals("true")) {
                    String message = response.body().getMessage();
                    Intent intent = new Intent(getApplication(), VirtualWalletActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                } else {
                    startRedirectUrlAsyncTask("http://192.168.0.116:3000/api/" + response.body().getUrl());

                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentBankAccount = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startRedirectUrlAsyncTask(String newURL) {
        RedirectURLAsyncTask redirectURLAsyncTask = new RedirectURLAsyncTask(this);
        redirectURLAsyncTask.execute(user.getEmail(), user.getToken(), newURL);
    }

    @Override
    public void redirectURL(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                Intent intent = new Intent(this, VirtualWalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(this, R.string.credit_transferred_success_toast_message,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
