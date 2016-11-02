package com.qwerteach.wivi.qwerteachapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.AsyncTasks.DisplayInfosGroupTopicsAsyncTack;
import com.qwerteach.wivi.qwerteachapp.AsyncTasks.DisplayInfosTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.AsyncTasks.SaveSmallAdAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateSmallAdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SaveSmallAdAsyncTask.ISaveSmallAdInfos,
        DisplayInfosGroupTopicsAsyncTack.IDisplayInfosGroupTopics,
        DisplayInfosTopicsAsyncTask.IDisplayTopicInfos {

    TextView courseMaterialTextView, otherCourseMaterialTextView;
    EditText otherCourseMaterialEditText, descriptionEditText;
    LinearLayout courseMaterialLinearLayout, checkboxesLinearLayout, coursePriceLinearLayout;
    Spinner categoryCourseSpinner, courseMaterialSpinner;
    String courseCategoryName, courseMaterialName, userId;
    ArrayList<String> courseCategoryNamesList, levelNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_small_ad);

        courseMaterialTextView = (TextView) findViewById(R.id.course_materiel_text_view);
        otherCourseMaterialTextView = (TextView) findViewById(R.id.other_course_material_text_view);
        otherCourseMaterialEditText = (EditText) findViewById(R.id.other_course_material_edit_text);
        descriptionEditText = (EditText) findViewById(R.id.description);
        courseMaterialLinearLayout = (LinearLayout) findViewById(R.id.course_material_linear_layout);
        checkboxesLinearLayout = (LinearLayout) findViewById(R.id.checkboxes_linear_layout);
        coursePriceLinearLayout = (LinearLayout) findViewById(R.id.course_price);
        categoryCourseSpinner = (Spinner) findViewById(R.id.course_category_spinner);
        courseMaterialSpinner = (Spinner) findViewById(R.id.course_material_spinner);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");

        courseCategoryNamesList = new ArrayList<>();
        levelNamesList = new ArrayList<>();

        DisplayInfosGroupTopicsAsyncTack displayInfosGroupTopicsAsyncTack = new DisplayInfosGroupTopicsAsyncTack(this);
        displayInfosGroupTopicsAsyncTack.execute();
    }

    public void didTouchSaveSmallAd(View view) {
        String otherCourseMaterialName = otherCourseMaterialEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        SaveSmallAdAsyncTask saveSmallAdAsyncTask = new SaveSmallAdAsyncTask(this);
        saveSmallAdAsyncTask.execute(courseCategoryName, courseMaterialName, otherCourseMaterialName, description, userId);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        courseCategoryName = adapterView.getItemAtPosition(position).toString();
        DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
        displayInfosTopicsAsyncTask.execute(courseCategoryName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.variable_course_price:
                if (checked) {
                    coursePriceLinearLayout.setVisibility(view.VISIBLE);

                } else {
                    coursePriceLinearLayout.setVisibility(view.GONE);
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_small_ad_menu, menu);
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

    @Override
    public void displayRegistrationConfirmationMessage(String string) {

    }

    @Override
    public void displayInfosGroupTopics(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("topic_group");

            for (int i = 0; i < jsonArray.length(); i++) {
                courseCategoryNamesList.add(jsonArray.getString(i));
            }

            ArrayAdapter<String> courseCategoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseCategoryNamesList);
            courseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryCourseSpinner.setAdapter(courseCategoryAdapter);
            categoryCourseSpinner.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayInfosTopics(String string) {

        levelNamesList.clear();

        try {

            final ArrayList<String> courseMaterialNamesList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(string);
            JSONArray topicJsonArray = jsonObject.getJSONArray("topics");
            JSONArray levelJsonArray = jsonObject.getJSONArray("levels");

            for (int i = 0; i < topicJsonArray.length(); i++) {
                JSONObject jsonData = topicJsonArray.getJSONObject(i);
                courseMaterialNamesList.add(jsonData.getString("title"));
            }

            for (int i = 0; i < levelJsonArray.length(); i++) {
                JSONObject jsonData = levelJsonArray.getJSONObject(i);
                levelNamesList.add(jsonData.getString("fr"));
            }

            checkboxesLinearLayout.removeAllViews();
            coursePriceLinearLayout.removeAllViews();

            ArrayAdapter<String> courseMaterialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseMaterialNamesList);
            courseMaterialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseMaterialSpinner.setAdapter(courseMaterialAdapter);

            courseMaterialTextView.setVisibility(View.VISIBLE);
            courseMaterialLinearLayout.setVisibility(View.VISIBLE);

            for (int i = 0; i < levelNamesList.size(); i++) {
                CheckBox cb = new CheckBox(this);
                cb.setId(i);
                cb.setText(levelNamesList.get(i));
                checkboxesLinearLayout.addView(cb);

                LinearLayout linearLayout = new LinearLayout(this);
                TextView textView = new TextView(this);
                final EditText editText = new EditText(this);
                final KeyListener keyListener = editText.getKeyListener();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setWeightSum(1);

                textView.setText(levelNamesList.get(i));
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
                TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                textView.setLayoutParams(params1);
                editText.setLayoutParams(params2);
                editText.setPadding(10, 10, 10, 10);
                editText.setKeyListener(null);
                editText.setBackgroundResource(R.color.gray);

                linearLayout.addView(textView);
                linearLayout.addView(editText);
                coursePriceLinearLayout.addView(linearLayout, params);

                cb.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(((CheckBox) view).isChecked()) {
                            editText.setKeyListener(keyListener);
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.setBackgroundResource(R.drawable.edit_text_border);
                        } else {
                            editText.setKeyListener(null);
                            editText.setBackgroundResource(R.color.gray);
                        }
                    }
                });
            }

            courseMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    courseMaterialName = adapterView.getItemAtPosition(i).toString();
                    String otherCourseMaterialName = courseMaterialNamesList.get(courseMaterialNamesList.size() - 1);

                    if (adapterView.getItemAtPosition(i).toString().equals(otherCourseMaterialName)) {
                        otherCourseMaterialTextView.setVisibility(view.VISIBLE);
                        otherCourseMaterialEditText.setVisibility(view.VISIBLE);
                    } else {
                        otherCourseMaterialTextView.setVisibility(view.GONE);
                        otherCourseMaterialEditText.setVisibility(view.GONE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}