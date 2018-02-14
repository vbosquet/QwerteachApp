package com.qwerteach.wivi.qwerteachapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.qwerteach.wivi.qwerteachapp.models.TopicGroup;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateSmallAdActivity extends AppCompatActivity  {

    SmallAd smallAd;
    ArrayList<Level> levels;
    ArrayList<SmallAdPrice> prices;
    ArrayList<EditText> coursePriceEditTextList;
    EditText descriptionEditText;
    TextView topicTextView, topicGroupTextView;
    LinearLayout coursePriceLinearLayout;
    String topic;
    TopicGroup topicGroup;
    QwerteachService service;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_small_ad);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        coursePriceLinearLayout = (LinearLayout) findViewById(R.id.course_price);
        descriptionEditText = (EditText) findViewById(R.id.description);
        topicTextView = (TextView) findViewById(R.id.topic);
        topicGroupTextView = (TextView) findViewById(R.id.topic_group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            smallAd = (SmallAd) getIntent().getSerializableExtra("smallAd");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        coursePriceEditTextList = new ArrayList<>();
        service = ApiClient.getClient().create(QwerteachService.class);
        prices = smallAd.getSmallAdPrices();

        Call<JsonResponse> call = service.showAdvertInfos(smallAd.getAdvertId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    topic = response.body().getTopicTitle();
                    topicGroup = response.body().getTopicGroup();
                    levels = response.body().getLevels();

                    topicTextView.setText(topic);
                    topicGroupTextView.setText(topicGroup.getTopicGroupTitle());
                    descriptionEditText.setText(smallAd.getDescription());
                    displayPrices();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_infos_small_ad_button:
                didTouchUpdateSmallAd();
                return true;
            case R.id.delete_small_ad_button:
                didTouchOnDeleteButton();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void didTouchOnDeleteButton() {
        Call<JsonResponse> call = service.deleteSmallAd(smallAd.getAdvertId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    String success = response.body().getSuccess();
                    if (success.equals("true")) {
                        setResult(Activity.RESULT_OK);
                        finish();
                        Toast.makeText(getApplicationContext(), R.string.delete_small_ad_success_true_message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.delete_small_ad_success_false_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void didTouchUpdateSmallAd() {
        ArrayList<SmallAdPrice> newPrices = new ArrayList<>();

        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).isChecked() && !coursePriceEditTextList.get(i).getText().toString().equals("")) {
                SmallAdPrice smallAdPrice = new SmallAdPrice();
                smallAdPrice.setLevelId(levels.get(i).getLevelId());
                smallAdPrice.setPrice(Double.valueOf(coursePriceEditTextList.get(i).getText().toString()));
                for (int j = 0; j < prices.size(); j++) {
                    if (prices.get(j).getLevelId() == levels.get(i).getLevelId()) {
                        smallAdPrice.setId(prices.get(j).getId());
                    }
                }

                newPrices.add(smallAdPrice);
            }
        }

        SmallAd newSmallAd = new SmallAd();
        newSmallAd.setDescription(descriptionEditText.getText().toString());
        newSmallAd.setSmallAdPrices(newPrices);

        Map<String, SmallAd> requestBody = new HashMap<>();
        requestBody.put("offer", newSmallAd);


        Call<JsonResponse> call = service.updateSmallAd(smallAd.getAdvertId(), smallAd.getTopicId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    String success = response.body().getSuccess();
                    String message = response.body().getMessage();

                    if (success.equals("true")) {
                        setResult(Activity.RESULT_OK);
                        finish();
                        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayPrices() {

        for (int i = 0; i < levels.size(); i++) {

            final Level level = levels.get(i);
            LinearLayout linearLayout = new LinearLayout(this);
            CheckBox checkBox = new CheckBox(this);
            final EditText editText = new EditText(this);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(1);

            TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.73f);
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.27f);

            checkBox.setText(level.getFrLevelName());
            checkBox.setTextColor(this.getResources().getColor(R.color.medium_grey));
            checkBox.setLayoutParams(params1);

            editText.setLayoutParams(params2);
            editText.setBackgroundResource(R.drawable.edit_text_border);
            editText.setGravity(Gravity.CENTER);
            editText.setTextColor(this.getResources().getColor(R.color.medium_grey));
            editText.setHint(R.string.course_price_eddit_text);
            editText.setHintTextColor(getResources().getColor(R.color.text_light_grey));
            editText.setId(i);
            editText.setVisibility(View.GONE);

            for (int j = 0; j < prices.size(); j++) {
                if (level.getLevelId() == prices.get(j).getLevelId()) {
                    editText.setText(String.valueOf(prices.get(j).getPrice()));
                    checkBox.setChecked(true);
                    level.setChecked(true);
                    displayEditTextLayout(editText);
                }

            }

            linearLayout.addView(checkBox);
            linearLayout.addView(editText);
            coursePriceLinearLayout.addView(linearLayout, params);
            coursePriceEditTextList.add(editText);

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    boolean checked = ((CheckBox) view).isChecked();

                    if(checked) {
                        level.setChecked(true);
                        displayEditTextLayout(editText);

                    } else {
                        level.setChecked(false);
                        editText.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    public void displayEditTextLayout(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setBackgroundResource(R.drawable.edit_text_border);
        editText.setVisibility(View.VISIBLE);

    }
}
