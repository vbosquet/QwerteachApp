package com.qwerteach.wivi.qwerteachapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToBecomeATeacherActivity extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, userDescriptionEditTet, birthDateEditText, emailEditText, phoneNumberEditText;
    QwerteachService service;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_become_a_teacher);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        firstNameEditText = (EditText) findViewById(R.id.firstname);
        lastNameEditText = (EditText) findViewById(R.id.lastname);
        birthDateEditText = (EditText) findViewById(R.id.birthdate);
        userDescriptionEditTet = (EditText) findViewById(R.id.description);
        emailEditText = (EditText) findViewById(R.id.email);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);

        service = ApiClient.getClient().create(QwerteachService.class);
        getUserInfos();
    }

    public void getUserInfos() {
        Call<JsonResponse> call = service.getUserInfos(user.getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                user = response.body().getUser();

                firstNameEditText.setText(user.getFirstName());
                lastNameEditText.setText(user.getLastName());
                birthDateEditText.setText(user.getBirthdate());
                userDescriptionEditTet.setText(Html.fromHtml(user.getDescription()), TextView.BufferType.SPANNABLE);
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhoneNumber());
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

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
}
