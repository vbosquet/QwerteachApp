package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailSignUpActivity extends AppCompatActivity  {

    EditText email, password;
    LinearLayout emailLinearLayout, passwordLinearLayout;
    TextView emailValidation, passwordValidation;
    Button emailSignUpButton;
    Menu myMenu;
    QwerteachService service;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.email_sign_up);
        password = (EditText) findViewById(R.id.password_sign_up);
        emailValidation = (TextView) findViewById(R.id.email_validation_text_view);
        emailLinearLayout = (LinearLayout) findViewById(R.id.email_linear_layout);
        passwordLinearLayout = (LinearLayout) findViewById(R.id.password_linear_layout);
        passwordValidation = (TextView) findViewById(R.id.password_validation_text_view);
        emailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!passwordValidator(password.getText().toString()) && !password.getText().toString().equals("")) {
                    passwordValidation.setVisibility(View.VISIBLE);
                    passwordLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailValidation.setVisibility(View.GONE);
                emailLinearLayout.setBackgroundDrawable(getDrawable(R.drawable.grey_edit_text_border));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!emailValidator(email.getText().toString()) && !email.getText().toString().equals("")) {
                    emailValidation.setVisibility(View.VISIBLE);
                    emailLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordValidation.setVisibility(View.GONE);
                passwordLinearLayout.setBackgroundDrawable(getDrawable(R.drawable.grey_edit_text_border));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signUpWithEmail() {
        startProgressDialog();
        Map<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString());
        data.put("password", password.getText().toString());

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

                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.registration_success_toast, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        break;
                    case "exist":
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.email_already_in_use_message, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.registration_error_message, Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                if(t instanceof SocketTimeoutException){;
                    Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void didTouchSignUpButton(View view) {
        Boolean connected = isOnline();
        if (!connected) {
            Toast.makeText(this, R.string.sign_in_connection_problem_message, Toast.LENGTH_LONG).show();
        } else {
            if (emailValidator(email.getText().toString()) && passwordValidator(password.getText().toString())) {
                signUpWithEmail();
            } else {
                if (!emailValidator(email.getText().toString()) && !passwordValidator(password.getText().toString())) {
                    emailValidation.setVisibility(View.VISIBLE);
                    emailLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                    passwordValidation.setVisibility(View.VISIBLE);
                    passwordLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                } else if (!emailValidator(email.getText().toString())) {
                    emailValidation.setVisibility(View.VISIBLE);
                    emailLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                } else if (!passwordValidator(password.getText().toString())) {
                    passwordValidation.setVisibility(View.VISIBLE);
                    passwordLinearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        }
    }
}
