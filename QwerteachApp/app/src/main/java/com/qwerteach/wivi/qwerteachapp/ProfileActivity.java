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

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.fragments.StudentProfileFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.TeacherProfileFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity  {

    Intent intent;
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
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(this);
        teacher = new Teacher();

        startProgressDialog();
        QwerteachService service = ApiClient.getClient().create(QwerteachService.class);
        Call<JsonResponse> call = service.getUserInfos(user.getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                user = response.body().getUser();
                String avatarUrl = response.body().getAvatar();
                user.setAvatarUrl(avatarUrl);

                if (!user.getPostulanceAccepted()) {
                    progressDialog.dismiss();
                    displayStudentProfileFragment();

                } else {
                    preparaDataForTeacher(response);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void preparaDataForTeacher(Response<JsonResponse> response) {
        ArrayList<SmallAd> smallAds = response.body().getSmallAds();
        ArrayList<String> topics = response.body().getTopicTitles();
        ArrayList<ArrayList<SmallAdPrice>> smallAdPrices = response.body().getSmallAdPrices();
        ArrayList<Review> reviews = response.body().getReviews();
        ArrayList<String> reviewSenderNames = response.body().getReviewSenderNames();
        float rating = response.body().getRating();
        double minPrice = response.body().getMinPrice();
        ArrayList<Integer> notes = response.body().getNotes();

        for (int i = 0; i < smallAds.size(); i++) {
            smallAds.get(i).setTitle(topics.get(i));

            ArrayList<SmallAdPrice> smallAdPriceArrayList = new ArrayList<>();

            if (smallAdPrices.get(i).size() > 0) {
                for (int j = 0; j < smallAdPrices.get(i).size(); j++) {
                    smallAdPriceArrayList.add(smallAdPrices.get(i).get(j));
                }
            }

            smallAds.get(i).setSmallAdPrices(smallAdPriceArrayList);
        }

        for (int i = 0; i < reviews.size(); i++) {
            reviews.get(i).setSenderFirstName(reviewSenderNames.get(i));
        }

        teacher.setUser(user);
        teacher.setMinPrice(minPrice);
        teacher.setRating(rating);
        teacher.setSmallAds(smallAds);
        teacher.setReviews(reviews);
        teacher.setNumberOfReviews(notes.size());

        progressDialog.dismiss();
        displayTeacherProfileFragment();

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
                if (user.getPostulanceAccepted()) {
                    intent.putExtra("teacher", teacher);
                }

                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
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
