package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Payment;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowLessonActivity extends AppCompatActivity {

    Lesson lesson;
    TextView lessonDate, lessonOtherUser, lessonDuration, lessonPrice, userTitle, paymentPrice, paymentStatus, paymentId;
    User user;
    List<Payment> payments;
    ProgressDialog progressDialog;
    QwerteachService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lesson);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lesson = (Lesson) getIntent().getSerializableExtra("lesson");
            payments = lesson.getPayments();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cours de " + lesson.getTopicTitle());

        lessonDate = (TextView) findViewById(R.id.lesson_time_start);
        lessonOtherUser = (TextView) findViewById(R.id.lesson_other_user);
        lessonDuration = (TextView) findViewById(R.id.lesson_duration);
        lessonPrice = (TextView) findViewById(R.id.lesson_price);
        userTitle = (TextView) findViewById(R.id.user_title);
        paymentPrice = (TextView) findViewById(R.id.payment_price);
        paymentStatus = (TextView) findViewById(R.id.payment_status);
        paymentId = (TextView) findViewById(R.id.payment_id);

        if (user.getPostulanceAccepted()) {
            userTitle.setText("Elève");
        } else {
            userTitle.setText("Professeur");
        }

        lessonDate.setText(lesson.getDate() + " à " + lesson.getTime());
        lessonOtherUser.setText(lesson.getUserName());
        lessonDuration.setText(lesson.calculateLessonDuration());
        lessonPrice.setText(lesson.getPrice() + "€");

        for (int i = 0; i < payments.size(); i++) {
            paymentPrice.setText(payments.get(i).getPrice() + "€");
            paymentStatus.setText(payments.get(i).getStatus());
            paymentId.setText("#" + payments.get(i).getPaymentId());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_lesson_menu, menu);
        if ((lesson.getStatus().equals("pending_teacher") && user.getPostulanceAccepted()) ||
                (lesson.getStatus().equals("pending_student") && !user.getPostulanceAccepted())) {
            MenuItem lessonAccept = menu.findItem(R.id.lesson_accept);
            MenuItem lessonRefuse = menu.findItem(R.id.lesson_refuse);
            MenuItem lessonUpdate = menu.findItem(R.id.lesson_update);
            lessonAccept.setVisible(true);
            lessonRefuse.setVisible(true);
            lessonUpdate.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.lesson_accept:
                didTouchAcceptLessonButton();
                return true;
            case R.id.lesson_refuse:
                didTouchRefuseLessonButton();
                return true;
            case R.id.lesson_update:
                didTouchUpdateLessonButton();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void didTouchAcceptLessonButton() {
        startProgressDialog();
        Call<JsonResponse> call = service.acceptLesson(lesson.getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if (response.body().getSuccess().equals("true")) {
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

    public void didTouchRefuseLessonButton() {
        startProgressDialog();
        Call<JsonResponse> call = service.refuseLesson(lesson.getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if (response.body().getSuccess().equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), MyLessonsActivity.class);
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

    public void didTouchUpdateLessonButton() {
        Intent intent = new Intent(getApplicationContext(), UpdateLessonActivity.class);
        intent.putExtra("lesson", lesson);
        startActivityForResult(intent, 10003);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10003) && (resultCode == Activity.RESULT_OK)) {
            finish();
            Intent intent = new Intent(this, MyLessonsActivity.class);
            intent.putExtra("position", 1);
            startActivity(intent);
        }
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
