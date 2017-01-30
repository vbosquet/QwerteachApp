package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
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

import com.mangopay.android.sdk.Callback;
import com.mangopay.android.sdk.MangoPayBuilder;
import com.mangopay.android.sdk.model.CardRegistration;
import com.mangopay.android.sdk.model.exception.MangoException;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.PayLessonWithCreditCardAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.PayLessonWithTransfertOrBancontactAsyncTask;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

public class PaymentMethodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    Double totalPrice;
    String userId, email, token;
    TextView totalWalletTextView;
    Spinner otherPaymentMethodSpinner;
    CheckBox paymentWithVirtualWallet;
    LinearLayout otherPaymentMethodDetailsLayout;
    ArrayList<String> otherPaymentMethods, months, years;
    String paymentMode = "";
    Teacher teacher;
    Integer teacherId, totalWallet;
    String cardId, currentAlias, currentMonth, currentYear;
    ArrayList<UserCreditCard> userCreditCards;
    EditText cardNumberEditText, cvvEditText;
    CardRegistrationData cardRegistrationData;
    QwerteachService service;
    Call<JsonResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        totalWalletTextView = (TextView) findViewById(R.id.total_wallet_text_view);
        otherPaymentMethodSpinner = (Spinner) findViewById(R.id.other_paiment_method_spinner);
        paymentWithVirtualWallet = (CheckBox) findViewById(R.id.payment_with_virtual_wallet);
        otherPaymentMethodDetailsLayout = (LinearLayout) findViewById(R.id.other_payment_method_details_layout);
        paymentWithVirtualWallet.setOnCheckedChangeListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
            teacher = (Teacher) getIntent().getSerializableExtra("teacher");
            userCreditCards = (ArrayList<UserCreditCard>) getIntent().getSerializableExtra("userCreditCardList");
            cardRegistrationData = (CardRegistrationData) getIntent().getSerializableExtra("cardRegistration");
        }


        teacherId = teacher.getUser().getUserId();
        months = new ArrayList<>();
        years = new ArrayList<>();
        service = ApiClient.getClient().create(QwerteachService.class);

        otherPaymentMethods = new ArrayList<>();
        otherPaymentMethods.add("Type de paiement");
        otherPaymentMethods.add("Carte de crédit");
        otherPaymentMethods.add("Bancontact");

        call = service.getTotalWallet(userId, email, token);
        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                totalWallet = response.body().getTotalWallet();
                totalWalletTextView.setText("Solde de mon Portefeuille : " + totalWallet/100 + "€");
                displayOtherPaymentMethodSpinner();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

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
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, otherPaymentMethods);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        otherPaymentMethodSpinner.setAdapter(arrayAdapter);
        otherPaymentMethodSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        otherPaymentMethodDetailsLayout.removeAllViews();
        months.clear();
        years.clear();

        String otherPaymentMethodName = adapterView.getItemAtPosition(i).toString();

        if (otherPaymentMethodName.equals(otherPaymentMethods.get(1))) {
            paymentMode = "cd";

            if (userCreditCards.size() > 0) {
                setCreditCardChoice();

            } else {
                currentAlias = "Nouvelle carte de crédit";
                otherPaymentMethodDetailsLayout.addView(addNewCreditCard());
            }

        } else if (otherPaymentMethodName.equals(otherPaymentMethods.get(2))) {
            paymentMode = "bancontact";
            setBancontactForm();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public LinearLayout addNewCreditCard() {

        LinearLayout mainLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainLinearLayout.setLayoutParams(layoutParams);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

        float scale = this.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);

        months.add("mm");
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
        years.add("aaaa");

        TextView endDateTextView = new TextView(this);
        TextView cvvTextView = new TextView(this);
        TextView cardNumberTextView = new TextView(this);

        cardNumberEditText = new EditText(this);
        cvvEditText = new EditText(this);

        Spinner endMonthSpinner = new Spinner(this);
        Spinner endYearSpinner = new Spinner(this);

        LinearLayout endDateLinearLayout = new LinearLayout(this);
        LinearLayout endMonthSpinnerLinearLayout = new LinearLayout(this);
        LinearLayout endYearSpinnerLinearLayout = new LinearLayout(this);

        cardNumberTextView.setText("Numéro de carte");
        endDateTextView.setText("Date expiration");
        cvvTextView.setText("CVV");

        LinearLayout.LayoutParams layoutParamsForTextView = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsForLongEditText = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsForShortEditText = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsForSpinner = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsForLinearLayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        layoutParamsForLongEditText.setMargins(0, 0, 0, dpAsPixels);
        layoutParamsForShortEditText.setMargins(0, 0, 0, dpAsPixels);
        layoutParamsForLinearLayout.setMargins(0, 0, 0, dpAsPixels);
        layoutParamsForSpinner.setMargins(0, 0, dpAsPixels, 0);

        cardNumberTextView.setLayoutParams(layoutParamsForTextView);
        endDateTextView.setLayoutParams(layoutParamsForTextView);
        cvvTextView.setLayoutParams(layoutParamsForTextView);

        cardNumberEditText.setLayoutParams(layoutParamsForLongEditText);
        cvvEditText.setLayoutParams(layoutParamsForShortEditText);

        cardNumberEditText.setBackgroundResource(R.drawable.edit_text_border);
        cardNumberEditText.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        cardNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        cvvEditText.setBackgroundResource(R.drawable.edit_text_border);
        cvvEditText.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        cvvEditText.setEms(3);
        cvvEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvvEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(3);
        cvvEditText .setFilters(filters);


        ArrayAdapter monthsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months) {
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
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endMonthSpinner.setAdapter(monthsAdapter);
        endMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentMonth = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter  yearsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years) {
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
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endYearSpinner.setAdapter(yearsAdapter);
        endYearSpinner.setSelection(years.size() - 1);
        endYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentYear = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        endDateLinearLayout.setLayoutParams(layoutParamsForLinearLayout);
        endDateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        endMonthSpinnerLinearLayout.setLayoutParams(layoutParamsForSpinner);
        endMonthSpinnerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        endMonthSpinnerLinearLayout.setBackgroundResource(R.drawable.edit_text_border);
        endMonthSpinnerLinearLayout.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        endYearSpinnerLinearLayout.setLayoutParams(layoutParamsForSpinner);
        endYearSpinnerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        endYearSpinnerLinearLayout.setBackgroundResource(R.drawable.edit_text_border);
        endYearSpinnerLinearLayout.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        endMonthSpinnerLinearLayout.addView(endMonthSpinner);
        endYearSpinnerLinearLayout.addView(endYearSpinner);
        endDateLinearLayout.addView(endMonthSpinnerLinearLayout);
        endDateLinearLayout.addView(endYearSpinnerLinearLayout);

        mainLinearLayout.addView(cardNumberTextView);
        mainLinearLayout.addView(cardNumberEditText);
        mainLinearLayout.addView(endDateTextView);
        mainLinearLayout.addView(endDateLinearLayout);
        mainLinearLayout.addView(cvvTextView);
        mainLinearLayout.addView(cvvEditText);

        return mainLinearLayout;

    }

    public void setBancontactForm() {
        paymentWithVirtualWallet.setChecked(false);
        float scale = this.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        TextView messageTextView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, dpAsPixels);
        messageTextView.setLayoutParams(layoutParams);
        messageTextView.setText("Nous allons vous rediriger vers le terminal de votre banque pour finaliser le paiement.");
        messageTextView.setGravity(Gravity.CENTER);
        otherPaymentMethodDetailsLayout.addView(messageTextView);
    }

    public void setCreditCardChoice() {
        float scale = this.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);

        final ArrayList<String> creditCardAlias = new ArrayList<>();
        creditCardAlias.add("Nouvelle carte de crédit");
        for (int i = 0; i < userCreditCards.size(); i++) {
            String alias = userCreditCards.get(i).getAlias();
            creditCardAlias.add(alias);
        }


        TextView titleTextView = new TextView(this);
        final Spinner creditCarsAliasSpinner = new Spinner(this);
        LinearLayout creditCardAliasLinearLayout = new LinearLayout(this);
        final LinearLayout newCrediCardFormLinearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParamsForLinearLayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 0, 0, dpAsPixels);
        layoutParamsForLinearLayout.setMargins(0, 0, 0, dpAsPixels);

        titleTextView.setLayoutParams(layoutParams);
        titleTextView.setText("Sélectionnez une de vos cartes de crédit");

        ArrayAdapter creditCardAliasAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, creditCardAlias);
        creditCardAliasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        creditCarsAliasSpinner.setAdapter(creditCardAliasAdapter);
        creditCarsAliasSpinner.setSelection(1);
        creditCarsAliasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentAlias = adapterView.getItemAtPosition(i).toString();

                if (currentAlias.equals(creditCardAlias.get(0))) {
                    newCrediCardFormLinearLayout.addView(addNewCreditCard());
                } else {
                    newCrediCardFormLinearLayout.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        creditCardAliasLinearLayout.setLayoutParams(layoutParamsForLinearLayout);
        creditCardAliasLinearLayout.setOrientation(LinearLayout.VERTICAL);
        creditCardAliasLinearLayout.setBackgroundResource(R.drawable.edit_text_border);
        creditCardAliasLinearLayout.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        creditCardAliasLinearLayout.addView(creditCarsAliasSpinner);

        otherPaymentMethodDetailsLayout.addView(titleTextView);
        otherPaymentMethodDetailsLayout.addView(creditCardAliasLinearLayout);
        otherPaymentMethodDetailsLayout.addView(newCrediCardFormLinearLayout);

    }

    public void didTouchPaymentButton(View view) {
        if (paymentMode.equals("transfert")) {
            if (totalWallet < totalPrice) {
                Toast.makeText(this, R.string.total_wallet_insufficient_toast_message, Toast.LENGTH_SHORT).show();

            } else {
                payLesson();
            }

        } else if (paymentMode.equals("cd")) {
            if (!currentAlias.equals("Nouvelle carte de crédit")) {
                for (int i = 0; i < userCreditCards.size(); i++) {
                    if (userCreditCards.get(i).getAlias().equals(currentAlias)) {
                        cardId = userCreditCards.get(i).getCardId();
                    }
                }

                payLesson();

            } else  {
                String year = currentYear.substring(2);
                String cardNumber = cardNumberEditText.getText().toString();
                String expirationDate = currentMonth + year;
                String securityCode = cvvEditText.getText().toString();

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
                                payLesson();
                            }

                            @Override
                            public void failure(MangoException error) {
                                Toast.makeText(PaymentMethodActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }).start();

            }

        } else if (paymentMode.equals("bancontact")) {
            payLesson();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            paymentMode = "transfert";
        } else {
            paymentMode = "";
        }

    }

    public void payLesson() {
        if (paymentMode.equals("cd")) {
            call = service.payLessonWithCreditCard(teacherId, paymentMode, cardId, email, token);

        } else {
            call = service.payLesson(teacherId, paymentMode, email, token);
        }

        call.enqueue(new retrofit2.Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();

                if (message.equals("result")) {
                    String url = response.body().getUrl();
                    Intent intent = new Intent(getApplication(), MangoPaySecureModeActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("mode", paymentMode);
                    startActivity(intent);

                } else if (message.equals("finish")) {
                    Toast.makeText(getApplication(), R.string.payment_success_toast_message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplication(), MyLessonsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }
}
