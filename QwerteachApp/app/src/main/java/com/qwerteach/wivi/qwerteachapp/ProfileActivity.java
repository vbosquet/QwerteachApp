package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements DisplayInfosProfileAsyncTask.IDisplayInfosProfile {

    Intent intent;
    String userId, email, token;
    TextView firstNameAndLastNameTextView, ageTextView, occupationTextView, descriptionTextView;
    Button contactUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firstNameAndLastNameTextView = (TextView) findViewById(R.id.firstname_and_lastanme_text_view);
        ageTextView = (TextView) findViewById(R.id.age_text_view);
        occupationTextView = (TextView) findViewById(R.id.occupation_text_view);
        descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        contactUserButton = (Button) findViewById(R.id.contact_button);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        displayInfosProfileAsyncTask.execute(userId, email, token);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profil_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
            case R.id.edit_profile_button:
                intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                String age = jsonObject.getString("age");
                String occupation = jsonData.getString("occupation");
                String description = jsonData.getString("description");

                firstNameAndLastNameTextView.setText(firstName + " " + lastName);
                ageTextView.setText(age + " ans");
                occupationTextView.setText(occupation);
                descriptionTextView.setText(description);
                contactUserButton.setText("Contacter " + firstName);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void didTouchContactButton(View view) {
    }
}
