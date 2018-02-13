package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherReviewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    User user;
    ProgressDialog progressDialog;
    QwerteachService service;
    int teacherId;
    Spinner notes;
    String note;
    EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_review);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            teacherId = (int) getIntent().getSerializableExtra("teacherId");
        }

        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);

        comment = (EditText) findViewById(R.id.comment_edit_text);
        notes = (Spinner) findViewById(R.id.note_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.note_spinner_item, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        notes.setAdapter(adapter);
        notes.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, DashboardActivity.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        note = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void didTouchValidateButton(View view) {
        Review review = new Review();
        review.setNote(Integer.parseInt(note));
        review.setReviewText(comment.getText().toString());

        Map<String, Review> body = new HashMap<>();
        body.put("review", review);

        startProgressDialog();
        Call<JsonResponse> call = service.letReviewToTeacher(teacherId, body, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String success = response.body().getSuccess();
                    String message = response.body().getMessage();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
