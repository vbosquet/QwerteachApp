package com.qwerteach.wivi.qwerteachapp;

import android.app.DialogFragment;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayTopicLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.DatePickerFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.TimePickerFragment;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

public class LessonReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DisplayInfosTopicsAsyncTask.IDisplayTopicInfos, DisplayTopicLevelsAsyncTask.IDisplayTopicLevels {

    TextView timeTextView, dateTextView, totalPriceTextView;
    Spinner hourSpinner, minutSpinner, topicGroupSpinner, topicSpinner, levelSpinner;
    ArrayList<String> topicGroupTitleList, topicTitleList, levelTitleList;
    ArrayList<SmallAd> smallAds;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    HashMap<String, Double> prices;
    String hour = "00", minut = "00";
    String currentLevelName = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_reservation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            topicGroupTitleList = getIntent().getStringArrayListExtra("topicGroup");
            smallAds = (ArrayList<SmallAd>) getIntent().getSerializableExtra("smallAds");
        }

        for (int j = 0; j < smallAds.size(); j++) {
            int topicId = smallAds.get(j).getTopicId();
            DisplayTopicLevelsAsyncTask displayTopicLevelsAsyncTask = new DisplayTopicLevelsAsyncTask(this);
            displayTopicLevelsAsyncTask.execute(topicId);
        }

        topicGroupTitleList = new ArrayList<>(new LinkedHashSet<>(topicGroupTitleList));
        topicTitleList = new ArrayList<>();
        levelTitleList = new ArrayList<>();
        topics = new ArrayList<>();
        levels = new ArrayList<>();
        prices = new HashMap<>();

        timeTextView = (TextView) findViewById(R.id.time_picker_text_view);
        dateTextView = (TextView) findViewById(R.id.date_picker_text_view);
        hourSpinner = (Spinner) findViewById(R.id.hour_spinner);
        minutSpinner = (Spinner) findViewById(R.id.minut_spinner);
        topicGroupSpinner = (Spinner) findViewById(R.id.topic_group_spinner);
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        levelSpinner = (Spinner) findViewById(R.id.level_spinner);
        totalPriceTextView = (TextView) findViewById(R.id.total_price_text_view);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(date);
        String currentTime = timeFormat.format(date);

        dateTextView.setText(currentDate);
        timeTextView.setText(currentTime);
        totalPriceTextView.setText("0 €");

        ArrayAdapter hourSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.hour_spinner_items, android.R.layout.simple_spinner_item);
        ArrayAdapter minutSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.minut_spinner_items, android.R.layout.simple_spinner_item);
        ArrayAdapter topicGroupSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, topicGroupTitleList);

        hourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicGroupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        hourSpinner.setAdapter(hourSpinnerAdapter);
        minutSpinner.setAdapter(minutSpinnerAdapter);
        topicGroupSpinner.setAdapter(topicGroupSpinnerAdapter);

        hourSpinner.setOnItemSelectedListener(this);
        minutSpinner.setOnItemSelectedListener(this);
        topicGroupSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson_reservation_menu, menu);
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

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.hour_spinner:
                hour = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minut);
                break;
            case R.id.minut_spinner:
                minut = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minut);
                break;
            case R.id.topic_group_spinner:
                String topicGroup = adapterView.getItemAtPosition(i).toString();
                DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
                displayInfosTopicsAsyncTask.execute(topicGroup);
                break;
            case R.id.topic_spinner:
                displayLevelSpinnerItems(i);

                break;
            case R.id.level_spinner:
                currentLevelName = adapterView.getItemAtPosition(i).toString();
                setTotalPrice(hour, minut);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

            ArrayAdapter topicSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, topicTitleList);
            topicSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            topicSpinner.setAdapter(topicSpinnerAdapter);
            topicSpinner.setOnItemSelectedListener(this);

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

    public void setTotalPrice(String hourString, String minutString) {
        Integer newMinutInt = 0;
        if (!minut.equals("00")) {
            Double minutDouble = Double.parseDouble(minutString);
            Double newMinutDouble = 100 / (60 / minutDouble);
            newMinutInt = newMinutDouble.intValue();
        }

        String totalDurationString = hourString + "." + newMinutInt;
        Double totalDurationDouble = Double.parseDouble(totalDurationString.toString());
        Double totalPrice = 0.0;
        for (Map.Entry<String, Double> entry : prices.entrySet()) {
            if (currentLevelName.equals(entry.getKey())) {
                Double price = entry.getValue();
                totalPrice = price * totalDurationDouble;
            }
        }

        totalPriceTextView.setText(totalPrice + " €");
    }

    public void didTouchBookingLessonButton(View view) {
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

    public void displayLevelSpinnerItems(int currentTopicItem) {
        levelTitleList.clear();
        prices.clear();

        int topicId = topics.get(currentTopicItem).getTopicId();

        addLevelTitleToList(topicId);
        levelTitleList = new ArrayList<>(new LinkedHashSet<>(levelTitleList));

        ArrayAdapter levelSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, levelTitleList);
        levelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelSpinnerAdapter);
        levelSpinner.setOnItemSelectedListener(this);
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
}

