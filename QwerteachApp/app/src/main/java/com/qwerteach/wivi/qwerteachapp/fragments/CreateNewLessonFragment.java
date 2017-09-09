package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.NewVirtualWalletActivity;
import com.qwerteach.wivi.qwerteachapp.PaymentMethodActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.common.Common;
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
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 13/12/16.
 */

public class CreateNewLessonFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, View.OnClickListener,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    View view;
    TextView timeTextView, dateTextView, totalPriceTextView;
    Spinner hourSpinner, minuteSpinner, topicGroupSpinner, topicSpinner, levelSpinner;
    Button datePickerButton, timePickerButton, createNewLessonButton;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    ArrayList<TopicGroup> topicGroups;
    String hour = "00", minute = "00";
    Teacher teacher;
    Float totalPrice;
    Bundle savedState;
    ProgressDialog progressDialog;
    QwerteachService service;
    int currentTopicGroupId, currentTopicId, currentLevelId, currentTopicGroupPosition,
            currentTopicPosition, currentLevelPosition;
    Call<JsonResponse> call;
    User user;
    Date newDate;
    Calendar now;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Intent intent;


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

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_new_lesson, container, false);

        newDate  = new Date(System.currentTimeMillis());
        now = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        datePickerDialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMinDate(java.util.Calendar.getInstance());

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

        startProgressDialog();
        call = service.getTeacherTopicGroups(teacher.getUser().getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
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

    public void displayDateAndTimePickers() {
        if (savedState!= null) {
            dateTextView.setText(savedState.getString("date"));
            timeTextView.setText(savedState.getString("time"));
        } else {
            dateTextView.setText(now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR));
            timeTextView.setText(newDate.getHours() + ":" + newDate.getMinutes());
        }

        datePickerButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);

    }

    public void displayTopicGroupSpinner() {
        TopicGroupAdapter topicGroupAdapter = new TopicGroupAdapter(getContext(), R.layout.simple_spinner_item, topicGroups);
        topicGroupAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        topicGroupSpinner.setAdapter(topicGroupAdapter);
        topicGroupSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            topicGroupSpinner.setSelection(savedState.getInt("topicGroup"));
        }

    }

    public void displayHourSpinner() {
        ArrayAdapter hourSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hour_spinner_items, R.layout.simple_spinner_item);
        hourSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourSpinnerAdapter);
        hourSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            int hourPosition = getIndexByString(hourSpinner, savedState.getString("hour"));
            hourSpinner.setSelection(hourPosition);
        } else {
            hourSpinner.setSelection(1);
        }

    }

    public void displayMinutSpinner() {
        ArrayAdapter minutSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.minut_spinner_items, R.layout.simple_spinner_item);
        minutSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minutSpinnerAdapter);
        minuteSpinner.setOnItemSelectedListener(this);

        if(savedState != null) {
            int minutePosition = getIndexByString(minuteSpinner, savedState.getString("minute"));
            minuteSpinner.setSelection(minutePosition);
        }

    }

    public void displayTopicSpinner() {
        TopicAdapter topicAdapter = new TopicAdapter(getContext(), R.layout.simple_spinner_item, topics);
        topicAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            topicSpinner.setSelection(savedState.getInt("topic"));
        }

    }

    public void displayLevelSpinner() {
        for (int i = 0; i < levels.size(); i++) {
            levels.get(i).setNeedBeLevel(true);
        }
        LevelAdapter levelAdapter = new LevelAdapter(getContext(), R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
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
                setTotalPrice();
                break;
            case R.id.minut_spinner:
                minute = adapterView.getItemAtPosition(i).toString();
                setTotalPrice();
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

                Call<JsonResponse> callForLevels = service.getTeacherLevels(teacher.getUser().getUserId(),
                        currentTopicId, user.getEmail(), user.getToken());
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
                setTotalPrice();
                break;
        }

    }

    public void setTotalPrice() {
        startProgressDialog();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("hours", hour);
        requestBody.put("minutes", minute);
        requestBody.put("topic_id", String.valueOf(currentTopicId));
        requestBody.put("level_id", String.valueOf(currentLevelId));

        Call<JsonResponse> call = service.calculateUserLessonRequest(teacher.getUser().getUserId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                totalPrice = response.body().getLessonTotalPrice();
                progressDialog.dismiss();
                totalPriceTextView.setText(totalPrice + " €");
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_picker_button:
                if (Common.checkIfCurrentDate(dateTextView.getText().toString())) {
                    timePickerDialog.setMinTime(newDate.getHours(), newDate.getMinutes(), newDate.getSeconds());
                } else {
                    timePickerDialog.setMinTime(0, 0, 0);
                }
                timePickerDialog.show(getActivity().getFragmentManager(), "timePickerDialog");
                break;
            case R.id.date_picker_button:
                datePickerDialog.show(getActivity().getFragmentManager(), "datePickerDialog");
                break;
            case R.id.create_new_lesson_button:
                didTouchCreateNewLessonButton();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void didTouchCreateNewLessonButton() {
        Lesson request = new Lesson();
        request.setLevelId(currentLevelId);
        request.setTopicId(currentTopicId);
        request.setTimeStart(dateTextView.getText().toString() + " " + timeTextView.getText().toString());
        request.setHours(hour);
        request.setMinutes(minute);

        Lesson lesson = new Lesson();
        lesson.setTeacherId(teacher.getUser().getUserId());
        Map<String, Lesson> requestBody = new HashMap<>();
        requestBody.put("request", request);
        requestBody.put("lesson", lesson);

        if (Common.checkIfValidTime(dateTextView.getText().toString(), timeTextView.getText().toString())) {
            startProgressDialog();
            Call<JsonResponse> call = service.createNewLesson(teacher.getUser().getUserId(), requestBody, user.getEmail(), user.getToken());
            call.enqueue(new Callback<JsonResponse>() {
                @Override
                public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                    String message = response.body().getMessage();
                    progressDialog.dismiss();
                    switch (message) {
                        case "no account":
                            intent = new Intent(getContext(), NewVirtualWalletActivity.class);
                            intent.putExtra("status", 1);
                            startActivity(intent);
                            break;
                        case "true":
                            ArrayList<UserCreditCard> creditCards = response.body().getUserCreditCards();
                            CardRegistrationData cardRegistrationData = response.body().getCardRegistrationData();
                            intent = new Intent(getContext(), PaymentMethodActivity.class);
                            intent.putExtra("totalPrice", totalPrice);
                            intent.putExtra("teacher", teacher);
                            intent.putExtra("userCreditCardList", creditCards);
                            intent.putExtra("cardRegistration", cardRegistrationData);
                            startActivity(intent);
                            break;
                        default:
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse> call, Throwable t) {
                    Log.d("FAILURE", t.toString());
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Impossible de réserver votre cours. Veuillez réessayer ultérieurement.", Toast.LENGTH_LONG).show();

                }
            });

        } else {
            Toast.makeText(getContext(), R.string.valid_time_message, Toast.LENGTH_LONG).show();
        }

    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /*public void displayCreateVirtualWalletFragment() {
        Fragment newFragment = CreateVirtualWalletFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

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
        outState.putFloat("totalPrice", totalPrice);
        outState.putString("activityTitle", getActivity().getTitle().toString());
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (hourOfDay < 10) {
            timeTextView.setText("0" + hourOfDay+":"+minute);
        } else {
            timeTextView.setText(hourOfDay+":"+minute);
        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateTextView.setText(dayOfMonth+"/"+ (monthOfYear + 1) +"/"+ year);
    }
}
