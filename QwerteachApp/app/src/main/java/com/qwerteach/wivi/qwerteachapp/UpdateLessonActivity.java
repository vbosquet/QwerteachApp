package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.fragments.DatePickerFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.TimePickerFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLessonActivity extends AppCompatActivity implements View.OnClickListener {

    Lesson lesson;
    TextView topicGroupTextView, topicTextView, levelTextView, totalPriceTextView, lessonDurationTextView, dateTextView, timeTextView;
    Button datePickerButton, timePickerButton, saveLessonInfosButton;
    ProgressDialog progressDialog;
    QwerteachService service;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lesson);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lesson = (Lesson) getIntent().getSerializableExtra("lesson");
        }

        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);

        topicGroupTextView = (TextView) findViewById(R.id.lesson_topic_group_text_view);
        topicTextView = (TextView) findViewById(R.id.lesson_topic_text_view);
        levelTextView = (TextView) findViewById(R.id.lesson_level_text_view);
        totalPriceTextView = (TextView) findViewById(R.id.total_price_text_view);
        lessonDurationTextView = (TextView) findViewById(R.id.lesson_duration_text_view);
        dateTextView = (TextView) findViewById(R.id.date_picker_text_view);
        timeTextView = (TextView) findViewById(R.id.time_picker_text_view);
        datePickerButton = (Button) findViewById(R.id.date_picker_button);
        timePickerButton = (Button) findViewById(R.id.time_picker_button);
        saveLessonInfosButton = (Button) findViewById(R.id.save_lesson_infos_button);

        dateTextView.setText(lesson.getDate(lesson.getTimeStart()));
        timeTextView.setText(lesson.getTime(lesson.getTimeStart()));
        topicTextView.setText(lesson.getTopicTitle());
        topicGroupTextView.setText(lesson.getTopicGroupTitle());
        levelTextView.setText(lesson.getLevel());
        totalPriceTextView.setText(lesson.getPrice() + " €");
        lessonDurationTextView.setText(lesson.getDuration());

        datePickerButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);
        saveLessonInfosButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_picker_button:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(this.getFragmentManager(), "timePicker");
                break;
            case R.id.date_picker_button:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(this.getFragmentManager(), "datePicker");
                break;
            case R.id.save_lesson_infos_button:
                didTouchUpdateLessonButton();
                break;
        }
    }

    public void didTouchUpdateLessonButton() {
        int lessonId = lesson.getLessonId();
        String newDate = dateTextView.getText().toString();
        String newTime = timeTextView.getText().toString();
        String timeStart = newDate + " " + newTime;

        Lesson lesson = new Lesson();
        lesson.setTimeStart(timeStart);
        Map<String, Lesson> requestbody = new HashMap<>();
        requestbody.put("lesson", lesson);

        startProgressDialog();
        Call<JsonResponse> call = service.updateLesson(lessonId, requestbody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();
                String message = response.body().getMessage();

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                if (success.equals("true")) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
