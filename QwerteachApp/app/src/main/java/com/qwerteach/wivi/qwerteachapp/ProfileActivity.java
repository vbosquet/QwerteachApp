package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.fragments.StudentProfileFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.TeacherProfileFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity  {

    Intent intent;
    ProgressDialog progressDialog;
    User currentUser;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        gson = new Gson();

        String json = preferences.getString("user", "");
        currentUser = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(this);
        getUserInfos();
    }

    public void getUserInfos() {
        startProgressDialog();
        QwerteachService service = ApiClient.getClient().create(QwerteachService.class);
        Call<JsonResponse> call = service.getUserInfos(currentUser.getUserId(), currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    currentUser = response.body().getUser();

                    String json = gson.toJson(currentUser);
                    editor.putString("user", json);
                    editor.apply();

                    if (!currentUser.getPostulanceAccepted()) {
                        progressDialog.dismiss();
                        displayStudentProfileFragment();

                    } else {
                        progressDialog.dismiss();
                        displayTeacherProfileFragment();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
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
                intent.putExtra("user", currentUser);

                if (currentUser.getPostulanceAccepted()) {
                    intent.putExtra("teacher", currentUser);
                }

                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void displayTeacherProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("teacher", currentUser);
        TeacherProfileFragment teacherProfileFragment = new TeacherProfileFragment();
        teacherProfileFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, teacherProfileFragment);
        transaction.commit();
    }

    public void displayStudentProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("student", currentUser);
        StudentProfileFragment studentProfileFragment = new StudentProfileFragment();
        studentProfileFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, studentProfileFragment);
        transaction.commit();
    }
}
