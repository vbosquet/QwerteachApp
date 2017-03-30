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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    EditText email, password;
    TextView connectionProblemMessage;
    Menu myMenu;
    QwerteachService service;
    ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_sign_in);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.email_sign_in);
        password = (EditText) findViewById(R.id.password_sign_in);
        connectionProblemMessage = (TextView) findViewById(R.id.problem_message_sign_in_textview);

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_in_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        myMenu = menu;
        myMenu.findItem(R.id.sign_in_button).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_in_button:
                Boolean connected = isOnline();

                if (connected) {
                    signIn();
                } else {
                    connectionProblemMessage.setText(R.string.sign_in_connection_problem_message);
                }

                return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signIn() {
        startProgressDialog();
        Map<String, String> data = new HashMap<>();
        data.put("email", email.getText().toString());
        data.put("password", password.getText().toString());

        Map<String, HashMap<String, String>> session = new HashMap<>();
        session.put("user", (HashMap<String, String>) data);

        Call<JsonResponse> call = service.signIn(session);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();

                if (success.equals("false")) {
                    progressDialog.dismiss();
                    connectionProblemMessage.setText(R.string.login_error_message);
                } else {
                    User user = response.body().getUser();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    editor.putString("user", json);
                    editor.putBoolean("isLogin", true);
                    editor.apply();

                    progressDialog.dismiss();
                    Toast.makeText(getApplication(), R.string.connection_success_toast, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchFacebookSignInButton(View view) {
    }

    public void didTouchGoogleSignInButton(View view) {
    }

    public void didTouchTextView(View view) {
    }

    public void checkFieldForEmptyValues() {
        String emailField = email.getText().toString();
        String passwordField = password.getText().toString();

        if (emailField.trim().length() > 0 && passwordField.trim().length() > 0) {
            myMenu.findItem(R.id.sign_in_button).setEnabled(true);
        } else {
            myMenu.findItem(R.id.sign_in_button).setEnabled(false);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
