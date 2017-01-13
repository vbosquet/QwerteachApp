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
import android.view.ViewGroup;
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
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllWalletInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetPreRegistrationCardDataAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.LoadWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ReloadWalletActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        LoadWalletAsyncTask.ILoadWallet {

    ActionBar actionBar;
    ArrayList<String> amounts;
    Spinner amountToReloadSpinner, creditCardListSpinner, yearSpinner, monthSpinner;
    CheckBox visaCheckbox, mastercardCheckbox, cbCheckbox, bcmcCheckbox, bankWireCheckbox, easyPaymentCheckBox;
    LinearLayout cardNumberLinearLayout, newCreditCardLinearLayout;
    EditText otherAmountEditText, cardNumberEditText, securityCodeEditText;
    String currentAmount, cardType = "", currentCardNumber = "", cardId, currentMonth, currentYear;
    String email, token;
    ArrayList<UserCreditCard> userCreditCards;
    ArrayList<String> months, years;
    CardRegistrationData cardRegistrationData;
    TextView noCreditCardForEasyPaymentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userCreditCards = new ArrayList<>();
        months = new ArrayList<>();
        years = new ArrayList<>();

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
        cardNumberLinearLayout = (LinearLayout) findViewById(R.id.card_numer_linear_layout);
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
            easyPaymentCheckBox.setText(userCreditCards.get(0).getCardProvider() + " - " + userCreditCards.get(0).getAlias() +
                    " - " + userCreditCards.get(0).getExpirationDate());
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

                            startLoadWalletAsyncTask();
                        }

                        @Override
                        public void failure(MangoException error) {
                            Toast.makeText(ReloadWalletActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }).start();
        } else {
            startLoadWalletAsyncTask();
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
                        cardId = userCreditCards.get(0).getCardId();
                    }

                    cardType = "CB_VISA_MASTERCARD";
                    cardNumberLinearLayout.setVisibility(View.GONE);

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

    @Override
    public void loadWallet(String string) {

        try {
            JSONObject jsonObject =new JSONObject(string);
            String message = jsonObject.getString("message");

            if (message.equals("true")) {
                Intent intent = new Intent(this, VirtualWalletActivity.class);
                startActivity(intent);
                Toast.makeText(this, R.string.load_wallet_by_credit_card_sucess_toast_message, Toast.LENGTH_SHORT).show();

            } else if(message.equals("error")) {


            } else if (message.equals("secure mode")) {
                String url = jsonObject.getString("url");
                Intent intent = new Intent(this, RegisterNewCardActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            } else if (message.equals("redirect url")) {
                String returnURL = jsonObject.getString("url");
                Intent intent = new Intent(this, MangoPayWebViewActivity.class);
                intent.putExtra("url", returnURL);
                startActivity(intent);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void startLoadWalletAsyncTask() {
        LoadWalletAsyncTask loadWalletAsyncTask = new LoadWalletAsyncTask(this);
        loadWalletAsyncTask.execute(email, token, currentAmount, cardType, cardId);
    }
}