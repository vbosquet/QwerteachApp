package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.ShowProfileInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.StudentProfileFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.TeacherProfileFragment;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements ShowProfileInfosAsyncTask.IShowProfileInfos {

    Intent intent;
    String userId, email, token;
    ProgressDialog progressDialog;
    User user;
    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        progressDialog = new ProgressDialog(this);
        user = new User();
        teacher = new Teacher();


        ShowProfileInfosAsyncTask showProfileInfosAsyncTask = new ShowProfileInfosAsyncTask(this);
        showProfileInfosAsyncTask.execute(userId, email, token);
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

                if (user.isPostulanceAccepted()) {
                    intent.putExtra("teacher", teacher);
                } else {
                    intent.putExtra("student", user);
                }

                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void showProfileInfos(String string) {

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
            int useLevelId = userJson.getInt("level_id");

            user.setUserId(newUserId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBirthdate(birthdate);
            user.setOccupation(occupation);
            user.setDescription(description);
            user.setPhoneNumber(phoneNumber);
            user.setPostulanceAccepted(postulanceAccepted);
            user.setLevelId(useLevelId);

            if (postulanceAccepted) {
                float avg = 0;
                if (!jsonObject.isNull("avg")) {
                    avg = jsonObject.getLong("avg");
                }

                double minPrice = jsonObject.getDouble("min_price");
                JSONArray notesJson = jsonObject.getJSONArray("notes");
                JSONArray reviewsJson = jsonObject.getJSONArray("reviews");
                JSONArray advertsJson = jsonObject.getJSONArray("adverts");
                JSONArray topicsJson = jsonObject.getJSONArray("topics");
                JSONArray reviewsSanderNamesJson = jsonObject.getJSONArray("review_sender_names");
                JSONArray advertPricesJson = jsonObject.getJSONArray("advert_prices");

                ArrayList<SmallAd> smallAds = new ArrayList<>();
                ArrayList<Review> reviews = new ArrayList<>();

                for (int i = 0; i < advertsJson.length(); i++) {

                    JSONObject jsonData = advertsJson.getJSONObject(i);
                    String topicTitle = topicsJson.getString(i);
                    int smallAdId = jsonData.getInt("id");
                    int topicId = jsonData.getInt("topic_id");
                    int topicGroupId = jsonData.getInt("topic_group_id");
                    int teacherId = jsonData.getInt("user_id");
                    String smallAdDescription = jsonData.getString("description");

                    JSONArray advertPrices = advertPricesJson.getJSONArray(i);
                    ArrayList<SmallAdPrice> smallAdPrices = new ArrayList<>();

                    for (int j = 0; j < advertPrices.length(); j++) {
                        JSONObject advertPricesData = advertPrices.getJSONObject(j);
                        int id = advertPricesData.getInt("id");
                        int levelId = advertPricesData.getInt("level_id");
                        double price = advertPricesData.getDouble("price");

                        SmallAdPrice smallAdPrice = new SmallAdPrice(id, levelId, price);
                        smallAdPrices.add(smallAdPrice);
                    }

                    SmallAd smallAd = new SmallAd();
                    smallAd.setAdvertId(smallAdId);
                    smallAd.setTopicId(topicId);
                    smallAd.setTopicGroupId(topicGroupId);
                    smallAd.setUserId(teacherId);
                    smallAd.setDescription(smallAdDescription);
                    smallAd.setTitle(topicTitle);
                    smallAd.setSmallAdPrices(smallAdPrices);

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
                    String senderFirstName = reviewsSanderNamesJson.getString(i);

                    Review review = new Review(reviewId, senderId, subjectId, reviewText, note, creationDate);
                    review.setSenderFirstName(senderFirstName);
                    reviews.add(review);
                }

                teacher.setUser(user);
                teacher.setSmallAds(smallAds);
                teacher.setReviews(reviews);
                teacher.setRating(avg);
                teacher.setNumberOfReviews(notesJson.length());
                teacher.setMinPrice(minPrice);

                progressDialog.dismiss();
                displayTeacherProfileFragment();

            } else {
                progressDialog.dismiss();
                displayStudentProfileFragment();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayTeacherProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("teacher", teacher);
        TeacherProfileFragment teacherProfileFragment = new TeacherProfileFragment();
        teacherProfileFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, teacherProfileFragment);
        transaction.commit();
    }

    public void displayStudentProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("student", user);
        StudentProfileFragment studentProfileFragment = new StudentProfileFragment();
        studentProfileFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, studentProfileFragment);
        transaction.commit();
    }
}
