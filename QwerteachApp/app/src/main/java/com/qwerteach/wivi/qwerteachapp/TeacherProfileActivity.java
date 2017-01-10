package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayTopicLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.NewLessonRequestAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SendMessageToTeacherAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.Review;
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

public class TeacherProfileActivity extends AppCompatActivity implements DisplayTopicLevelsAsyncTask.IDisplayTopicLevels,
        NewLessonRequestAsyncTask.INewLessonRequest,
        SendMessageToTeacherAsyncTask.ISendMessageToTeacher,
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile {

    Teacher teacher;
    SmallAd smallAd;
    TextView teacherName, teacherDescription, teacherOccupation, teacherAge, courseMaterialNames, minPrice;
    Button contactTeacherButton;
    ArrayList<SmallAd> smallAds;
    ArrayList<Level> levels;
    ArrayList<String> topicGroupTitleList;
    ArrayList<Review> reviews;
    String email, token, query, firstName, lastName;
    LinearLayout lastReview;
    TextView senderFirstName, reviewText, sendingDate, readMoreComments;
    RatingBar ratingBar;
    ProgressDialog progressDialog;

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
            query = getIntent().getStringExtra("query");
        }

        actionBar.setTitle(query);

        levels = new ArrayList<>();
        topicGroupTitleList = new ArrayList<>();
        smallAds = teacher.getSmallAds();
        reviews = teacher.getReviews();
        progressDialog = new ProgressDialog(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        firstName = preferences.getString("firstName", "");
        lastName = preferences.getString("lastName", "");

        for (int i = 0; i < smallAds.size(); i++) {
            int topicId = smallAds.get(i).getTopicId();
            DisplayTopicLevelsAsyncTask displayTopicLevelsAsyncTask = new DisplayTopicLevelsAsyncTask(this);
            displayTopicLevelsAsyncTask.execute(topicId);
        }


        if (reviews != null) {
            for (int i = 0; i < reviews.size(); i++) {
                int senderId = reviews.get(i).getSenderId();
                DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
                displayInfosProfileAsyncTask.execute(String.valueOf(senderId), email, token);
                startProgressDialog();
            }

        }

        displayTeacherProfileInfos();
    }

    public void displayTeacherProfileInfos() {
        teacherName.setText(teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName());
        teacherOccupation.setText(teacher.getUser().getOccupation());
        String text = teacher.getUser().getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("\\n", "");
        teacherDescription.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        contactTeacherButton.setText("Contacter " + teacher.getUser().getFirstName());
        courseMaterialNames.setText(teacher.getTopicTitleList());
        minPrice.setText("A partir de " +teacher.getMinPrice() + " €/h");
        teacherAge.setText(teacher.getUser().getAge() + " ans");
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

    public void didTouchContactButton(View view) {
        createContactTeacherAlertDialog();
    }

    public void createContactTeacherAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View content = inflater.inflate(R.layout.alert_dialog_contact_teacher, null);
        builder.setView(content);

        TextView titleTextView = (TextView) content.findViewById(R.id.alert_dialog_title);
        final EditText userMessageEditText = (EditText) content.findViewById(R.id.alert_dialog_user_message);

        String message = ("Posez une question à " + teacher.getUser().getFirstName());
        titleTextView.setText(message);

        builder.setPositiveButton("ENVOYER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String body = userMessageEditText.getText().toString();
                int recipient = teacher.getUser().getUserId();
                String subject = firstName + " " + lastName + " vous pose une question !";

                startSendMessageToTeacherAsyncTask(body, subject, recipient);

            }
        });
        builder.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();

    }

    public void startSendMessageToTeacherAsyncTask(String body, String subject, int recipient) {
        SendMessageToTeacherAsyncTask sendMessageToTeacherAsyncTask = new SendMessageToTeacherAsyncTask(this);
        sendMessageToTeacherAsyncTask.execute(email, token, subject, body, recipient);
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

        title.setText("Tarif(s) de " + teacher.getUser().getFirstName());

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
        String teacherId = String.valueOf(teacher.getUser().getUserId());
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

    @Override
    public void confirmationMessage(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyMessagesActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayUserInfosProfile(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                JSONObject userJson = jsonObject.getJSONObject("user");
                int userId = userJson.getInt("id");
                String firstName = userJson.getString("firstname");

                for (int i = 0; i < reviews.size(); i++) {
                    if (userId == reviews.get(i).getSenderId()) {
                        reviews.get(i).setSenderFirstName(firstName);
                    }

                    if (userId == reviews.get(reviews.size() - 1).getSenderId()) {
                        progressDialog.dismiss();
                        displayLastComment(firstName);
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void didTouchReadComments(View view) {

        Intent intent = new Intent(this, ReadCommentsActivity.class);
        intent.putExtra("reviews", reviews);
        startActivity(intent);
    }

    public void displayLastComment(String firstName) {
        lastReview = (LinearLayout) findViewById(R.id.last_comment);
        senderFirstName = (TextView) findViewById(R.id.sender_first_name);
        reviewText = (TextView) findViewById(R.id.review_text);
        sendingDate = (TextView) findViewById(R.id.sending_date);
        readMoreComments = (TextView) findViewById(R.id.read_more_comments);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        int lastPosition = reviews.size() - 1;

        lastReview.setVisibility(View.VISIBLE);
        senderFirstName.setText(firstName);
        reviewText.setText(reviews.get(lastPosition).getReviewText());
        String dateToFormat = reviews.get(lastPosition).getCreationDate();
        sendingDate.setText(reviews.get(lastPosition).getMonth(dateToFormat) + " "
                + reviews.get(lastPosition).getYear(dateToFormat));
        readMoreComments.setText("Lire " + teacher.getNumberOfReviews() + " commentaire(s)");
        ratingBar.setRating(teacher.getRating());

        if (reviews.size() > 1) {
            readMoreComments.setVisibility(View.VISIBLE);
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
