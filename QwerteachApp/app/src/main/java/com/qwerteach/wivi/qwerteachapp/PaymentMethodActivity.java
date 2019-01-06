package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mangopay.android.sdk.Callback;
import com.mangopay.android.sdk.MangoPayBuilder;
import com.mangopay.android.sdk.model.CardRegistration;
import com.mangopay.android.sdk.model.exception.MangoException;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String NEW_CREDIT_CARD = "Nouvelle carte de crédit";
    public static final String BANCONTACT_MODE = "bancontact";
    public static final String TRANSFER_MODE = "transfert";
    public static final String CREDIT_CARD_MODE = "cd";

    Float totalPrice;
    TextView totalWalletTextView, bancontactTextView;
    Spinner otherPaymentMethodSpinner, creditCardSpinner, endMonthSpinner, endYearSpinner;
    CheckBox paymentWithVirtualWallet;
    LinearLayout newCreditCardLinearLayout, creditCardChoiceLinearLayout;
    ArrayList<String> otherPaymentMethods, months, years, creditCards;
    Integer teacherId, totalWallet;
    String cardId, currentAlias, currentMonth, currentYear, paymentMode = "", clientId;
    ArrayList<UserCreditCard> userCreditCards;
    EditText cardNumberEditText, cvvEditText;
    CardRegistrationData cardRegistrationData;
    QwerteachService service;
    Call<JsonResponse> call;
    User currentUser, teacher;
    Intent intent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        totalWalletTextView = (TextView) findViewById(R.id.total_wallet_text_view);
        otherPaymentMethodSpinner = (Spinner) findViewById(R.id.other_paiment_method_spinner);
        paymentWithVirtualWallet = (CheckBox) findViewById(R.id.payment_with_virtual_wallet);
        cvvEditText = (EditText) findViewById(R.id.card_validity_edit_text);
        cardNumberEditText = (EditText) findViewById(R.id.card_number_edit_text);
        newCreditCardLinearLayout = (LinearLayout) findViewById(R.id.new_credit_card_linear_layout);
        endMonthSpinner = (Spinner) findViewById(R.id.end_month_spinner);
        endYearSpinner = (Spinner) findViewById(R.id.end_year_spinner);
        creditCardSpinner = (Spinner) findViewById(R.id.credit_card_choice_spinner);
        creditCardChoiceLinearLayout = (LinearLayout) findViewById(R.id.credit_card_choice_linear_layout);
        //bancontactTextView = (TextView) findViewById(R.id.bancontact_text_view);
        paymentWithVirtualWallet.setOnCheckedChangeListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String userJson = preferences.getString("user", "");
        String teacherJson = preferences.getString("teacher", "");
        String cardRegistrationDataJson = preferences.getString("cardRegistration", "");
        String creditCardsJson = preferences.getString("userCreditCardList", "");

        currentUser = gson.fromJson(userJson, User.class);
        totalPrice = preferences.getFloat("totalPrice", 0);
        clientId = preferences.getString("clientId", "");
        teacher = gson.fromJson(teacherJson, User.class);
        cardRegistrationData = gson.fromJson(cardRegistrationDataJson, CardRegistrationData.class);
        Type type = new TypeToken<ArrayList<UserCreditCard>>(){}.getType();
        userCreditCards = gson.fromJson(creditCardsJson, type);

        teacherId = teacher.getUserId();
        months = new ArrayList<>();
        years = new ArrayList<>();
        creditCards = new ArrayList<>();
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);

        otherPaymentMethods = new ArrayList<>();
        otherPaymentMethods.add("Choisissez votre mode de paiement");
        otherPaymentMethods.add("Carte de crédit");
        //otherPaymentMethods.add("Bancontact");

        setCreditCards();
        setYears();
        setMonths();

        startProgressDialog();
        call = service.getTotalWallet(currentUser.getUserId(), currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    totalWallet = response.body().getTotalWallet();
                    totalWalletTextView.setText("Solde de mon Portefeuille : " + totalWallet/100 + "€");
                    displayOtherPaymentMethodSpinner();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
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

    public void displayOtherPaymentMethodSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, otherPaymentMethods);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        otherPaymentMethodSpinner.setAdapter(arrayAdapter);
        otherPaymentMethodSpinner.setOnItemSelectedListener(this);

    }

    public void setCreditCards() {
        creditCards.add(NEW_CREDIT_CARD);
        for (int i = 0; i < userCreditCards.size(); i++) {
            String alias = userCreditCards.get(i).getAlias();
            creditCards.add(alias);
        }
    }

    public void setYears() {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int j = 2026; j >= thisYear; j--) {
            years.add(Integer.toString(j));
        }
        Collections.reverse(years);
        years.add("aaaa");
    }

    public void setMonths() {
        months.add("mm");
        for (int j = 1; j <= 12; j++) {
            if (j >= 10) {
                months.add(String.valueOf(j));
            } else {
                months.add("0" + j);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.other_paiment_method_spinner:
                String otherPaymentMethodName = adapterView.getItemAtPosition(i).toString();
                if (otherPaymentMethodName.equals(otherPaymentMethods.get(0))) {
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    creditCardChoiceLinearLayout.setVisibility(View.GONE);
                    //bancontactTextView.setVisibility(View.GONE);
                } else if (otherPaymentMethodName.equals(otherPaymentMethods.get(1))) {
                    paymentMode = CREDIT_CARD_MODE;
                    if (userCreditCards.size() > 0) {
                        creditCardChoiceLinearLayout.setVisibility(View.VISIBLE);
                        newCreditCardLinearLayout.setVisibility(View.GONE);
                        //bancontactTextView.setVisibility(View.GONE);
                        paymentWithVirtualWallet.setChecked(false);
                        setCreditCardSpinner();
                    } else {
                        currentAlias = NEW_CREDIT_CARD;
                        newCreditCardLinearLayout.setVisibility(View.VISIBLE);
                        //bancontactTextView.setVisibility(View.GONE);
                        paymentWithVirtualWallet.setChecked(false);
                        addNewCreditCard();
                    }
                } /*else if (otherPaymentMethodName.equals(otherPaymentMethods.get(2))) {
                    paymentMode = BANCONTACT_MODE;
                    bancontactTextView.setVisibility(View.VISIBLE);
                    creditCardChoiceLinearLayout.setVisibility(View.GONE);
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                    paymentWithVirtualWallet.setChecked(false);
                }*/
                break;
            case R.id.credit_card_choice_spinner:
                currentAlias = adapterView.getItemAtPosition(i).toString();
                if (currentAlias.equals(creditCards.get(0))) {
                    newCreditCardLinearLayout.setVisibility(View.VISIBLE);
                    addNewCreditCard();
                } else {
                    newCreditCardLinearLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.end_month_spinner:
                currentMonth = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.end_year_spinner:
                currentYear = adapterView.getItemAtPosition(i).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void addNewCreditCard() {
        displayEndMonthSpinner();
        displayEndYearSpinner();

        cvvEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(3);
        cvvEditText .setFilters(filters);
    }

    public void displayEndYearSpinner() {
        ArrayAdapter  yearsAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, years) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                int lastPosition = years.size() -1;

                if (position == lastPosition) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        yearsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        endYearSpinner.setAdapter(yearsAdapter);
        endYearSpinner.setSelection(years.size() - 1);
        endYearSpinner.setOnItemSelectedListener(this);
    }

    public void displayEndMonthSpinner() {
        ArrayAdapter monthsAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, months) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        monthsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        endMonthSpinner.setAdapter(monthsAdapter);
        endMonthSpinner.setOnItemSelectedListener(this);
    }

    public void setCreditCardSpinner() {
        ArrayAdapter<String> creditCardAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, creditCards);
        creditCardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        creditCardSpinner.setAdapter(creditCardAdapter);
        creditCardSpinner.setSelection(1);
        creditCardSpinner.setOnItemSelectedListener(this);
    }

    public void didTouchPaymentButton(View view) {
        startProgressDialog();
        switch (paymentMode) {
            case TRANSFER_MODE:
                if (totalWallet < totalPrice) {
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.total_wallet_insufficient_toast_message, Toast.LENGTH_SHORT).show();
                } else {
                    payLesson();
                }

                break;
            case CREDIT_CARD_MODE:
                if (!currentAlias.equals(NEW_CREDIT_CARD)) {
                    for (int i = 0; i < userCreditCards.size(); i++) {
                        if (userCreditCards.get(i).getAlias().equals(currentAlias)) {
                            cardId = userCreditCards.get(i).getCardId();
                        }
                    }
                    payLesson();

                } else {
                    createNewCreditCard();
                }

                break;
            case BANCONTACT_MODE:
                payLesson();
                break;
        }
    }

    public void createNewCreditCard() {
        MangoPayBuilder builder = new MangoPayBuilder(this);
        builder.baseURL("https://api.mangopay.com")
                .clientId(clientId)
                .accessKey(cardRegistrationData.getAccessKey())
                .cardRegistrationURL(cardRegistrationData.getCardRegistrationURL())
                .preregistrationData(cardRegistrationData.getPreRegistrationData())
                .cardPreregistrationId(cardRegistrationData.getCardPreregistrationId())
                .cardNumber(cardNumberEditText.getText().toString())
                .cardExpirationMonth(Integer.parseInt(currentMonth))
                .cardExpirationYear(Integer.parseInt(currentYear))
                .cardCvx(cvvEditText.getText().toString())
                .callback(new Callback() {
                    @Override
                    public void success(CardRegistration cardRegistration) {
                        cardId = cardRegistration.getCardId();
                        payLesson();
                    }

                    @Override
                    public void failure(MangoException error) {
                        progressDialog.dismiss();
                        Toast.makeText(PaymentMethodActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            paymentMode = TRANSFER_MODE;
        } else {
            paymentMode = "";
        }
    }

    public void payLesson() {
        if (paymentMode.equals(CREDIT_CARD_MODE)) {
            call = service.payLessonWithCreditCard(teacherId, paymentMode, cardId, currentUser.getEmail(), currentUser.getToken());
        } else {
            call = service.payLesson(teacherId, paymentMode, currentUser.getEmail(), currentUser.getToken());
        }

       call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String message = response.body().getMessage();
                    switch (message) {
                        case "result":
                            String url = response.body().getUrl();
                            intent = new Intent(getApplication(), MangoPaySecureModeActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("mode", paymentMode);
                            startActivity(intent);
                            break;
                        case "finish":
                        case"true": {
                            Toast.makeText(getApplication(), R.string.payment_success_toast_message, Toast.LENGTH_LONG).show();
                            intent = new Intent(getApplication(), MyLessonsActivity.class);
                            intent.putExtra("position", 1);
                            startActivity(intent);
                            break;
                        }
                        case "errors":
                            Toast.makeText(getApplication(), "Une erreur s'est produite. Veuillez réessayer ultérieurement", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
