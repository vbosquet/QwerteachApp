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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.SignInActivityAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements SignInActivityAsyncTask.ISignIn{

    EditText email;
    EditText password;
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
        setContentView(R.layout.activity_sign_in);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

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
                    SignInActivityAsyncTask signInActivityAsyncTask = new SignInActivityAsyncTask(this);
                    signInActivityAsyncTask.execute(email.getText().toString(), password.getText().toString());

                } else {
                    TextView connectionProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) connectionProblemMessage.getLayoutParams();
                    params.setMargins(0, 15, 0, 15);
                    connectionProblemMessage.setLayoutParams(params);
                    connectionProblemMessage.setText(R.string.sign_in_connection_problem_message);

                }

                return  true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void displayConfirmationConnectionMessage(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String loginConfirmation = jsonObject.getString("success");

            if (loginConfirmation.equals("false")) {
                TextView connectionProblemMessage = (TextView) findViewById(R.id.problem_message_textview);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) connectionProblemMessage.getLayoutParams();
                params.setMargins(0, 15, 0, 15);
                connectionProblemMessage.setLayoutParams(params);
                connectionProblemMessage.setText(R.string.login_error_message);

            } else {
                JSONObject jsonData = jsonObject.getJSONObject("data");
                JSONObject jsonUser = jsonData.getJSONObject("user");
                String userId = jsonUser.getString("id");
                String email = jsonUser.getString("email");
                String token = jsonUser.getString("authentication_token");
                String firstName = jsonUser.getString("firstname");
                String lastName = jsonUser.getString("lastname");
                boolean isTeacher = jsonUser.getBoolean("postulance_accepted");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userId", userId);
                editor.putString("email", email);
                editor.putString("token", token);
                editor.putString("firstName", firstName);
                editor.putString("lastName", lastName);
                editor.putBoolean("isTeacher", isTeacher);
                editor.putBoolean("isLogin", true);
                editor.apply();

                Toast.makeText(this, R.string.connection_success_toast, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
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
}
