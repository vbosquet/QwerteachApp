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

import com.qwerteach.wivi.qwerteachapp.PaymentMethodActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateLessonRequestAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayTopicLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by wivi on 13/12/16.
 */

public class CreateNewLessonFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        DisplayTopicLevelsAsyncTask.IDisplayTopicLevels,
        View.OnClickListener,
        DisplayInfosTopicsAsyncTask.IDisplayTopicInfos,
        CreateLessonRequestAsyncTask.ICreateLessonRequest {

    View view;
    TextView timeTextView, dateTextView, totalPriceTextView;
    Spinner hourSpinner, minuteSpinner, topicGroupSpinner, topicSpinner, levelSpinner;
    Button datePickerButton, timePickerButton, createNewLessonButton;
    ArrayList<String> topicGroupTitleList, topicTitleList, levelTitleList;
    ArrayList<SmallAd> smallAds;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    HashMap<String, Double> prices;
    String studentId, userEmail, userToken;
    String hour = "00", minute = "00";
    String currentLevelName = "", currentTopicTitle = "", currentTopicGroup = "";
    Teacher teacher;
    Double totalPrice;
    Bundle savedState;
    ProgressDialog progressDialog;


    public static CreateNewLessonFragment newInstance() {
        CreateNewLessonFragment createNewLessonFragment = new CreateNewLessonFragment();
        return createNewLessonFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_new_lesson, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            topicGroupTitleList = getActivity().getIntent().getStringArrayListExtra("topicGroup");
            smallAds = (ArrayList<SmallAd>) getActivity().getIntent().getSerializableExtra("smallAds");
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
        }

        for (int j = 0; j < smallAds.size(); j++) {
            int topicId = smallAds.get(j).getTopicId();
            DisplayTopicLevelsAsyncTask displayTopicLevelsAsyncTask = new DisplayTopicLevelsAsyncTask(this);
            displayTopicLevelsAsyncTask.execute(topicId);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        studentId = preferences.getString("userId", "");
        userEmail = preferences.getString("email", "");
        userToken = preferences.getString("token", "");

        topicGroupTitleList = new ArrayList<>(new LinkedHashSet<>(topicGroupTitleList));
        topicTitleList = new ArrayList<>();
        levelTitleList = new ArrayList<>();
        topics = new ArrayList<>();
        levels = new ArrayList<>();
        prices = new HashMap<>();
        progressDialog = new ProgressDialog(getContext());

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

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat tf = new SimpleDateFormat("HH:mm");
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        tf.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        String currentTime = tf.format(currentLocalTime);
        String currentDate = df.format(currentLocalTime);

        dateTextView.setText(currentDate);
        timeTextView.setText(currentTime);
        totalPriceTextView.setText("0 €");

        ArrayAdapter hourSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hour_spinner_items, android.R.layout.simple_spinner_item);
        ArrayAdapter minutSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.minut_spinner_items, android.R.layout.simple_spinner_item);
        ArrayAdapter topicGroupSpinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, topicGroupTitleList);

        hourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        hourSpinner.setAdapter(hourSpinnerAdapter);
        minuteSpinner.setAdapter(minutSpinnerAdapter);
        topicGroupSpinner.setAdapter(topicGroupSpinnerAdapter);

        hourSpinner.setOnItemSelectedListener(this);
        minuteSpinner.setOnItemSelectedListener(this);
        topicGroupSpinner.setOnItemSelectedListener(this);
        datePickerButton.setOnClickListener(this);
        timePickerButton.setOnClickListener(this);
        createNewLessonButton.setOnClickListener(this);

        if (savedState != null) {
            getActivity().setTitle(savedState.getString("activityTitle"));
            dateTextView.setText(savedState.getString("date"));
            timeTextView.setText(savedState.getString("time"));

            int hourPosition = getIndexByString(hourSpinner, savedState.getString("hour"));
            int minutePosition = getIndexByString(minuteSpinner, savedState.getString("minute"));
            int topicGroupPosition = getIndexByString(topicGroupSpinner, savedState.getString("topicGroup"));

            DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
            displayInfosTopicsAsyncTask.execute(savedState.getString("topicGroup"));

            hourSpinner.setSelection(hourPosition);
            minuteSpinner.setSelection(minutePosition);
            topicGroupSpinner.setSelection(topicGroupPosition);

        }

        return  view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        savedState = new Bundle();
        savedState.putString("date", dateTextView.getText().toString());
        savedState.putString("time", timeTextView.getText().toString());
        savedState.putString("hour", hour);
        savedState.putString("minute", minute);
        savedState.putString("topicGroup", currentTopicGroup);
        savedState.putString("topic", currentTopicTitle);
        savedState.putString("level",currentLevelName);
        savedState.putDouble("totalPrice", totalPrice);
        savedState.putString("activityTitle", getActivity().getTitle().toString());
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
                currentTopicGroup = adapterView.getItemAtPosition(i).toString();
                DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
                displayInfosTopicsAsyncTask.execute(currentTopicGroup);
                break;
            case R.id.topic_spinner:
                currentTopicTitle = adapterView.getItemAtPosition(i).toString();
                displayLevelSpinnerItems(i);

                break;
            case R.id.level_spinner:
                currentLevelName = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minute);
                break;
        }

    }

    public void setTotalPrice(String hourString, String minuteString) {
        Integer newMinuteInt = 0;
        if (!minute.equals("00")) {
            Double minuteDouble = Double.parseDouble(minuteString);
            Double newMinuteDouble = 100 / (60 / minuteDouble);
            newMinuteInt = newMinuteDouble.intValue();
        }

        String totalDurationString = hourString + "." + newMinuteInt;
        Double totalDurationDouble = Double.parseDouble(totalDurationString.toString());
        totalPrice = 0.0;
        for (Map.Entry<String, Double> entry : prices.entrySet()) {
            if (currentLevelName.equals(entry.getKey())) {
                Double price = entry.getValue();
                totalPrice = price * totalDurationDouble;
            }
        }

        totalPriceTextView.setText(totalPrice + " €");

    }

    public void displayLevelSpinnerItems(int currentTopicItem) {
        levelTitleList.clear();
        prices.clear();

        int topicId = topics.get(currentTopicItem).getTopicId();

        addLevelTitleToList(topicId);
        levelTitleList = new ArrayList<>(new LinkedHashSet<>(levelTitleList));

        ArrayAdapter levelSpinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, levelTitleList);
        levelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelSpinnerAdapter);
        levelSpinner.setOnItemSelectedListener(this);

        if (savedState != null) {
            int levelPosition = getIndexByString(levelSpinner, savedState.getString("level"));
            levelSpinner.setSelection(levelPosition);

        }
    }

    public void addLevelTitleToList(int topicId) {
        for (int i = 0; i < smallAds.size(); i++) {

            if (topicId == smallAds.get(i).getTopicId()) {
                ArrayList<SmallAdPrice> smallAdPrices = smallAds.get(i).getSmallAdPrices();

                for (int j = 0; j < smallAdPrices.size(); j++) {
                    int levelId = smallAdPrices.get(j).getLevelId();
                    double price = smallAdPrices.get(j).getPrice();
                    String levelName = "";
                    for (int k = 0; k < levels.size(); k++) {
                        if (levelId == levels.get(k).getLevelId()) {
                            levelName = levels.get(k).getLevelName();
                        }
                    }

                    levelTitleList.add(levelName);
                    prices.put(levelName, price);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void displayTopicLevels(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray topicJsonArray = jsonObject.getJSONArray("levels");

            for (int i = 0; i < topicJsonArray.length(); i++) {
                JSONObject jsonData = topicJsonArray.getJSONObject(i);
                int levelId = jsonData.getInt("id");
                String levelName = jsonData.getString("fr");
                Level level = new Level(levelId, levelName);
                levels.add(level);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        int teacherId = teacher.getTeacherId();
        int levelId = 0;
        int topicId = 0;
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();
        String timeStart = date + " " + time;
        String hours = hour;
        String minutes = minute;

        for (int i = 0; i < levels.size(); i++) {
            if (currentLevelName.equals(levels.get(i).getLevelName())) {
                levelId = levels.get(i).getLevelId();
            }
        }

        for (int i = 0; i < topics.size(); i++) {
            if (currentTopicTitle.equals(topics.get(i).getTopicTitle())) {
                topicId = topics.get(i).getTopicId();
            }
        }

        CreateLessonRequestAsyncTask createLessonRequestAsyncTask = new CreateLessonRequestAsyncTask(this);
        createLessonRequestAsyncTask.execute(teacherId, studentId, levelId, topicId, timeStart, hours, minutes, false, userEmail, userToken);
        startProgressDialog();

    }

    @Override
    public void displayInfosTopics(String string) {
        topicTitleList.clear();
        topics.clear();

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray topicJsonArray = jsonObject.getJSONArray("topics");

            for (int i = 0; i < topicJsonArray.length(); i++) {
                JSONObject jsonData = topicJsonArray.getJSONObject(i);
                int topicId = jsonData.getInt("id");
                String title = jsonData.getString("title");
                int topicGroupId = jsonData.getInt("topic_group_id");
                Topic topic = new Topic(topicId, title, topicGroupId);
                addItemToTopicTitleList(topic);
            }

            ArrayAdapter topicSpinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, topicTitleList);
            topicSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            topicSpinner.setAdapter(topicSpinnerAdapter);
            topicSpinner.setOnItemSelectedListener(this);

            if (savedState != null) {
                int topicPosition = getIndexByString(topicSpinner, savedState.getString("topic"));
                topicSpinner.setSelection(topicPosition);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addItemToTopicTitleList(Topic topic) {
        String title = topic.getTopicTitle();
        int topicId = topic.getTopicId();

        for (int i = 0; i < smallAds.size(); i++) {
            if (topicId == smallAds.get(i).getTopicId()) {
                topicTitleList.add(title);
                topics.add(topic);
            }
        }

        topicTitleList = new ArrayList<>(new LinkedHashSet<>(topicTitleList));
    }

    @Override
    public void createLessonRequest(String string) {
        ArrayList<UserCreditCard> userCreditCards = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");

            if (message.equals("no account")) {

                progressDialog.dismiss();
                Fragment newFragment = CreateVirtualWalletFragment.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else if (message.equals("true")) {

                JSONArray userCardJsonArray = jsonObject.getJSONArray("user_cards");

                if (userCardJsonArray.length() > 0) {
                    for (int i = 0; i < userCardJsonArray.length(); i++) {
                        JSONObject jsonData = userCardJsonArray.getJSONObject(i);
                        String alias = jsonData.getString("alias");
                        String cardId = jsonData.getString("id");
                        UserCreditCard userCreditCard = new UserCreditCard(alias, cardId);
                        userCreditCards.add(userCreditCard);
                    }

                }

                JSONObject cardRegistrationJsonObject = jsonObject.getJSONObject("card_registration");
                String accessKey = cardRegistrationJsonObject.getString("access_key");
                String preRegistrationData = cardRegistrationJsonObject.getString("preregistration_data");
                String cardRegistrationURL = cardRegistrationJsonObject.getString("card_registration_url");
                String cardPreregistrationId = cardRegistrationJsonObject.getString("id");
                CardRegistrationData cardRegistrationData = new CardRegistrationData(accessKey,
                        preRegistrationData, cardRegistrationURL, cardPreregistrationId);

                progressDialog.dismiss();

                Intent intent = new Intent(getContext(), PaymentMethodActivity.class);
                intent.putExtra("totalPrice", totalPrice);
                intent.putExtra("teacher", teacher);
                intent.putExtra("userCreditCardList", userCreditCards);
                intent.putExtra("cardRegistration", cardRegistrationData);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
