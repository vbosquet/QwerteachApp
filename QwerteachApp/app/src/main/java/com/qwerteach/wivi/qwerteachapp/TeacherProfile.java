package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayTopicLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.NewLessonRequestAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.focusable;
import static android.R.attr.format;
import static android.R.attr.top;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class TeacherProfile extends AppCompatActivity implements DisplayTopicLevelsAsyncTask.IDisplayTopicLevels,
        NewLessonRequestAsyncTask.INewLessonRequest {

    Teacher teacher;
    SmallAd smallAd;
    TextView teacherName, teacherDescription, teacherOccupation, teacherAge, courseMaterialNames, minPrice;
    Button contactTeacherButton;
    ArrayList<SmallAd> smallAds;
    ArrayList<Level> levels;
    ArrayList<String> topicGroupTitleList;
    String email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        teacherName = (TextView) findViewById(R.id.firstname_and_lastanme_text_view);
        teacherDescription = (TextView) findViewById(R.id.description_text_view);
        teacherOccupation = (TextView) findViewById(R.id.occupation_text_view);
        teacherAge = (TextView) findViewById(R.id.age_text_view);
        courseMaterialNames = (TextView) findViewById(R.id.course_names_text_view);
        minPrice = (TextView) findViewById(R.id.teacher_min_price);
        contactTeacherButton = (Button) findViewById(R.id.contact_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getIntent().getSerializableExtra("teacher");
            smallAd = (SmallAd) getIntent().getSerializableExtra("smallAd");
        }

        smallAds = new ArrayList<>();
        levels = new ArrayList<>();
        topicGroupTitleList = new ArrayList<>();
        smallAds = teacher.getSmallAds();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        for (int i = 0; i < smallAds.size(); i++) {
            int topicId = smallAds.get(i).getTopicId();
            DisplayTopicLevelsAsyncTask displayTopicLevelsAsyncTask = new DisplayTopicLevelsAsyncTask(this);
            displayTopicLevelsAsyncTask.execute(topicId);
        }

        displayTeacherProfileInfos();
    }

    public void displayTeacherProfileInfos() {
        teacherName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        teacherOccupation.setText(teacher.getOccupation());
        String text = teacher.getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("\\n", "");
        teacherDescription.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        contactTeacherButton.setText("Contacter " + teacher.getFirstName());
        courseMaterialNames.setText(teacher.getTopicTitleList());
        minPrice.setText("A partir de " +teacher.getMinPrice() + " €/h");

        Date currentDate = new Date();
        currentDate.getTime();

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = format.parse(teacher.getBirthDate());
            int age = getDiffBetYears(birthDate, currentDate);
            teacherAge.setText(age + " ans");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_profile_menu, menu);
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

    public static int getDiffBetYears(Date first, Date last) {
        Calendar firstDate = getCalendar(first);
        Calendar secDate = getCalendar(last);
        int diff = secDate.get(YEAR) - firstDate.get(YEAR);
        if (firstDate.get(MONTH) > secDate.get(MONTH) ||
                (firstDate.get(MONTH) == secDate.get(MONTH) && firstDate.get(DATE) > secDate.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void didTouchContactButton(View view) {
    }

    public void didTouchSeeDetailedPrices(View view) {
        createAlertDialog();
    }


    public void createAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.detailed_prices_alert_dialog, null);
        builder.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        LinearLayout alertDialog = (LinearLayout) dialogView.findViewById(R.id.alert_dialog_linear_layout);

        title.setText("Tarif(s) de " + teacher.getFirstName());

        for (int i = 0; i < smallAds.size(); i++) {
            String topicTitle = smallAds.get(i).getTitle();
            String topicGroupTitle = topicGroupTitleList.get(i);
            addSmallAdTitlesToAlertDialog(topicTitle, topicGroupTitle, alertDialog);

            ArrayList<SmallAdPrice> smallAdPrices = smallAds.get(i).getSmallAdPrices();

            for (int j = 0; j < smallAdPrices.size(); j++) {
                int levelId = smallAdPrices.get(j).getLevelId();
                String price = String.valueOf(smallAdPrices.get(j).getPrice());
                String levelName = "";

                for (int k = 0; k < levels.size(); k++) {
                    if (levels.get(k).getLevelId() == levelId) {
                        levelName = levels.get(k).getLevelName();
                    }
                }

                addSmallAdLevelsAndPricesToAlertDialog(price, levelName, alertDialog);
            }
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void addSmallAdTitlesToAlertDialog(String topicTitle, String topicGroupTitle, LinearLayout alertDialog) {
        LinearLayout titleLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView topic = new TextView(this);
        topic.setText(topicGroupTitle + " - " + topicTitle);
        topic.setPadding(0, 20, 0, 20);
        topic.setTextColor(this.getColor(R.color.colorPrimary));
        titleLinearLayout.addView(topic);
        alertDialog.addView(titleLinearLayout, titleParams);
    }

    public void addSmallAdLevelsAndPricesToAlertDialog(String priceString, String levelName, LinearLayout alertDialog) {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView level = new TextView(this);
        TextView price = new TextView(this);

        level.setText(levelName);
        price.setText(priceString + " €/h");

        TableRow.LayoutParams levelParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
        TableRow.LayoutParams priceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
        level.setLayoutParams(levelParams);
        price.setLayoutParams(priceParams);

        linearLayout.addView(level);
        linearLayout.addView(price);

        alertDialog.addView(linearLayout, params);

    }

    @Override
    public void displayTopicLevels(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("levels");
            String topicGroupTitle = jsonObject.getString("topic_group_title");
            topicGroupTitleList.add(topicGroupTitle);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int levelId = jsonData.getInt("id");
                String levelName = jsonData.getString("fr");
                Level level = new Level(levelId, levelName);
                levels.add(level);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void didTouchLessonReservationButton(View view) {
        String teacherId = String.valueOf(teacher.getTeacherId());
        NewLessonRequestAsyncTask newLessonRequestAsyncTask = new NewLessonRequestAsyncTask(this);
        newLessonRequestAsyncTask.execute(teacherId, email, token);
    }

    @Override
    public void lessonRequest(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                Intent intent = new Intent(this, LessonReservationActivity.class);
                intent.putStringArrayListExtra("topicGroup", topicGroupTitleList);
                intent.putExtra("smallAds", smallAds);
                intent.putExtra("teacher", teacher);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
