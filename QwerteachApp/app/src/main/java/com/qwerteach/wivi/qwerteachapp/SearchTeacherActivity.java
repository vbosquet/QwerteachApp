package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.OptionAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTeacherActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        TeacherAdapter.MyClickListener, View.OnClickListener {

    ArrayList<Teacher> teacherList;
    ArrayList<String> menuItems, optionList;
    RecyclerView teacherRecyclerView;
    RecyclerView.Adapter teacherAdapter;
    RecyclerView.LayoutManager teacherLayoutManager;
    Spinner searchSortingOptionsSpinner;
    int currentOptionId = 0, page = 1, scrollPosition = 0;
    String query, email, token, currentOption = "";
    ProgressDialog progressDialog;
    FloatingActionButton floatingActionButton;
    QwerteachService service;
    ArrayList<ArrayList<String>> searchOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchSortingOptionsSpinner = (Spinner) findViewById(R.id.search_sorting_options_spinner);
        teacherRecyclerView = (RecyclerView) findViewById(R.id.teacher_recycler_view);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        query = getIntent().getStringExtra("query");
        teacherList = new ArrayList<>();
        menuItems = new ArrayList<>();
        optionList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);

        menuItems.add(query);
        startSearchTeacherAsyncTask();
        getAllTopics();

    }

    public void getAllTopics() {
        Call<JsonResponse> call = service.getAllTopics();
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                ArrayList<Topic> topics = response.body().getTopics();
                for (int i = 0; i < topics.size(); i++) {
                    if (!topics.get(i).getTopicTitle().equals("Other")) {
                        menuItems.add(topics.get(i).getTopicTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_teacher_menu, menu);
        displayMenuSpinner(menu);
        return true;
    }

    public void displayMenuSpinner(Menu menu) {
        MenuItem item = menu.findItem(R.id.teacher_search_filter);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter topicAdapter = new ArrayAdapter(this, R.layout.drop_down_menu_item, menuItems) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(topicAdapter);
        spinner.setOnItemSelectedListener(this);

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

    public void displayTeacherListView() {
        teacherAdapter = new TeacherAdapter(teacherList, this, this);
        teacherRecyclerView.setHasFixedSize(true);
        teacherLayoutManager = new LinearLayoutManager(this);
        teacherRecyclerView.setLayoutManager(teacherLayoutManager);
        teacherRecyclerView.setItemAnimator(new DefaultItemAnimator());
        teacherRecyclerView.setAdapter(teacherAdapter);
        teacherRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.search_sorting_options_spinner:
                currentOption = searchOptions.get(i).get(1);
                if (currentOptionId == i) {
                    return;
                } else {
                    startSearchTeacherAsyncTask();
                }

                currentOptionId = i;
                break;
            case R.id.teacher_search_filter:
                String newQuery = menuItems.get(i);
                if (!newQuery.equals(query)) {
                    Intent intent = new Intent(this, SearchTeacherActivity.class);
                    intent.putExtra("query", newQuery);
                    startActivity(intent);
                    finish();
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void displaySearchSortingOptionsSpinner() {
        OptionAdapter searchOptionsAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, optionList);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSortingOptionsSpinner.setAdapter(searchOptionsAdapter);
        searchSortingOptionsSpinner.setSelection(currentOptionId);
        searchSortingOptionsSpinner.setOnItemSelectedListener(this);
    }

    public void startSearchTeacherAsyncTask() {
        scrollPosition = 0;
        teacherList.clear();
        startProgressDialog();
        getSearchResults(query, currentOption, 1);

    }

    public void getSearchResults(String query, final String searchSortingOption, int pageNumber) {
        optionList.clear();

        Call<JsonResponse> call = service.getSearchResults(query, searchSortingOption, pageNumber, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                ArrayList<User> users = response.body().getUsers();
                searchOptions = response.body().getOptions();

                if (users.size() > 0) {
                    for (int i = 0; i < users.size(); i++) {
                        Teacher teacher = new Teacher();
                        teacher.setUser(users.get(i));
                        teacherList.add(teacher);
                        getTeacherInfos(users.get(i).getUserId());
                    }
                } else if (users.size() == 0 && page == 1){
                    progressDialog.dismiss();
                    Toast.makeText(getApplication(), R.string.no_result_found_toast_message, Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                }

                for (int i = 0; i < searchOptions.size(); i++) {
                    optionList.add(searchOptions.get(i).get(0));
                }

                displaySearchSortingOptionsSpinner();

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    public void getTeacherInfos(int userId) {
        Call<JsonResponse> call = service.getUserInfos(String.valueOf(userId), email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                prepareDataForTeacher(response);

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void prepareDataForTeacher(Response<JsonResponse> response) {
        User user = response.body().getUser();
        String avatarUrl = response.body().getAvatar();
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

        for (int i = 0; i < teacherList.size(); i++) {
            if (Objects.equals(teacherList.get(i).getUser().getUserId(), user.getUserId())) {
                teacherList.get(i).setSmallAds(smallAds);
                teacherList.get(i).setReviews(reviews);
                teacherList.get(i).setRating(rating);
                teacherList.get(i).setNumberOfReviews(notes.size());
                teacherList.get(i).setMinPrice(minPrice);
                teacherList.get(i).getUser().setAvatarUrl(avatarUrl);
            }

            if (Objects.equals(user.getUserId(), teacherList.get(teacherList.size() - 1).getUser().getUserId())) {
                progressDialog.dismiss();
                displayTeacherListView();
            }
        }

    }

    @Override
    public void onClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), TeacherProfileActivity.class);
        intent.putExtra("teacher", teacherList.get(position));
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        page += 1;
        scrollPosition = teacherList.size() - 1;
        getSearchResults(query,currentOption, page);
        startProgressDialog();

    }
}
