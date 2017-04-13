package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.Gravity;
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

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.TopicAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;
import com.qwerteach.wivi.qwerteachapp.models.TopicGroupAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;;import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateSmallAdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView otherCourseMaterialTextView;
    EditText otherCourseMaterialEditText, descriptionEditText;
    LinearLayout coursePriceLinearLayout;
    Spinner categoryCourseSpinner, courseMaterialSpinner;
    String courseMaterialName, courseCategoryName;
    ArrayList<EditText> coursePriceEditTextList;
    ArrayList<TopicGroup> topicGroups;
    ArrayList<Topic> topics;
    ArrayList<Level> levels;
    ArrayList<SmallAdPrice> prices;
    QwerteachService service;
    int topicId, topicGroupId;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_small_ad);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        otherCourseMaterialTextView = (TextView) findViewById(R.id.other_course_material_text_view);
        otherCourseMaterialEditText = (EditText) findViewById(R.id.other_course_material_edit_text);
        descriptionEditText = (EditText) findViewById(R.id.description);
        coursePriceLinearLayout = (LinearLayout) findViewById(R.id.course_price);
        categoryCourseSpinner = (Spinner) findViewById(R.id.course_category_spinner);
        courseMaterialSpinner = (Spinner) findViewById(R.id.course_material_spinner);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        coursePriceEditTextList = new ArrayList<>();
        prices = new ArrayList<>();

        service = ApiClient.getClient().create(QwerteachService.class);
        getTopicGroups();


    }

    public void getTopicGroups() {
        Call<JsonResponse> call = service.getAllTopicGroups();
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                topicGroups = response.body().getTopicGroups();
                displayTopicGroupSpinner();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void getTopics() {
        Call<JsonResponse> call = service.getTopics(topicGroupId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                topics = response.body().getTopics();
                displayTopicSpinner();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void getLevels() {
        Call<JsonResponse> call = service.getLevels(topicId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                levels = response.body().getLevels();
                displayLevelCheckboxes();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void displayTopicGroupSpinner() {
        TopicGroupAdapter adapter = new TopicGroupAdapter(this, R.layout.simple_spinner_item, topicGroups);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        categoryCourseSpinner.setAdapter(adapter);
        categoryCourseSpinner.setOnItemSelectedListener(this);

    }

    public void displayTopicSpinner() {
        TopicAdapter topicAdapter = new TopicAdapter(this, R.layout.simple_spinner_item, topics);
        topicAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        courseMaterialSpinner.setAdapter(topicAdapter);
        courseMaterialSpinner.setOnItemSelectedListener(this);
    }

    public void didTouchSaveSmallAd() {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).isChecked() && !coursePriceEditTextList.get(i).getText().toString().equals("")) {
                SmallAdPrice smallAdPrice = new SmallAdPrice();
                smallAdPrice.setPrice(Double.valueOf(coursePriceEditTextList.get(i).getText().toString()));
                smallAdPrice.setLevelId(levels.get(i).getLevelId());
                prices.add(smallAdPrice);
            }
        }

        String otherCourseMaterialName = otherCourseMaterialEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        SmallAd smallAd = new SmallAd();
        smallAd.setSmallAdPrices(prices);
        smallAd.setTopicId(topicId);
        smallAd.setTopicGroupId(topicGroupId);
        smallAd.setDescription(description);

        if (!otherCourseMaterialName.equals("")) {
            smallAd.setOtherName(otherCourseMaterialName);
        }

        final Map<String, SmallAd> requestBody = new HashMap<>();
        requestBody.put("offer", smallAd);

        Call<JsonResponse> call = service.createNewAdvert(requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();

                if (success.equals("true")) {
                    setResult(Activity.RESULT_OK);
                    finish();
                    Toast.makeText(getApplication(), R.string.save_small_ad_success_true_message, Toast.LENGTH_SHORT).show();

                } else {
                    String message = response.body().getMessage();
                    Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.course_category_spinner:
                courseCategoryName = topicGroups.get(position).getTopicGroupTitle();
                topicGroupId = topicGroups.get(position).getTopicGroupId();
                getTopics();
                break;
            case R.id.course_material_spinner:
                courseMaterialName = topics.get(position).getTopicTitle();
                topicId = topics.get(position).getTopicId();
                getLevels();

                if (courseMaterialName.equals("Autre")) {
                    otherCourseMaterialTextView.setVisibility(view.VISIBLE);
                    otherCourseMaterialEditText.setVisibility(view.VISIBLE);
                } else {
                    otherCourseMaterialTextView.setVisibility(view.GONE);
                    otherCourseMaterialEditText.setVisibility(view.GONE);
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_infos_small_ad_button:
                didTouchSaveSmallAd();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayLevelCheckboxes() {

        coursePriceLinearLayout.removeAllViews();
        coursePriceEditTextList.clear();

        for (int i = 0; i < levels.size(); i++) {
            final Level level = levels.get(i);

            LinearLayout linearLayout = new LinearLayout(this);
            CheckBox checkBox = new CheckBox(this);
            final EditText editText = new EditText(this);
            final KeyListener keyListener = editText.getKeyListener();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(1);

            checkBox.setText(level.getFrLevelName());
            checkBox.setTextColor(getResources().getColor(R.color.medium_grey));
            TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.73f);
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.27f);
            checkBox.setLayoutParams(params1);
            editText.setLayoutParams(params2);
            editText.setKeyListener(null);
            editText.setBackgroundResource(R.color.gray);
            editText.setGravity(Gravity.CENTER);
            editText.setId(i);
            coursePriceEditTextList.add(editText);

            linearLayout.addView(checkBox);
            linearLayout.addView(editText);
            coursePriceLinearLayout.addView(linearLayout, params);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox) view).isChecked();

                    if(checked) {
                        level.setChecked(true);
                        editText.setKeyListener(keyListener);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setBackgroundResource(R.drawable.edit_text_border);
                        editText.setHint(R.string.course_price_eddit_text);
                        editText.setHintTextColor(getResources().getColor(R.color.text_light_grey));
                        editText.setTextColor(getResources().getColor(R.color.medium_grey));


                    } else {
                        level.setChecked(false);
                        editText.setKeyListener(null);
                        editText.setBackgroundResource(R.color.text_light_grey);
                        editText.setText("");
                    }
                }
            });
        }

    }
}
