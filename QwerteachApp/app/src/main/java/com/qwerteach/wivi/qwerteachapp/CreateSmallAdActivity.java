package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllGroupTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SaveSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.TopicAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroupAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;;

public class CreateSmallAdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        SaveSmallAdAsyncTask.ISaveSmallAdInfos,
        GetAllGroupTopicsAsyncTask.IDisplayInfosGroupTopics,
        DisplayInfosTopicsAsyncTask.IDisplayTopicInfos {

    TextView otherCourseMaterialTextView;
    EditText otherCourseMaterialEditText, descriptionEditText, fixCoursePriceEditText;
    LinearLayout checkboxesLinearLayout, coursePriceLinearLayout;
    Spinner categoryCourseSpinner, courseMaterialSpinner;
    String courseMaterialName, userId, email, token, courseCategoryName;
    ArrayList<EditText> coursePriceEditTextList;
    ArrayList<TopicGroup> topicGroups;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    CheckBox variableCoursePriceCheckbox;
    TopicGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_small_ad);

        otherCourseMaterialTextView = (TextView) findViewById(R.id.other_course_material_text_view);
        otherCourseMaterialEditText = (EditText) findViewById(R.id.other_course_material_edit_text);
        fixCoursePriceEditText = (EditText) findViewById(R.id.fix_course_price);
        descriptionEditText = (EditText) findViewById(R.id.description);
        checkboxesLinearLayout = (LinearLayout) findViewById(R.id.checkboxes_linear_layout);
        coursePriceLinearLayout = (LinearLayout) findViewById(R.id.course_price);
        categoryCourseSpinner = (Spinner) findViewById(R.id.course_category_spinner);
        courseMaterialSpinner = (Spinner) findViewById(R.id.course_material_spinner);
        variableCoursePriceCheckbox = (CheckBox) findViewById(R.id.variable_course_price);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        topicGroups = new ArrayList<>();
        topics = new ArrayList<>();
        levels = new ArrayList<>();
        coursePriceEditTextList = new ArrayList<>();

        GetAllGroupTopicsAsyncTask getAllGroupTopicsAsyncTask = new GetAllGroupTopicsAsyncTask(this);
        getAllGroupTopicsAsyncTask.execute();
    }

    public void didTouchSaveSmallAd(View view) {
        TopicGroup topicGroup = new TopicGroup();
        Topic topic = new Topic();

        for(int i = 0; i < topicGroups.size(); i++) {
            if (topicGroups.get(i).getTopicGroupTitle().equals(courseCategoryName)) {
                topicGroup = topicGroups.get(i);
            }
        }

        for(int i = 0; i < topics.size(); i++) {
            if (topics.get(i).getTopicTitle().equals(courseMaterialName)) {
                topic = topics.get(i);
            }
        }

        if (variableCoursePriceCheckbox.isChecked()) {

            for (int i = 0; i < levels.size(); i++) {
                if (levels.get(i).isChecked()) {
                    if (!coursePriceEditTextList.get(i).getText().toString().equals("")) {
                        Double coursePrice = Double.valueOf(coursePriceEditTextList.get(i).getText().toString());
                        levels.get(i).setPrice(coursePrice);
                    }
                }
            }

        } else {
            for (int i = 0; i < levels.size(); i++) {
                if (levels.get(i).isChecked()) {
                    if (!fixCoursePriceEditText.getText().toString().equals("")) {
                        Double coursePrice = Double.valueOf(fixCoursePriceEditText.getText().toString());
                        levels.get(i).setPrice(coursePrice);
                    }
                }
            }
        }

        String otherCourseMaterialName = otherCourseMaterialEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        SaveSmallAdAsyncTask saveSmallAdAsyncTask = new SaveSmallAdAsyncTask(this);
        saveSmallAdAsyncTask.execute(topicGroup, topic, otherCourseMaterialName, description, userId, levels, email, token);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        courseCategoryName = topicGroups.get(position).getTopicGroupTitle();
        DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
        displayInfosTopicsAsyncTask.execute(courseCategoryName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        final KeyListener keyListener = fixCoursePriceEditText.getKeyListener();

        switch(view.getId()) {
            case R.id.variable_course_price:
                if (checked) {
                    coursePriceLinearLayout.setVisibility(view.VISIBLE);
                    fixCoursePriceEditText.setKeyListener(null);
                    fixCoursePriceEditText.setBackgroundResource(R.color.gray);
                    fixCoursePriceEditText.setTextColor(Color.WHITE);

                } else {
                    coursePriceLinearLayout.setVisibility(view.GONE);
                    fixCoursePriceEditText.setKeyListener(keyListener);
                    fixCoursePriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    fixCoursePriceEditText.setBackgroundResource(R.drawable.edit_text_border);
                    fixCoursePriceEditText.setTextColor(Color.BLACK);
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
        try {
            JSONObject jsonObject = new JSONObject(string);
            String confirmationMessage = jsonObject.getString("success");

            if (confirmationMessage.equals("exists")) {
                Toast.makeText(this, R.string.save_small_ad_success_exists_message, Toast.LENGTH_SHORT).show();
            } else if (confirmationMessage.equals("false")) {
                Toast.makeText(this, R.string.save_small_ad_success_false_message, Toast.LENGTH_SHORT).show();
            } else if (confirmationMessage.equals("true")) {
                setResult(Activity.RESULT_OK);
                finish();
                Toast.makeText(this, R.string.save_small_ad_success_true_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.save_small_ad_success_need_message, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayInfosGroupTopics(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("topic_group");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int topicGroupId = jsonData.getInt("id");
                String topicGroupTitle = jsonData.getString("title");
                String levelCode = jsonData.getString("level_code");
                TopicGroup topicGroup = new TopicGroup(topicGroupId, topicGroupTitle, levelCode);
                topicGroups.add(topicGroup);
            }

            adapter = new TopicGroupAdapter(this, android.R.layout.simple_spinner_item, topicGroups);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryCourseSpinner.setAdapter(adapter);
            categoryCourseSpinner.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayInfosTopics(String string) {

        checkboxesLinearLayout.removeAllViews();
        coursePriceLinearLayout.removeAllViews();
        topics.clear();
        levels.clear();
        coursePriceEditTextList.clear();

        try {

            JSONObject jsonObject = new JSONObject(string);
            JSONArray topicJsonArray = jsonObject.getJSONArray("topics");
            JSONArray levelJsonArray = jsonObject.getJSONArray("levels");

            for (int i = 0; i < topicJsonArray.length(); i++) {
                JSONObject jsonData = topicJsonArray.getJSONObject(i);
                int topicId = jsonData.getInt("id");
                String topicTitle = jsonData.getString("title");
                int topicGroupId = jsonData.getInt("topic_group_id");
                Topic topic = new Topic(topicId, topicTitle, topicGroupId);
                topics.add(topic);
            }

            for (int i = 0; i < levelJsonArray.length(); i++) {
                JSONObject jsonData = levelJsonArray.getJSONObject(i);
                int levelId = jsonData.getInt("id");
                String levelName = jsonData.getString("fr");
                Level level = new Level(levelId, levelName);
                levels.add(level);
            }

            TopicAdapter topicAdapter = new TopicAdapter(this, android.R.layout.simple_spinner_item, topics);
            topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseMaterialSpinner.setAdapter(topicAdapter);

            for (int i = 0; i < levels.size(); i++) {
                CheckBox cb = new CheckBox(this);
                cb.setId(i);
                cb.setText(levels.get(i).getLevelName());
                checkboxesLinearLayout.addView(cb);

                LinearLayout linearLayout = new LinearLayout(this);
                TextView textView = new TextView(this);
                final EditText editText = new EditText(this);
                final KeyListener keyListener = editText.getKeyListener();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setWeightSum(1);

                textView.setText(levels.get(i).getLevelName());
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
                TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                textView.setLayoutParams(params1);
                editText.setLayoutParams(params2);
                editText.setPadding(10, 10, 10, 10);
                editText.setKeyListener(null);
                editText.setBackgroundResource(R.color.gray);
                editText.setId(i);
                coursePriceEditTextList.add(editText);

                linearLayout.addView(textView);
                linearLayout.addView(editText);
                coursePriceLinearLayout.addView(linearLayout, params);

                cb.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int index = view.getId();

                        if(((CheckBox) view).isChecked()) {
                            levels.get(index).setChecked(true);

                            editText.setKeyListener(keyListener);
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.setBackgroundResource(R.drawable.edit_text_border);


                        } else {
                            levels.get(index).setChecked(false);

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
                    courseMaterialName = topics.get(i).getTopicTitle();

                    if (courseMaterialName.equals("Other")) {
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
