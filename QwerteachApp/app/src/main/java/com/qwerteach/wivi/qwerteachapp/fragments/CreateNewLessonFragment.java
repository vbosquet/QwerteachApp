package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.PaymentMethodActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.LevelAdapter;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.TopicAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroupAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 13/12/16.
 */

public class CreateNewLessonFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    View view;
    TextView timeTextView, dateTextView, totalPriceTextView;
    Spinner hourSpinner, minuteSpinner, topicGroupSpinner, topicSpinner, levelSpinner;
    Button datePickerButton, timePickerButton, createNewLessonButton;
    ArrayList<SmallAd> smallAds;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    ArrayList<TopicGroup> topicGroups;
    ArrayList<SmallAdPrice> smallAdPrices;
    String hour = "00", minute = "00";
    Teacher teacher;
    Double totalPrice;
    Bundle savedState;
    ProgressDialog progressDialog;
    QwerteachService service;
    int currentTopicGroupId, currentTopicId, currentLevelId;
    int currentTopicGroupPosition, currentTopicPosition, currentLevelPosition;
    Call<JsonResponse> call;
    User user;


    public static CreateNewLessonFragment newInstance() {
        CreateNewLessonFragment createNewLessonFragment = new CreateNewLessonFragment();
        return createNewLessonFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
        }

        smallAds = teacher.getSmallAds();
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_new_lesson, container, false);

        timeTextView = (TextView) view.findViewById(R.id.time_picker_text_view);
        dateTextView = (TextView) view.findViewById(R.id.date_picker_text_view);
        hourSpinner = (Spinner) view.findViewById(R.id.hour_spinner);
        minuteSpinner = (Spinner) view.findViewById(R.id.minut_spinner);
        topicGroupSpinner = (Spinner) view.findViewById(R.id.topic_group_spinner);
        topicSpinner = (Spinner) view.findViewById(R.id.topic_spinner);
        levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);
        totalPriceTextView = (TextView) view.findViewById(R.id.total_price_text_view);
        datePickerButton = (Button) view.findViewById(R.id.date_picker_button);
        timePickerButton = (Button) view.findViewById(R.id.time_picker_button);
        createNewLessonButton = (Button) view.findViewById(R.id.create_new_lesson_button);

        createNewLessonButton.setOnClickListener(this);
        totalPriceTextView.setText("0 €");
        smallAdPrices = new ArrayList<>();

        startProgressDialog();
        call = service.getTeacherTopicGroups(teacher.getUser().getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                topicGroups = response.body().getTopicGroups();

                displayDateAndTimePickers();
                displayHourSpinner();
                displayMinutSpinner();
                displayTopicGroupSpinner();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });


        return  view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void displayDateAndTimePickers() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat tf = new SimpleDateFormat("HH:mm");
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        tf.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        String currentTime = tf.format(currentLocalTime);
        String currentDate = df.format(currentLocalTime);

        if (savedState!= null) {
            dateTextView.setText(savedState.getString("date"));
            timeTextView.setText(savedState.getString("time"));

        } else {
            dateTextView.setText(currentDate);
            timeTextView.setText(currentTime);

        }

        datePickerButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);

    }

    public void displayTopicGroupSpinner() {
        TopicGroupAdapter topicGroupAdapter = new TopicGroupAdapter(getContext(), android.R.layout.simple_spinner_item, topicGroups);
        topicGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicGroupSpinner.setAdapter(topicGroupAdapter);
        topicGroupSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            topicGroupSpinner.setSelection(savedState.getInt("topicGroup"));
        }

    }

    public void displayHourSpinner() {
        ArrayAdapter hourSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hour_spinner_items, android.R.layout.simple_spinner_item);
        hourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourSpinnerAdapter);
        hourSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            int hourPosition = getIndexByString(hourSpinner, savedState.getString("hour"));
            hourSpinner.setSelection(hourPosition);
        }

    }

    public void displayMinutSpinner() {
        ArrayAdapter minutSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.minut_spinner_items, android.R.layout.simple_spinner_item);
        minutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minutSpinnerAdapter);
        minuteSpinner.setOnItemSelectedListener(this);

        if(savedState != null) {
            int minutePosition = getIndexByString(minuteSpinner, savedState.getString("minute"));
            minuteSpinner.setSelection(minutePosition);
        }

    }

    public void displayTopicSpinner() {
        TopicAdapter topicAdapter = new TopicAdapter(getContext(), android.R.layout.simple_spinner_item, topics);
        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            topicSpinner.setSelection(savedState.getInt("topic"));
        }

    }

    public void displayLevelSpinner() {
        LevelAdapter levelAdapter = new LevelAdapter(getContext(), android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);
        levelSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            levelSpinner.setSelection(savedState.getInt("level"));

        }
    }

    private int getIndexByString(Spinner spinner, String string) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {
            case R.id.hour_spinner:
                hour = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minute);
                break;
            case R.id.minut_spinner:
                minute = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minute);
                break;
            case R.id.topic_group_spinner:
                currentTopicGroupPosition = i;
                currentTopicGroupId = topicGroups.get(i).getTopicGroupId();

                Call<JsonResponse> callForTopics  = service.getTeacherTopics(teacher.getUser().getUserId(), currentTopicGroupId,
                        user.getEmail(), user.getToken());
                callForTopics.enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        topics = response.body().getTopics();
                        displayTopicSpinner();
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {

                    }
                });
                break;
            case R.id.topic_spinner:
                currentTopicPosition = i;
                currentTopicId = topics.get(i).getTopicId();

                for (int j = 0; j < smallAds.size(); j++) {
                    if (smallAds.get(j).getTopicId() == currentTopicId) {
                        smallAdPrices = smallAds.get(j).getSmallAdPrices();
                    }
                }

                Call<JsonResponse> callForLevels = service.getTeacherLevels(teacher.getUser().getUserId(), currentTopicId, user.getEmail(), user.getToken());
                callForLevels.enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        levels = response.body().getLevels();
                        displayLevelSpinner();
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {

                    }
                });
                break;
            case R.id.level_spinner:
                currentLevelPosition = i;
                currentLevelId = levels.get(i).getLevelId();
                setTotalPrice(hour, minute);
                break;
        }

    }

    public void setTotalPrice(String hourString, String minuteString) {
        Integer newMinuteInt = 0;
        totalPrice = 0.0;

        if (!minute.equals("00")) {
            Double minuteDouble = Double.parseDouble(minuteString);
            Double newMinuteDouble = 100 / (60 / minuteDouble);
            newMinuteInt = newMinuteDouble.intValue();
        }

        String totalDurationString = hourString + "." + newMinuteInt;
        Double totalDurationDouble = Double.parseDouble(totalDurationString.toString());

        for (int i = 0; i < smallAdPrices.size(); i++) {
            if (smallAdPrices.get(i).getLevelId() == currentLevelId) {
                totalPrice = totalDurationDouble * smallAdPrices.get(i).getPrice();
            }
        }

        totalPriceTextView.setText(totalPrice + " €");

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            case R.id.create_new_lesson_button:
                didTouchCreateNewLessonButton();
                break;
        }
    }

    public void didTouchCreateNewLessonButton() {
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        String timeStart = date + " " + time;

        Lesson request = new Lesson();
        request.setLevelId(currentLevelId);
        request.setTopicId(currentTopicId);
        request.setTimeStart(timeStart);
        request.setHours(hour);
        request.setMinutes(minute);

        Lesson lesson = new Lesson();
        lesson.setTeacherId(teacher.getUser().getUserId());

        Map<String, Lesson> requestBody = new HashMap<>();
        requestBody.put("request", request);
        requestBody.put("lesson", lesson);

        startProgressDialog();
        Call<JsonResponse> call = service.createNewLesson(teacher.getUser().getUserId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();

                if (message.equals("no account")) {
                    progressDialog.dismiss();
                    displayCreateVirtualWalletFragment();

                } else if (message.equals("true")) {
                    ArrayList<UserCreditCard> creditCards = response.body().getUserCreditCards();
                    CardRegistrationData cardRegistrationData = response.body().getCardRegistrationData();

                    progressDialog.dismiss();
                    Intent intent = new Intent(getContext(), PaymentMethodActivity.class);
                    intent.putExtra("totalPrice", totalPrice);
                    intent.putExtra("teacher", teacher);
                    intent.putExtra("userCreditCardList", creditCards);
                    intent.putExtra("cardRegistration", cardRegistrationData);
                    startActivity(intent);
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

    public void displayCreateVirtualWalletFragment() {
        Fragment newFragment = CreateVirtualWalletFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            savedState = savedInstanceState;
            getActivity().setTitle(savedInstanceState.getString("activityTitle"));
        }
    }

    public void saveData(Bundle outState) {
        outState.putString("date", dateTextView.getText().toString());
        outState.putString("time", timeTextView.getText().toString());
        outState.putString("hour", hour);
        outState.putString("minute", minute);
        outState.putInt("topicGroup", currentTopicGroupPosition);
        outState.putInt("topic", currentTopicPosition);
        outState.putInt("level", currentLevelPosition);
        outState.putDouble("totalPrice", totalPrice);
        outState.putString("activityTitle", getActivity().getTitle().toString());
    }

}
