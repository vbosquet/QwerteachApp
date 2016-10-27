package com.qwerteach.wivi.qwerteachapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.AsyncTasks.EmailSignUpAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignUpActivity extends AppCompatActivity implements EmailSignUpAsyncTask.IEmailSignUp {

    EditText email;
    EditText password;
    EditText passwordConfirmation;
    Menu myMenu;

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

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        passwordConfirmation = (EditText) findViewById(R.id.passwordConfirmation);

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
                            TextView passwordConfirmationProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) passwordConfirmationProblemMessage.getLayoutParams();
                            params.setMargins(0, 15, 0, 15);
                            passwordConfirmationProblemMessage.setLayoutParams(params);
                            passwordConfirmationProblemMessage.setText(R.string.password_confirmation_problem_message);
                        } else {
                            EmailSignUpAsyncTask emailSignUpAsyncTask = new EmailSignUpAsyncTask(this);
                            emailSignUpAsyncTask.execute(email.getText().toString(), password.getText().toString(), passwordConfirmation.getText().toString());
                        }

                    } else {
                        TextView emailValidationProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) emailValidationProblemMessage.getLayoutParams();
                        params.setMargins(0, 15, 0, 15);
                        emailValidationProblemMessage.setLayoutParams(params);
                        emailValidationProblemMessage.setText(R.string.message_alert_email_validation);
                    }

                } else {
                    TextView connectionProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) connectionProblemMessage.getLayoutParams();
                    params.setMargins(0, 15, 0, 15);
                    connectionProblemMessage.setLayoutParams(params);
                    connectionProblemMessage.setText(R.string.sign_up_connection_problem_message);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void didTouchFacebookSignUpButton(View view) {
    }

    public void didTouchGoogleSignUpButton(View view) {
    }

    @Override
    public void displayConfirmationRegistrationMessage(String string) {

        try {

            JSONObject jsonObject = new JSONObject(string);
            String registrationConfirmation = jsonObject.getString("success");

            if (registrationConfirmation.equals("true")) {
                String userId = jsonObject.getString("id");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userId", userId);
                editor.apply();

                Toast.makeText(this, R.string.registration_success_toast, Toast.LENGTH_SHORT).show();

            } else if (registrationConfirmation.equals("exist")) {
                TextView connectionProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) connectionProblemMessage.getLayoutParams();
                params.setMargins(0, 15, 0, 15);
                connectionProblemMessage.setLayoutParams(params);
                connectionProblemMessage.setText(R.string.email_already_in_use_message);

            } else {
                TextView connectionProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) connectionProblemMessage.getLayoutParams();
                params.setMargins(0, 15, 0, 15);
                connectionProblemMessage.setLayoutParams(params);
                connectionProblemMessage.setText(R.string.registration_error_message);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    public boolean emailValidator(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
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
