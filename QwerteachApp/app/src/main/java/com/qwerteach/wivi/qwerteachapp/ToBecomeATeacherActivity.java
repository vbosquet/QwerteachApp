package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.AsyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.AsyncTasks.SaveInfosProfileAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ToBecomeATeacherActivity extends AppCompatActivity implements SaveInfosProfileAsyncTask.ISaveInfosProfile,
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile {

    Intent intent;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText userDescriptionEditTet;
    EditText birthDateEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_become_a_teacher);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firstNameEditText = (EditText) findViewById(R.id.firstname);
        lastNameEditText = (EditText) findViewById(R.id.lastname);
        birthDateEditText = (EditText) findViewById(R.id.birthdate);
        userDescriptionEditTet = (EditText) findViewById(R.id.description);
        emailEditText = (EditText) findViewById(R.id.email);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);

        startDisplayInfosProfileAsynTack();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.to_become_a_teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void didTouchSaveInfosProfile(View view) {
        startSaveInfosProfileTabAsyncTask(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                birthDateEditText.getText().toString(), userDescriptionEditTet.getText().toString(),
                emailEditText.getText().toString(), phoneNumberEditText.getText().toString());
    }

    @Override
    public void displayConfirmationRegistrationInfosProfile(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String regsitrationConfirmation = jsonObject.getString("success");

            if (regsitrationConfirmation.equals("true")) {
                Intent intent = new Intent(this, CreateSmallAdActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.error_save_infos_profile_toast_message, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startSaveInfosProfileTabAsyncTask(String firstName, String lastName, String birthDate, String userDescription, String email, String phoneNumber) {

        SaveInfosProfileAsyncTask saveInfosProfileAsyncTask = new SaveInfosProfileAsyncTask(this);
        saveInfosProfileAsyncTask.execute(firstName, lastName, birthDate, userDescription, userId, email, phoneNumber);
    }

    public void startDisplayInfosProfileAsynTack() {

        DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        displayInfosProfileAsyncTask.execute(userId);
    }

    @Override
    public void displayUserInfosProfile(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String getUserInfosProfile = jsonObject.getString("success");

            if (getUserInfosProfile.equals("true")) {
                JSONObject jsonData = jsonObject.getJSONObject("user");
                String firstName = jsonData.getString("firstname");
                String lastName = jsonData.getString("lastname");
                String birthDate = jsonData.getString("birthdate");
                String description = jsonData.getString("description");
                String email = jsonData.getString("email");
                String phonenumber = jsonData.getString("phonenumber");

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                birthDateEditText.setText(birthDate);
                userDescriptionEditTet.setText(description);
                emailEditText.setText(email);
                phoneNumberEditText.setText(phonenumber);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
