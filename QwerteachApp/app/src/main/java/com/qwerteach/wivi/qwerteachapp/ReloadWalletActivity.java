package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mangopay.android.sdk.Callback;
import com.mangopay.android.sdk.MangoPayBuilder;
import com.mangopay.android.sdk.model.CardRegistration;
import com.mangopay.android.sdk.model.exception.MangoException;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ReloadWalletActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    ActionBar actionBar;
    ArrayList<String> amounts, months, years, easyPayments;
    Spinner amountToReloadSpinner, creditCardListSpinner, yearSpinner, monthSpinner;
    CheckBox visaCheckbox, mastercardCheckbox, cbCheckbox, bcmcCheckbox, bankWireCheckbox, easyPaymentCheckBox;
    LinearLayout cardNumberLinearLayout, newCreditCardLinearLayout;
    EditText otherAmountEditText, cardNumberEditText, securityCodeEditText;
    String currentAmount, cardType = "", currentCardNumber = "", cardId, currentMonth, currentYear, paymentMode;
    String email, token;
    ArrayList<UserCreditCard> userCreditCards;
    CardRegistrationData cardRegistrationData;
    TextView noCreditCardForEasyPaymentTextView;
    QwerteachService service;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userCreditCards = new ArrayList<>();
        months = new ArrayList<>();
        years = new ArrayList<>();
        easyPayments = new ArrayList<>();
        service = ApiClient.getClient().create(QwerteachService.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userCreditCards = (ArrayList<UserCreditCard>) getIntent().getSerializableExtra("easy_payment");
            cardRegistrationData = (CardRegistrationData) getIntent().getSerializableExtra("card_registration");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        amounts = new ArrayList<>();
        amounts.add("20");
        amounts.add("50");
        amounts.add("100");
        amounts.add("Autre montant");

        otherAmountEditText = (EditText) findViewById(R.id.other_amount_edit_text);
        amountToReloadSpinner = (Spinner) findViewById(R.id.amount_spinner);
        visaCheckbox = (CheckBox) findViewById(R.id.visa);
        mastercardCheckbox = (CheckBox) findViewById(R.id.mastercard);
        cbCheckbox = (CheckBox) findViewById(R.id.cb);
        bcmcCheckbox = (CheckBox) findViewById(R.id.bcmc);
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

        visaCheckbox.setOnClickListener(this);
        mastercardCheckbox.setOnClickListener(this);
        cbCheckbox.setOnClickListener(this);
        bcmcCheckbox.setOnClickListener(this);
        bankWireCheckbox.setOnClickListener(this);
        easyPaymentCheckBox.setOnClickListener(this);

        if (userCreditCards.size() > 0) {
            for (int i = 0; i < userCreditCards.size(); i++) {
                if (userCreditCards.get(i).getValidity().equals("VALID")) {
                    easyPayments.add(userCreditCards.get(i).getCardProvider() + " - " + userCreditCards.get(0).getAlias() +
                            " - " + userCreditCards.get(i).getExpirationDate());
                }
            }

            easyPaymentCheckBox.setText(easyPayments.get(0));
        } else {
            easyPaymentCheckBox.setVisibility(View.GONE);
            noCreditCardForEasyPaymentTextView.setVisibility(View.VISIBLE);
        }

        ArrayAdapter amountToReloadAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, amounts);
        amountToReloadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amountToReloadSpinner.setAdapter(amountToReloadAdapter);
        amountToReloadSpinner.setOnItemSelectedListener(this);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.amount_spinner:
                currentAmount = adapterView.getItemAtPosition(i).toString();

                if (currentAmount.equals(amounts.get(amounts.size()-1))) {
                    otherAmountEditText.setVisibility(View.VISIBLE);
                    currentAmount = otherAmountEditText.getText().toString();
                } else {
                    otherAmountEditText.setVisibility(View.GONE);
                    currentAmount = adapterView.getItemAtPosition(i).toString();
                }

                break;
            case R.id.card_list_spinner:
                currentCardNumber = adapterView.getItemAtPosition(i).toString();

                if (currentCardNumber.equals("Nouvelle carte")) {
                    newCreditCardLinearLayout.setVisibility(View.VISIBLE);
                    setNewCreditCardLayout();
                } else {
                    newCreditCardLinearLayout.setVisibility(View.GONE);

                    for (int j = 0; j < userCreditCards.size(); j++) {
                        if (currentCardNumber.equals(userCreditCards.get(i).getAlias())) {
                            cardId = userCreditCards.get(i).getCardId();
                        } else {
                            cardId = "";
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

        if (currentCardNumber.equals("Nouvelle carte")) {

            String year = currentYear.substring(2);
            String cardNumber = cardNumberEditText.getText().toString();
            String expirationDate = currentMonth + year;
            String securityCode = securityCodeEditText.getText().toString();

            MangoPayBuilder builder = new MangoPayBuilder(this);
            builder.baseURL("https://api.sandbox.mangopay.com")
                    .clientId("qwerteachrails")
                    .accessKey(cardRegistrationData.getAccessKey())
                    .cardRegistrationURL(cardRegistrationData.getCardRegistrationURL())
                    .preregistrationData(cardRegistrationData.getPreRegistrationData())
                    .cardPreregistrationId(cardRegistrationData.getCardPreregistrationId())
                    .cardNumber(cardNumber)
                    .cardExpirationDate(expirationDate)
                    .cardCvx(securityCode)
                    .callback(new Callback() {
                        @Override public void success(CardRegistration cardRegistration) {
                            Log.d(MainActivity.class.getSimpleName(), cardRegistration.toString());
                            cardId = cardRegistration.getCardId();
                            cardType = "CB_VISA_MASTERCARD";

                            startLoadWallet();
                        }

                        @Override
                        public void failure(MangoException error) {
                            Toast.makeText(ReloadWalletActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }).start();
        } else {
            startLoadWallet();
        }
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
                    bcmcCheckbox.setChecked(false);
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

                    cardType = "CB_VISA_MASTERCARD";
                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    paymentMode = "cd";

                }
                break;
            case R.id.visa:
                if (checked) {
                    cardType = "CB_VISA_MASTERCARD";
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(true);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                }
                break;
            case R.id.mastercard:
                if (checked) {
                    cardType = "CB_VISA_MASTERCARD";
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(true);
                    cbCheckbox.setChecked(false);
                    bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                }
                break;
            case R.id.cb:
                if (checked) {
                    cardType = "CB_VISA_MASTERCARD";
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(true);
                    bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(false);

                    setCreditCardSpinner();
                    paymentMode = "cd";
                }
                break;
            case R.id.bcmc:
                if (checked) {
                    cardType = "BCMC";
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    bcmcCheckbox.setChecked(true);
                    bankWireCheckbox.setChecked(false);

                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    paymentMode = "bancontact";
                }
                break;
            case R.id.banck_wire:
                if (checked) {
                    cardType = "BANK_WIRE";
                    easyPaymentCheckBox.setChecked(false);
                    visaCheckbox.setChecked(false);
                    mastercardCheckbox.setChecked(false);
                    cbCheckbox.setChecked(false);
                    bcmcCheckbox.setChecked(false);
                    bankWireCheckbox.setChecked(true);

                    cardNumberLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                }
                break;
        }

    }

    public void setCreditCardSpinner() {
        ArrayList<String> creditCardList = new ArrayList<>();
        creditCardList.add("Nouvelle carte");

        if (userCreditCards.size() > 0) {
            for (int j = 0; j < userCreditCards.size(); j++) {
                creditCardList.add(userCreditCards.get(j).getAlias());
            }
        }

        cardNumberLinearLayout.setVisibility(View.VISIBLE);

        ArrayAdapter creditcardAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, creditCardList);
        creditcardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        creditCardListSpinner.setAdapter(creditcardAdapter);
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
        for (int j = 2026; j >= thisYear; j--) {
            years.add(Integer.toString(j));
        }

        Collections.reverse(years);

        ArrayAdapter monthsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthsAdapter);
        monthSpinner.setOnItemSelectedListener(this);

        ArrayAdapter  yearsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearsAdapter);
        yearSpinner.setOnItemSelectedListener(this);

        securityCodeEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(3);
        securityCodeEditText .setFilters(filters);
    }

    public void startLoadWallet() {
        Map<String, String> resquestBody = new HashMap<>();
        resquestBody.put("amount", currentAmount);
        resquestBody.put("card_type", cardType);

        if (cardId != null) {
            resquestBody.put("card", cardId);
        }

        Call<JsonResponse> call = service.loadUserWallet(resquestBody, email, token);
        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();
                String url = response.body().getUrl();
                displayConfirmationMessage(message, url);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void displayConfirmationMessage(String message, String url) {
        switch (message) {
            case "true":
                intent = new Intent(this, VirtualWalletActivity.class);
                startActivity(intent);
                Toast.makeText(this, R.string.load_wallet_by_credit_card_sucess_toast_message, Toast.LENGTH_SHORT).show();
                break;
            case "redirect url":
                intent = new Intent(this, MangoPaySecureModeActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("mode", paymentMode);
                startActivity(intent);
                break;
        }
    }
}
