package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.ShowProfileInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements DisplayInfosProfileAsyncTask.IDisplayInfosProfile,
        DisplayInfosSmallAdAsyncTask.IDisplayInfosSmallAd,
        ShowProfileInfosAsyncTask.IShowProfileInfos {

    Intent intent;
    String userId, email, token;
    TextView firstNameAndLastNameTextView, ageTextView, occupationTextView, descriptionTextView;
    TextView courseNamesTextView;
    LinearLayout courseNamesLinearLayout;
    Button contactUserButton;
    ProgressDialog progressDialog;
    User user;
    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firstNameAndLastNameTextView = (TextView) findViewById(R.id.firstname_and_lastanme_text_view);
        ageTextView = (TextView) findViewById(R.id.age_text_view);
        occupationTextView = (TextView) findViewById(R.id.occupation_text_view);
        descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        contactUserButton = (Button) findViewById(R.id.contact_button);
        courseNamesLinearLayout = (LinearLayout) findViewById(R.id.course_names_linear_layout);
        courseNamesTextView = (TextView) findViewById(R.id.course_names_text_view);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        progressDialog = new ProgressDialog(this);
        user = new User();
        teacher = new Teacher();


        ShowProfileInfosAsyncTask showProfileInfosAsyncTask = new ShowProfileInfosAsyncTask(this);
        showProfileInfosAsyncTask.execute(userId, email, token);
        //DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        //displayInfosProfileAsyncTask.execute(userId, email, token);
        startProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profil_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
            case R.id.edit_profile_button:
                intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayUserInfosProfile(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String getUserInfosProfile = jsonObject.getString("success");
            progressDialog.dismiss();

            if (getUserInfosProfile.equals("true")) {
                JSONObject jsonData = jsonObject.getJSONObject("user");
                int newUserId = jsonData.getInt("id");
                String firstName = jsonData.getString("firstname");
                String lastName = jsonData.getString("lastname");
                String birthdate = jsonData.getString("birthdate");
                String occupation = jsonData.getString("occupation");
                String description = jsonData.getString("description");
                String phoneNumber = jsonData.getString("phonenumber");
                boolean postulanceAccepted = jsonData.getBoolean("postulance_accepted");

                user.setUserId(newUserId);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setBirthdate(birthdate);
                user.setOccupation(occupation);
                user.setDescription(description);
                user.setPhoneNumber(phoneNumber);
                user.setPostulanceAccepted(postulanceAccepted);

                if (postulanceAccepted) {
                    DisplayInfosSmallAdAsyncTask displayInfosSmallAdAsyncTask = new DisplayInfosSmallAdAsyncTask(this);
                    displayInfosSmallAdAsyncTask.execute(userId, email, token);

                } else {
                    progressDialog.dismiss();
                    firstNameAndLastNameTextView.setText(firstName + " " + lastName);
                    ageTextView.setText(user.getAge() + " ans");
                    occupationTextView.setText(occupation);
                    descriptionTextView.setText(description);
                    contactUserButton.setText("Contacter " + firstName);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void didTouchContactButton(View view) {
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void displayInfosSmallAd(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                ArrayList<String> topicTitleList = new ArrayList<>();
                ArrayList<SmallAd> smallAds = new ArrayList<>();
                JSONArray smallAdJsonArray = jsonObject.getJSONArray("advert");
                JSONArray topicTitleJsonArray = jsonObject.getJSONArray("topic_title");

                for (int i = 0; i < smallAdJsonArray.length(); i++) {
                    JSONObject jsonData = smallAdJsonArray.getJSONObject(i);
                    int smallAdId = jsonData.getInt("id");
                    String otherName = jsonData.getString("other_name");
                    String topicTitle = topicTitleJsonArray.getString(i);
                    String description = jsonData.getString("description");
                    int topicId = jsonData.getInt("topic_id");
                    int topicGroupId = jsonData.getInt("topic_group_id");

                    if(topicTitle.equals("Other")) {
                        SmallAd smallAd = new SmallAd(otherName, smallAdId, topicId, topicGroupId, description);
                        topicTitleList.add(otherName);
                        smallAds.add(smallAd);
                    } else {
                        SmallAd smallAd = new SmallAd(topicTitle, smallAdId, topicId, topicGroupId, description);
                        topicTitleList.add(topicTitle);
                        smallAds.add(smallAd);
                    }
                }

                teacher.setUser(user);
                teacher.setTopicTitleList(topicTitleList);
                teacher.setSmallAds(smallAds);


                progressDialog.dismiss();
                firstNameAndLastNameTextView.setText(user.getFirstName() + " " + user.getLastName());
                ageTextView.setText(user.getAge() + " ans");
                occupationTextView.setText(user.getOccupation());
                descriptionTextView.setText(user.getDescription());
                contactUserButton.setText("Contacter " + user.getFirstName());
                courseNamesLinearLayout.setVisibility(View.VISIBLE);
                courseNamesTextView.setText(teacher.getTopicTitleList());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProfileInos(String string) {
        progressDialog.dismiss();
        Log.i("USER_INFOS", string);

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");
            int newUserId = userJson.getInt("id");
            String firstName = userJson.getString("firstname");
            String lastName = userJson.getString("lastname");
            String birthdate = userJson.getString("birthdate");
            String occupation = userJson.getString("occupation");
            String description = userJson.getString("description");
            String phoneNumber = userJson.getString("phonenumber");
            boolean postulanceAccepted = userJson.getBoolean("postulance_accepted");

            user.setUserId(newUserId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBirthdate(birthdate);
            user.setOccupation(occupation);
            user.setDescription(description);
            user.setPhoneNumber(phoneNumber);
            user.setPostulanceAccepted(postulanceAccepted);

            if (postulanceAccepted) {
                float avgJson = jsonObject.getLong("avg");
                JSONArray notesJson = jsonObject.getJSONArray("notes");
                JSONArray reviewsJson = jsonObject.getJSONArray("reviews");
                JSONArray pricesJson = jsonObject.getJSONArray("prices");
                JSONArray advertsJson = jsonObject.getJSONArray("adverts");

                ArrayList<SmallAd> smallAds = new ArrayList<>();
                ArrayList<Double> prices = new ArrayList<>();
                ArrayList<Review> reviews = new ArrayList<>();

                for (int i = 0; i < advertsJson.length(); i++) {
                    JSONObject jsonData = advertsJson.getJSONObject(i);

                    int smallAdId = jsonData.getInt("id");
                    int topicId = jsonData.getInt("topic_id");
                    int topicGroupId = jsonData.getInt("topic_group_id");
                    int userId = jsonData.getInt("user_id");
                    String smallAdDescription = jsonData.getString("description");

                    SmallAd smallAd = new SmallAd();
                    smallAd.setAdvertId(smallAdId);
                    smallAd.setTopicId(topicId);
                    smallAd.setTopicGroupId(topicGroupId);
                    smallAd.setUserId(userId);
                    smallAd.setDescription(smallAdDescription);

                    smallAds.add(smallAd);
                }

                for (int i = 0; i < reviewsJson.length(); i++) {
                    JSONObject jsonData = reviewsJson.getJSONObject(i);

                    int reviewId = jsonData.getInt("id");
                    int senderId = jsonData.getInt("sender_id");
                    int subjectId = jsonData.getInt("subject_id");
                    String reviewText = jsonData.getString("review_text");
                    int note = jsonData.getInt("note");
                    String creationDate = jsonData.getString("created_at");

                    Review review = new Review(reviewId, senderId, subjectId, reviewText, note, creationDate);
                    reviews.add(review);
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
