package com.qwerteach.wivi.qwerteachapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.ShowProfileInfosAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ToBecomeATeacherActivity extends AppCompatActivity implements ShowProfileInfosAsyncTask.IShowProfileInfos {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText userDescriptionEditTet;
    EditText birthDateEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    String userId, email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_become_a_teacher);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        firstNameEditText = (EditText) findViewById(R.id.firstname);
        lastNameEditText = (EditText) findViewById(R.id.lastname);
        birthDateEditText = (EditText) findViewById(R.id.birthdate);
        userDescriptionEditTet = (EditText) findViewById(R.id.description);
        emailEditText = (EditText) findViewById(R.id.email);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);

        ShowProfileInfosAsyncTask showProfileInfosAsyncTask = new ShowProfileInfosAsyncTask(this);
        showProfileInfosAsyncTask.execute(userId, email, token);
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
            case R.id.cancel_button:
               finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void didTouchSaveInfosProfile(View view) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String description = userDescriptionEditTet.getText().toString();
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        //TODO
    }

    @Override
    public void showProfileInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");

            String firstName = userJson.getString("firstname");
            String lastName = userJson.getString("lastname");
            String birthDate = userJson.getString("birthdate");
            String description = userJson.getString("description");
            String email = userJson.getString("email");
            String phonenumber = userJson.getString("phonenumber");

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            birthDateEditText.setText(birthDate);
            userDescriptionEditTet.setText(description);
            emailEditText.setText(email);
            phoneNumberEditText.setText(phonenumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
