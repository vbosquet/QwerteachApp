package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mangopay.android.sdk.Callback;
import com.mangopay.android.sdk.MangoPay;
import com.mangopay.android.sdk.MangoPayBuilder;
import com.mangopay.android.sdk.model.CardRegistration;
import com.mangopay.android.sdk.model.MangoCard;
import com.mangopay.android.sdk.model.MangoSettings;
import com.mangopay.android.sdk.model.exception.MangoException;
import com.mangopay.android.sdk.util.JsonUtil;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.BankWireData;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Response;

public class ReloadWalletActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    static final String OTHER_AMOUNT = "Autre montant";
    static final String NEW_CREDIT_CARD = "Nouvelle carte";
    static final String CB_VISA_MASTERCARD = "CB_VISA_MASTERCARD";
    //static final String BCMC = "BCMC";
    static final String BANK_WIRE = "BANK_WIRE";

    ActionBar actionBar;
    ArrayList<String> amounts, months, years, easyPayments;
    Spinner amountToReloadSpinner, creditCardListSpinner, yearSpinner, monthSpinner;
    CheckBox visaCheckbox, mastercardCheckbox, cbCheckbox, bcmcCheckbox, bankWireCheckbox, easyPaymentCheckBox;
    LinearLayout cardNumberLinearLayout, newCreditCardLinearLayout, bankWireData;
    EditText otherAmountEditText, cardNumberEditText, securityCodeEditText;
    String currentAmount, cardType = "", currentCardNumber = "", cardId, currentMonth, currentYear, paymentMode;
    ArrayList<UserCreditCard> userCreditCards;
    CardRegistrationData cardRegistrationData;
    TextView noCreditCardForEasyPaymentTextView, bankWireBeneficiary, bankWireAddress, bankWireIban, bankWireBic, bankWireAmount, bankWireCommunication;
    QwerteachService service;
    Intent intent;
    User user;
    ProgressDialog progressDialog;
    Button validationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        service = ApiClient.getClient().create(QwerteachService.class);

        months = new ArrayList<>();
        years = new ArrayList<>();
        easyPayments = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        amounts = new ArrayList<>();
        amounts.add("20");
        amounts.add("50");
        amounts.add("100");
        amounts.add(OTHER_AMOUNT);

        otherAmountEditText = (EditText) findViewById(R.id.other_amount_edit_text);
        amountToReloadSpinner = (Spinner) findViewById(R.id.amount_spinner);
        visaCheckbox = (CheckBox) findViewById(R.id.visa);
        mastercardCheckbox = (CheckBox) findViewById(R.id.mastercard);
        cbCheckbox = (CheckBox) findViewById(R.id.cb);
        //bcmcCheckbox = (CheckBox) findViewById(R.id.bcmc);
        bankWireCheckbox = (CheckBox) findViewById(R.id.banck_wire);
        easyPaymentCheckBox = (CheckBox) findViewById(R.id.easy_payment);
        cardNumberLinearLayout = (LinearLayout) findViewById(R.id.card_number_linear_layout);
        creditCardListSpinner = (Spinner) findViewById(R.id.card_list_spinner);
        newCreditCardLinearLayout = (LinearLayout) findViewById(R.id.new_credit_card_linear_layout);
        cardNumberEditText = (EditText) findViewById(R.id.card_number_edit_text);
        securityCodeEditText = (EditText) findViewById(R.id.security_code_edit_text);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        monthSpinner = (Spinner) findViewById(R.id.month_spinner);
        noCreditCardForEasyPaymentTextView = (TextView) findViewById(R.id.no_credit_card_for_easy_payment_text_view);
        bankWireData = (LinearLayout) findViewById(R.id.bank_wire_data);
        bankWireBeneficiary = (TextView) findViewById(R.id.bank_wire_beneficiary);
        bankWireAddress = (TextView) findViewById(R.id.bank_wire_address);
        bankWireIban = (TextView) findViewById(R.id.bank_wire_iban);
        bankWireBic = (TextView) findViewById(R.id.bank_wire_bic);
        bankWireAmount = (TextView) findViewById(R.id.bank_wire_amount);
        bankWireCommunication = (TextView) findViewById(R.id.bank_wire_communication);
        validationButton = (Button) findViewById(R.id.validation_button);

        visaCheckbox.setOnClickListener(this);
        mastercardCheckbox.setOnClickListener(this);
        cbCheckbox.setOnClickListener(this);
        //bcmcCheckbox.setOnClickListener(this);
        bankWireCheckbox.setOnClickListener(this);
        easyPaymentCheckBox.setOnClickListener(this);

        getPreRegistrationCardData();
    }

    public void displayInitialLayout() {
        if (userCreditCards.size() > 0) {
            for (int i = 0; i < userCreditCards.size(); i++) {
                if (userCreditCards.get(i).getValidity().equals("VALID")) {
                    easyPayments.add(userCreditCards.get(i).getCardProvider() + " - " + userCreditCards.get(0).getAlias() +
                            " - " + userCreditCards.get(i).getExpirationDate());
                }
            }

            if (easyPayments.size() > 0) {
                easyPaymentCheckBox.setText(easyPayments.get(0));
            } else {
                easyPaymentCheckBox.setVisibility(View.GONE);
                noCreditCardForEasyPaymentTextView.setVisibility(View.VISIBLE);
            }
        } else {
            easyPaymentCheckBox.setVisibility(View.GONE);
            noCreditCardForEasyPaymentTextView.setVisibility(View.VISIBLE);
        }

        ArrayAdapter amountToReloadAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, amounts);
        amountToReloadAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        amountToReloadSpinner.setAdapter(amountToReloadAdapter);
        amountToReloadSpinner.setOnItemSelectedListener(this);
    }

    public void getPreRegistrationCardData() {
        startProgressDialog();
        Call<JsonResponse> call = service.getPreRegistrationCardData(user.getEmail(), user.getToken());
        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                cardRegistrationData = response.body().getCardRegistrationData();
                userCreditCards = response.body().getUserCreditCards();
                displayInitialLayout();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
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
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.amount_spinner:
                currentAmount = adapterView.getItemAtPosition(i).toString();

                if (currentAmount.equals(OTHER_AMOUNT)) {
                    otherAmountEditText.setVisibility(View.VISIBLE);
                } else {
                    otherAmountEditText.setVisibility(View.GONE);
                    currentAmount = adapterView.getItemAtPosition(i).toString();
                }

                break;
            case R.id.card_list_spinner:
                currentCardNumber = adapterView.getItemAtPosition(i).toString();

                if (currentCardNumber.equals(NEW_CREDIT_CARD)) {
                    newCreditCardLinearLayout.setVisibility(View.VISIBLE);
                    setNewCreditCardLayout();
                } else {
                    newCreditCardLinearLayout.setVisibility(View.GONE);

                    for (int j = 0; j < userCreditCards.size(); j++) {
                        if (currentCardNumber.equals(userCreditCards.get(j).getAlias())) {
                            cardId = userCreditCards.get(j).getCardId();
                        }
                    }
                }

                break;
            case R.id.month_spinner:
                currentMonth = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.year_spinner:
                currentYear = adapterView.getItemAtPosition(i).toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void didTouchValidateButton(View view) {
        if (currentCardNumber.equals(NEW_CREDIT_CARD)) {
            if (cardNumberEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), R.string.check_card_number, Toast.LENGTH_LONG).show();
            } else if (checkEpirationDate()) {
                Toast.makeText(getApplicationContext(), R.string.check_expiration_date, Toast.LENGTH_LONG).show();
            } else {
                startProgressDialog();
                createNewCreditCard();
            }

        } else {
            startProgressDialog();
            startLoadWallet();
        }
    }

    private void createNewCreditCard() {
        MangoPayBuilder builder = new MangoPayBuilder(this);
        builder.baseURL("https://api.sandbox.mangopay.com")
                .clientId("qwerteachrails")
                .accessKey(cardRegistrationData.getAccessKey())
                .cardRegistrationURL(cardRegistrationData.getCardRegistrationURL())
                .preregistrationData(cardRegistrationData.getPreRegistrationData())
                .cardPreregistrationId(cardRegistrationData.getCardPreregistrationId())
                .cardNumber(cardNumberEditText.getText().toString())
                .cardExpirationMonth(Integer.parseInt(currentMonth))
                .cardExpirationYear(Integer.parseInt(currentYear))
                .cardCvx(securityCodeEditText.getText().toString())
                .callback(new Callback() {
                    @Override public void success(CardRegistration cardRegistration) {
                        cardId = cardRegistration.getCardId();
                        cardType = CB_VISA_MASTERCARD;
                        startLoadWallet();
                    }

                    @Override
                    public void failure(MangoException error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReloadWalletActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).start();
    }

    private boolean checkEpirationDate() {
        boolean isExpired = false;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

        if (Integer.parseInt(currentYear) == year && Integer.parseInt(currentMonth) < month) {
            isExpired = true;
        }

        return isExpired;
    }

    @Override
    public void onClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.easy_payment:
                if (checked) {
                    easyPaymentCheckBox.setChecked(true);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    //bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    if (userCreditCards.size() > 0) {
                        ArrayList<String> cardIdList = new ArrayList<>();

                        for (int i = 0; i < userCreditCards.size(); i++) {
                            if (userCreditCards.get(i).getValidity().equals("VALID")) {
                                cardIdList.add(userCreditCards.get(i).getCardId());
                            }
                        }

                        cardId = cardIdList.get(0);
                    }

                    cardType = CB_VISA_MASTERCARD;
                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    bankWireData.setVisibility(View.GONE);
                    validationButton.setVisibility(View.VISIBLE);
                    paymentMode = "cd";

                }
                break;
            case R.id.visa:
                if (checked) {
                    cardType = CB_VISA_MASTERCARD;
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(true);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    //bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                    bankWireData.setVisibility(View.GONE);
                    validationButton.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.mastercard:
                if (checked) {
                    cardType = CB_VISA_MASTERCARD;
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(true);
                    cbCheckbox.setChecked(false);
                    //bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                    bankWireData.setVisibility(View.GONE);
                    validationButton.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.cb:
                if (checked) {
                    cardType = CB_VISA_MASTERCARD;
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(true);
                    //bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                    bankWireData.setVisibility(View.GONE);
                    validationButton.setVisibility(View.VISIBLE);
                }
                break;
            /*case R.id.bcmc:
                if (checked) {
                    cardType = BCMC;
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    bcmcCheckbox.setChecked(true);
                    bankWireCheckbox.setChecked(false);

                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    bankWireData.setVisibility(View.GONE);
                    validationButton.setVisibility(View.VISIBLE);
                    paymentMode = "bancontact";
                }
                break;*/
            case R.id.banck_wire:
                if (checked) {
                    cardType = BANK_WIRE;
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    //bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(true);

                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                }
                break;
        }

    }

    public void setCreditCardSpinner() {
        ArrayList<String> creditCardList = new ArrayList<>();
        creditCardList.add(NEW_CREDIT_CARD);

        if (userCreditCards.size() > 0) {
            for (int j = 0; j < userCreditCards.size(); j++) {
                creditCardList.add(userCreditCards.get(j).getAlias());
            }
        }

        cardNumberLinearLayout.setVisibility(View.VISIBLE);

        ArrayAdapter creditcardAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, creditCardList);
        creditcardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        creditCardListSpinner.setAdapter(creditcardAdapter);
        if (creditCardList.size() > 1) {
            creditCardListSpinner.setSelection(1);
        }
        creditCardListSpinner.setOnItemSelectedListener(this);

    }

    public void setNewCreditCardLayout() {
        for (int j = 1; j <= 12; j++) {
            if (j >= 10) {
                months.add(String.valueOf(j));
            } else {
                months.add("0" + j);
            }
        }

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int j = 2067; j >= thisYear; j--) {
            years.add(Integer.toString(j));
        }

        Collections.reverse(years);

        ArrayAdapter monthsAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, months);
        monthsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthsAdapter);
        monthSpinner.setOnItemSelectedListener(this);

        ArrayAdapter  yearsAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearsAdapter);
        yearSpinner.setOnItemSelectedListener(this);

        securityCodeEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(3);
        securityCodeEditText .setFilters(filters);
    }

    public void startLoadWallet() {
        Map<String, String> resquestBody = new HashMap<>();
        if (currentAmount.equals(OTHER_AMOUNT)) {
            currentAmount = otherAmountEditText.getText().toString();
        }
        resquestBody.put("amount", currentAmount);
        resquestBody.put("card_type", cardType);

        if (cardId != null) {
            resquestBody.put("card", cardId);
        }

        Call<JsonResponse> call = service.loadUserWallet(resquestBody, user.getEmail(), user.getToken());
        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.error_payment_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void displayConfirmationMessage(Response<JsonResponse> response) {
        progressDialog.dismiss();
        String message = response.body().getMessage();
        switch (message) {
            case "true":
                intent = new Intent(this, VirtualWalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(this, R.string.load_wallet_by_credit_card_sucess_toast_message, Toast.LENGTH_SHORT).show();
                break;
            case "redirect url":
                Log.d("REDIRECT_URL", message);
                String url = response.body().getUrl();
                intent = new Intent(this, MangoPaySecureModeActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("mode", paymentMode);
                startActivity(intent);
                break;
            case "error":
                List<String> errorMessages = response.body().getErrorMessages();
                if (errorMessages.size() > 0) {
                    for (int i = 0; i < errorMessages.size(); i++) {
                        Log.d("ERROR", errorMessages.get(i));
                        Toast.makeText(getApplicationContext(), errorMessages.get(i), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case "bank wire":
                BankWireData bankWireData = response.body().getBankWireData();
                displayBankWireData(bankWireData);
                break;
        }
    }

    public void displayBankWireData(BankWireData newBankWireData) {
        bankWireData.setVisibility(View.VISIBLE);
        validationButton.setVisibility(View.GONE);
        bankWireBeneficiary.setText(newBankWireData.getBankAccount().getOwnerName());
        bankWireAddress.setText(newBankWireData.getBankAccount().getAddress());
        bankWireIban.setText(newBankWireData.getBankAccount().getIban());
        bankWireBic.setText(newBankWireData.getBankAccount().getBic());
        bankWireAmount.setText(currentAmount);
        bankWireCommunication.setText(newBankWireData.getWireReference());
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
