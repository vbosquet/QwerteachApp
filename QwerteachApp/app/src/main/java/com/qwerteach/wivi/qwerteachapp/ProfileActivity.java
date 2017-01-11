package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements ShowProfileInfosAsyncTask.IShowProfileInfos {

    Intent intent;
    String userId, email, token;
    TextView firstNameAndLastNameTextView, ageTextView, occupationTextView, descriptionTextView;
    TextView courseNamesTextView, reviewSenderTextView, reviewSendingDateTextView, reviewText;
    TextView readMoreCommentsTextView, minPriceTextView;
    LinearLayout courseNamesLinearLayout, lastCommentLinearLayout, priceLinearLayout;
    Button contactUserButton, lessonReservationButton;
    RatingBar ratingBar;
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
        lessonReservationButton = (Button) findViewById(R.id.reservation_button);
        lastCommentLinearLayout = (LinearLayout) findViewById(R.id.last_comment);
        reviewSenderTextView = (TextView) findViewById(R.id.sender_first_name);
        reviewSendingDateTextView = (TextView) findViewById(R.id.sending_date);
        reviewText = (TextView) findViewById(R.id.review_text);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        readMoreCommentsTextView = (TextView)  findViewById(R.id.read_more_comments);
        priceLinearLayout = (LinearLayout) findViewById(R.id.price_linear_layout);
        minPriceTextView = (TextView) findViewById(R.id.teacher_min_price);

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

            user.setUserId(newUserId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBirthdate(birthdate);
            user.setOccupation(occupation);
            user.setDescription(description);
            user.setPhoneNumber(phoneNumber);
            user.setPostulanceAccepted(postulanceAccepted);

            if (postulanceAccepted) {
                float avg = jsonObject.getLong("avg");
                double minPrice = jsonObject.getDouble("min_price");
                JSONArray notesJson = jsonObject.getJSONArray("notes");
                JSONArray reviewsJson = jsonObject.getJSONArray("reviews");
                JSONArray advertsJson = jsonObject.getJSONArray("adverts");
                JSONArray topicsJson = jsonObject.getJSONArray("topics");
                JSONArray reviewsSanderNamesJson = jsonObject.getJSONArray("review_sender_names");

                ArrayList<SmallAd> smallAds = new ArrayList<>();
                ArrayList<Review> reviews = new ArrayList<>();

                for (int i = 0; i < advertsJson.length(); i++) {

                    JSONObject jsonData = advertsJson.getJSONObject(i);
                    String topicTitle = topicsJson.getString(i);
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
                    smallAd.setTitle(topicTitle);

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
            }

            progressDialog.dismiss();
            displayProfileInfos();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayProfileInfos() {
        firstNameAndLastNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        ageTextView.setText(user.getAge() + " ans");
        occupationTextView.setText(user.getOccupation());
        descriptionTextView.setText(user.getDescription());
        contactUserButton.setText("Contacter " + user.getFirstName());
        courseNamesLinearLayout.setVisibility(View.VISIBLE);
        courseNamesTextView.setText(teacher.getTopics());
        lessonReservationButton.setVisibility(View.VISIBLE);
        priceLinearLayout.setVisibility(View.VISIBLE);
        minPriceTextView.setText("A partir de " + teacher.getMinPrice() + " €/h");
        courseNamesTextView.setText(teacher.getTopics());

        if (teacher.getNumberOfReviews() > 0) {
            displayLastComment();
        }
    }

    public void displayLastComment() {
        lastCommentLinearLayout.setVisibility(View.VISIBLE);
        int lastPosition = teacher.getReviews().size() - 1;
        String dateToFormat = teacher.getReviews().get(lastPosition).getCreationDate();
        reviewSendingDateTextView.setText(teacher.getReviews().get(lastPosition).getMonth(dateToFormat)
                + " " + teacher.getReviews().get(lastPosition).getYear(dateToFormat));
        ratingBar.setRating(teacher.getRating());
        reviewText.setText(teacher.getReviews().get(lastPosition).getReviewText());
        reviewSenderTextView.setText(teacher.getReviews().get(lastPosition).getSenderFirstName());

        if (teacher.getReviews().size() > 1) {
            readMoreCommentsTextView.setVisibility(View.VISIBLE);
            readMoreCommentsTextView.setText("Lire " + teacher.getNumberOfReviews() + " commentaire(s)");
        }
    }
}
