package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 15/12/16.
 */

public class UpdateLessonFragment extends Fragment implements View.OnClickListener {

    View view;
    Lesson lesson;
    TextView topicGroupTextView, topicTextView, levelTextView, totalPriceTextView, lessonDurationTextView, dateTextView, timeTextView;
    Button datePickerButton, timePickerButton, saveLessonInfosButton;
    String email, token;
    ProgressDialog progressDialog;
    QwerteachService service;

    public static UpdateLessonFragment newInstance(Lesson lesson) {
        UpdateLessonFragment updateLessonFragment = new UpdateLessonFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("lesson", lesson);
        updateLessonFragment.setArguments(bundle);
        return updateLessonFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        if (getArguments() != null) {
            lesson = (Lesson) getArguments().getSerializable("lesson");
        }

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_lesson, container, false);
        topicGroupTextView = (TextView) view.findViewById(R.id.lesson_topic_group_text_view);
        topicTextView = (TextView) view.findViewById(R.id.lesson_topic_text_view);
        levelTextView = (TextView) view.findViewById(R.id.lesson_level_text_view);
        totalPriceTextView = (TextView) view.findViewById(R.id.total_price_text_view);
        lessonDurationTextView = (TextView) view.findViewById(R.id.lesson_duration_text_view);
        dateTextView = (TextView) view.findViewById(R.id.date_picker_text_view);
        timeTextView = (TextView) view.findViewById(R.id.time_picker_text_view);
        datePickerButton = (Button) view.findViewById(R.id.date_picker_button);
        timePickerButton = (Button) view.findViewById(R.id.time_picker_button);
        saveLessonInfosButton = (Button) view.findViewById(R.id.save_lesson_infos_button);

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

        return  view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_picker_button:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getActivity().getFragmentManager(), "timePicker");
                break;
            case R.id.date_picker_button:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getActivity().getFragmentManager(), "datePicker");
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
        Call<JsonResponse> call = service.updateLesson(lessonId, requestbody, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();
                String message = response.body().getMessage();

                progressDialog.dismiss();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                if (success.equals("true")) {
                    getActivity().getSupportFragmentManager().popBackStack();
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
}
