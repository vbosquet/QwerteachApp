package com.qwerteach.wivi.qwerteachapp;

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

import com.qwerteach.wivi.qwerteachapp.asyncTasks.MakePayoutAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RedirectURLAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnloadWalletActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        MakePayoutAsyncTask.IMakePayout, RedirectURLAsyncTask.IRedirectURL {

    ActionBar actionBar;
    String email, token;
    ArrayList<UserBankAccount> userBankAccounts;
    double totalWallet;
    TextView amountToBeTransferred;
    Spinner bankAccountSpinner;
    ArrayList<String> accountNumbers;
    ArrayAdapter<String> bankAccountAdapter;
    String currentBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unload_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userBankAccounts = (ArrayList<UserBankAccount>) getIntent().getSerializableExtra("userBankAccounts");
            totalWallet = getIntent().getDoubleExtra("totalWallet", 0);
        }

        accountNumbers = new ArrayList<>();
        for (int i = 0; i < userBankAccounts.size(); i++) {
            String bankAccount = "";
            if (userBankAccounts.get(i).getType().equals("IBAN")) {
                bankAccount = userBankAccounts.get(i).getIban();
            } else {
                bankAccount = userBankAccounts.get(i).getAccountNumber();
            }
            accountNumbers.add(bankAccount);
        }

        amountToBeTransferred = (TextView) findViewById(R.id.amount_to_be_transferred);
        bankAccountSpinner = (Spinner) findViewById(R.id.bank_accounts);

        amountToBeTransferred.setText("Montant à transférer : " + totalWallet/100 + " €");
        bankAccountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountNumbers);
        bankAccountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankAccountSpinner.setAdapter(bankAccountAdapter);
        bankAccountSpinner.setOnItemSelectedListener(this);

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

    public void didTouchUnloadWalletButton(View view) {
        MakePayoutAsyncTask makePayoutAsyncTask = new MakePayoutAsyncTask(this);
        makePayoutAsyncTask.execute(email, token, currentBankAccount);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currentBankAccount = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void makePayoutConfirmationMessage(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                String message = jsonObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, VirtualWalletActivity.class);
                startActivity(intent);
            } else {
                String redirectURL = jsonObject.getString("redirect_url");
                String newURL = "http://192.168.0.125:3000/api/" + redirectURL;

                RedirectURLAsyncTask redirectURLAsyncTask = new RedirectURLAsyncTask(this);
                redirectURLAsyncTask.execute(email, token, newURL);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void redirectURL(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                Toast.makeText(this, R.string.credit_transferred_success_toast_message,Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, VirtualWalletActivity.class);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
