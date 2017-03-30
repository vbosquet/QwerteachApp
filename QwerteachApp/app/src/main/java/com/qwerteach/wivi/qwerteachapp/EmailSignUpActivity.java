package com.qwerteach.wivi.qwerteachapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailSignUpActivity extends AppCompatActivity  {

    EditText email, password, passwordConfirmation;
    Menu myMenu;
    QwerteachService service;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkFieldForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.email_sign_up);
        password = (EditText) findViewById(R.id.password_sign_up);
        passwordConfirmation = (EditText) findViewById(R.id.password_confirmation);

        service = ApiClient.getClient().create(QwerteachService.class);

        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        passwordConfirmation.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.email_sign_up_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        myMenu = menu;
        myMenu.findItem(R.id.sign_up_button).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_up_button:
                Boolean connected = isOnline();

                if (connected) {
                    Boolean emailValidated = emailValidator(email.getText().toString());
                    if (emailValidated) {
                        if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
                            displayToastMessage(R.string.password_confirmation_problem_message);
                        } else {
                            boolean passwordValidated = passwordValidator(password.getText().toString());
                            if (passwordValidated) {
                                signUpWithEmail();
                            } else {
                                displayToastMessage(R.string.password_validation_message);
                            }
                        }

                    } else {
                        displayToastMessage(R.string.message_alert_email_validation);
                    }

                } else {
                    displayToastMessage(R.string.sign_up_connection_problem_message);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayToastMessage(int idString) {
        TextView passwordConfirmationProblemMessage = (TextView) findViewById(R.id.problem_message_sign_up_textview);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) passwordConfirmationProblemMessage.getLayoutParams();
        params.setMargins(0, 15, 0, 15);
        passwordConfirmationProblemMessage.setLayoutParams(params);
        passwordConfirmationProblemMessage.setText(idString);

    }

    public void signUpWithEmail() {
        Map<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString());
        data.put("password", password.getText().toString());
        data.put("password_confirmation", passwordConfirmation.getText().toString());

        Map<String, HashMap<String, String>> registration = new HashMap<>();
        registration.put("user", (HashMap<String, String>) data);

        Call<JsonResponse> call = service.signUpWithEmail(registration);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();
                switch (success) {
                    case "true":
                        User user = response.body().getUser();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        editor.putString("user", json);
                        editor.putBoolean("isLogin", true);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), R.string.registration_success_toast, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        break;
                    case "exist":
                        displayToastMessage(R.string.email_already_in_use_message);
                        break;
                    default:
                        displayToastMessage(R.string.registration_error_message);
                        break;
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchFacebookSignUpButton(View view) {
    }

    public void didTouchGoogleSignUpButton(View view) {

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    public boolean emailValidator(String email) {
        String emailPattern = "\\A[^@\\s]+@[^@\\s]+\\z";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean passwordValidator(String password) {
        String passwordPattern = "(.{8,128})";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void checkFieldForEmptyValues() {
        String emailField = email.getText().toString();
        String passwordField = password.getText().toString();
        String passwordConfirmationField = passwordConfirmation.getText().toString();

        if (emailField.trim().length() > 0 && passwordField.trim().length() > 0 && passwordConfirmationField.trim().length() > 0) {
            myMenu.findItem(R.id.sign_up_button).setEnabled(true);

        } else {
            myMenu.findItem(R.id.sign_up_button).setEnabled(false);
        }
    }

}
