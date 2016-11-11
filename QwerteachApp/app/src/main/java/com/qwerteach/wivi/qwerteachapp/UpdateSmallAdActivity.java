package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
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
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosGroupTopicsAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplaySmallAdPriceAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.EditSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.TopicAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroupAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateSmallAdActivity extends AppCompatActivity implements DisplayInfosGroupTopicsAsyncTack.IDisplayInfosGroupTopics,
        AdapterView.OnItemSelectedListener,
        DisplayInfosTopicsAsyncTask.IDisplayTopicInfos,
        DisplaySmallAdPriceAsyncTask.IDisplaySmallAdPrice,
        EditSmallAdAsyncTask.IEditSmallAd {

    SmallAd smallAd;
    Spinner categoryCourseSpinner, courseMaterialSpinner;
    ArrayList<TopicGroup> topicGroups;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    ArrayList<SmallAdPrice> prices;
    ArrayList<EditText> coursePriceEditTextList;
    String defaultTextForTopicGroupAdapter, defaultTextForTopicAdapter, courseCategoryName, courseMaterialName;
    TextView otherCourseMaterialTextView;
    EditText otherCourseMaterialEditText, fixCoursePriceEditText, descriptionEditText;
    LinearLayout checkboxesLinearLayout, coursePriceLinearLayout;
    CheckBox variableCoursePriceCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_small_ad);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            smallAd = (SmallAd) getIntent().getSerializableExtra("smallAd");
        }

        categoryCourseSpinner = (Spinner) findViewById(R.id.course_category_spinner);
        courseMaterialSpinner = (Spinner) findViewById(R.id.course_material_spinner);
        otherCourseMaterialTextView = (TextView) findViewById(R.id.other_course_material_text_view);
        otherCourseMaterialEditText = (EditText) findViewById(R.id.other_course_material_edit_text);
        checkboxesLinearLayout = (LinearLayout) findViewById(R.id.checkboxes_linear_layout);
        coursePriceLinearLayout = (LinearLayout) findViewById(R.id.course_price);
        fixCoursePriceEditText = (EditText) findViewById(R.id.fix_course_price);
        descriptionEditText = (EditText) findViewById(R.id.description);
        variableCoursePriceCheckbox = (CheckBox) findViewById(R.id.variable_course_price);

        topicGroups = new ArrayList<>();
        topics = new ArrayList<>();
        levels = new ArrayList<>();
        prices = new ArrayList<>();
        coursePriceEditTextList = new ArrayList<>();

        DisplayInfosGroupTopicsAsyncTack displayInfosGroupTopicsAsyncTack = new DisplayInfosGroupTopicsAsyncTack(this);
        displayInfosGroupTopicsAsyncTack.execute();

        if (!smallAd.getDescription().equals("")) {
            descriptionEditText.setText(smallAd.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_small_ad_menu, menu);
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

    public void didTouchUpdateSmallAd(View view) {
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
                } else {
                    levels.get(i).setPrice(0.0);
                }
            }

        } else {
            for (int i = 0; i < levels.size(); i++) {
                if (levels.get(i).isChecked()) {
                    if (!fixCoursePriceEditText.getText().toString().equals("")) {
                        Double coursePrice = Double.valueOf(fixCoursePriceEditText.getText().toString());
                        levels.get(i).setPrice(coursePrice);
                    }
                } else {
                    levels.get(i).setPrice(0.0);
                }
            }
        }

        String otherCourseMaterialName = otherCourseMaterialEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        EditSmallAdAsyncTask editSmallAdAsyncTask = new EditSmallAdAsyncTask(this);
        editSmallAdAsyncTask.execute(topicGroup, topic, levels, otherCourseMaterialName, description, smallAd.getAdvertId());
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

            ArrayList<String> topicTitleList = new ArrayList<>();

            for (int i = 0; i < topicGroups.size(); i++) {
                topicTitleList.add(topicGroups.get(i).getTopicGroupTitle());
            }

            for (int i = 0; i < topicGroups.size(); i++) {
                if (topicGroups.get(i).getTopicGroupId() == smallAd.getTopicGroupId()) {
                    defaultTextForTopicGroupAdapter = topicGroups.get(i).getTopicGroupTitle();
                }

            }

            ArrayAdapter topicGroupAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, topicTitleList);
            topicGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryCourseSpinner.setAdapter(topicGroupAdapter);
            int position = getIndexByString(categoryCourseSpinner, defaultTextForTopicGroupAdapter);
            categoryCourseSpinner.setSelection(position);
            categoryCourseSpinner.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
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
        courseCategoryName = topicGroups.get(i).getTopicGroupTitle();
        DisplayInfosTopicsAsyncTask displayInfosTopicsAsyncTask = new DisplayInfosTopicsAsyncTask(this);
        displayInfosTopicsAsyncTask.execute(courseCategoryName, null);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void displayInfosTopics(final String string) {

        prices.clear();
        topics.clear();
        levels.clear();

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


            ArrayList<String> topicTitleList = new ArrayList<>();

            for (int i = 0; i < topics.size(); i++) {
                topicTitleList.add(topics.get(i).getTopicTitle());
            }

            for (int i = 0; i < topics.size(); i++) {
                if (topics.get(i).getTopicId() == smallAd.getTopicId()) {
                    defaultTextForTopicAdapter = topics.get(i).getTopicTitle();
                }
            }

            ArrayAdapter topicAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, topicTitleList);
            topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseMaterialSpinner.setAdapter(topicAdapter);
            int position = getIndexByString(courseMaterialSpinner, defaultTextForTopicAdapter);
            courseMaterialSpinner.setSelection(position);

            DisplaySmallAdPriceAsyncTask displaySmallAdPriceAsyncTask = new DisplaySmallAdPriceAsyncTask(this);
            displaySmallAdPriceAsyncTask.execute(smallAd.getAdvertId());

            courseMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    courseMaterialName = topics.get(i).getTopicTitle();
                    displayLevelCheckbox();

                    if (prices.size() > 0) {
                        displayFixPrice();
                    }

                    if (courseMaterialName.equals("Other") && topics.get(i).getTopicGroupId() == smallAd.getTopicGroupId()) {
                        setOtherCourseMaterialEditTextAndTextViewVisible(view, smallAd.getTitle());
                    } else if (courseMaterialName.equals("Other") && topics.get(i).getTopicGroupId() != smallAd.getTopicGroupId()) {
                        setOtherCourseMaterialEditTextAndTextViewVisible(view, "");
                    } else {
                        otherCourseMaterialTextView.setVisibility(view.GONE);
                        otherCourseMaterialEditText.setVisibility(view.GONE);
                        otherCourseMaterialEditText.setText("");
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

    public void setOtherCourseMaterialEditTextAndTextViewVisible(View view, String string) {
        otherCourseMaterialTextView.setVisibility(view.VISIBLE);
        otherCourseMaterialEditText.setVisibility(view.VISIBLE);
        otherCourseMaterialEditText.setText(string);
    }

    @Override
    public void displaySmallAdPrice(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("advert_price");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int id = jsonData.getInt("id");
                int smallAdId = jsonData.getInt("advert_id");
                int levelId = jsonData.getInt("level_id");
                double price = jsonData.getDouble("price");
                SmallAdPrice smallAdPrice = new SmallAdPrice(id, smallAdId, levelId, price);
                prices.add(smallAdPrice);
            }

            displayLevelCheckbox();
            displayFixPrice();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayLevelCheckbox() {

        checkboxesLinearLayout.removeAllViews();
        coursePriceLinearLayout.removeAllViews();
        coursePriceEditTextList.clear();

        for (int i = 0; i < levels.size(); i++) {
            CheckBox cb = new CheckBox(this);
            cb.setId(i);
            cb.setText(levels.get(i).getLevelName());
            int levelId = levels.get(i).getLevelId();

            for (int j = 0; j < prices.size(); j++) {
                if (prices.get(j).getLevelId() == levelId && courseMaterialName.equals(defaultTextForTopicAdapter)) {
                    cb.setChecked(true);
                }
            }

            checkboxesLinearLayout.addView(cb);
            final EditText editText = dislayVariableCoursePriceEditText(cb, i);
            final KeyListener keyListener = editText.getKeyListener();

            cb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int index = view.getId();
                    updateCheckBoxEditText((CheckBox) view, editText, index, keyListener);
                }
            });
        }

    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.variable_course_price:
                didTouchVariablePriceCheckBox(checked);
                break;
        }
    }

    public void updateCheckBoxEditText(CheckBox cb, EditText editText, int i, KeyListener keyListener) {
        if (cb.isChecked()) {
            levels.get(i).setChecked(true);
            editText.setKeyListener(keyListener);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setBackgroundResource(R.drawable.edit_text_border);

            for (int j = 0; j < prices.size(); j++) {
                if (levels.get(i).getLevelId() == prices.get(j).getLevelId() && courseMaterialName.equals(defaultTextForTopicAdapter)) {
                    String price = String.valueOf(prices.get(j).getPrice());
                    editText.setText(price);
                }
            }

        } else {
            levels.get(i).setChecked(false);
            editText.setKeyListener(null);
            editText.setBackgroundResource(R.color.gray);
            editText.setText("");
        }
    }

    public EditText dislayVariableCoursePriceEditText(CheckBox cb, int i) {

        LinearLayout linearLayout = new LinearLayout(this);
        TextView textView = new TextView(this);
        final EditText editText = new EditText(this);
        final KeyListener keyListener = editText.getKeyListener();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

        updateCheckBoxEditText(cb, editText, i, keyListener);

        linearLayout.addView(textView);
        linearLayout.addView(editText);
        coursePriceLinearLayout.addView(linearLayout, params);
        coursePriceEditTextList.add(editText);

        return editText;

    }

    public boolean containsSameValues(ArrayList<SmallAdPrice> arrayList) {
        double first = arrayList.get(0).getPrice();
        for (int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).getPrice() != first) {
                return false;
            }
        }
        return true;
    }

    public void didTouchVariablePriceCheckBox(Boolean checked) {
        KeyListener keyListener = fixCoursePriceEditText.getKeyListener();

        if (checked) {
            coursePriceLinearLayout.setVisibility(View.VISIBLE);
            fixCoursePriceEditText.setKeyListener(null);
            fixCoursePriceEditText.setBackgroundResource(R.color.gray);
            fixCoursePriceEditText.setTextColor(Color.WHITE);

        } else {
            coursePriceLinearLayout.setVisibility(View.GONE);
            fixCoursePriceEditText.setKeyListener(keyListener);
            fixCoursePriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            fixCoursePriceEditText.setBackgroundResource(R.drawable.edit_text_border);
            fixCoursePriceEditText.setTextColor(Color.BLACK);
        }
    }

    public void displayFixPrice() {
        if (courseMaterialName.equals(defaultTextForTopicAdapter) && courseCategoryName.equals(defaultTextForTopicGroupAdapter)) {
            Boolean isSameValues = containsSameValues(prices);

            if (isSameValues) {
                String price = String.valueOf(prices.get(0).getPrice());
                fixCoursePriceEditText.setText(price);
            } else {
                variableCoursePriceCheckbox.setChecked(true);
                didTouchVariablePriceCheckBox(true);
            }
        } else {
            fixCoursePriceEditText.setText(null);
        }
    }

    @Override
    public void confirmationRegsitrationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String confirmationRegistration = jsonObject.getString("success");

            if (confirmationRegistration.equals("true")) {
                setResult(Activity.RESULT_OK);
                finish();
                Toast.makeText(this, R.string.update_small_ad_success_true_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_small_ad_success_false_message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
