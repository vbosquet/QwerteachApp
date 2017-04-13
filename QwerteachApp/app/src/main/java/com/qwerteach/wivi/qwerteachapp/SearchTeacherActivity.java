package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTeacherActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, TeacherAdapter.ISearcTeacher, View.OnTouchListener {

    ArrayList<Teacher> teacherList;
    ArrayList<String> menuItems, optionList;
    RecyclerView teacherRecyclerView;
    RecyclerView.Adapter teacherAdapter;
    RecyclerView.LayoutManager teacherLayoutManager;
    Spinner searchSortingOptionsSpinner, searchTopicsSpinner;
    int currentOptionId = 0, currentQueryId = 0, page = 1, scrollPosition = 0;
    String query, currentOption = "";
    ProgressDialog progressDialog;
    QwerteachService service;
    ArrayList<ArrayList<String>> searchOptions;
    User user;
    boolean loading = true, isPressed = true;
    ImageView chevronButton;
    LinearLayout searchFilterLinearLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        searchSortingOptionsSpinner = (Spinner) findViewById(R.id.search_sorting_options_spinner);
        searchTopicsSpinner = (Spinner) findViewById(R.id.search_topics_spinner);
        teacherRecyclerView = (RecyclerView) findViewById(R.id.teacher_recycler_view);
        chevronButton = (ImageView) findViewById(R.id.chevron_button);
        searchFilterLinearLayout = (LinearLayout) findViewById(R.id.search_filter_linear_layout);
        chevronButton.setOnTouchListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        query = getIntent().getStringExtra("query");
        teacherList = new ArrayList<>();
        menuItems = new ArrayList<>();
        optionList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);
        actionBar.setTitle("Résultats pour " + query);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String newQuery = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(newQuery);
        }

        menuItems.add(query);
        startSearchTeacher();
        getAllTopics();

    }

    public void doMySearch(String query) {
        finish();
        Intent intent = new Intent(this, SearchTeacherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("query", query);
        startActivity(intent);
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
        return true;
    }

    public void displaySearchTopicsSpinner() {
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
        searchTopicsSpinner.setAdapter(topicAdapter);
        searchTopicsSpinner.setSelection(currentQueryId);
        searchTopicsSpinner.setOnItemSelectedListener(this);

    }

    public void displaySearchSortingOptionsSpinner() {
        OptionAdapter searchOptionsAdapter = new OptionAdapter(this, android.R.layout.simple_spinner_item, optionList);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSortingOptionsSpinner.setAdapter(searchOptionsAdapter);
        searchSortingOptionsSpinner.setSelection(currentOptionId);
        searchSortingOptionsSpinner.setOnItemSelectedListener(this);
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
        teacherAdapter = new TeacherAdapter(this, teacherList, this);
        teacherRecyclerView.setHasFixedSize(true);
        teacherLayoutManager = new LinearLayoutManager(this);
        teacherRecyclerView.setLayoutManager(teacherLayoutManager);
        teacherRecyclerView.setItemAnimator(new DefaultItemAnimator());
        teacherRecyclerView.setAdapter(teacherAdapter);
        teacherRecyclerView.scrollToPosition(scrollPosition);
        teacherRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int total = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (loading) {
                    if (total > 0) {
                        if ((total - 1) == lastVisibleItem) {
                            getMoreTeachers();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.search_sorting_options_spinner:
                currentOption = searchOptions.get(i).get(1);
                if (currentOptionId == i) {
                    return;
                } else {
                    startSearchTeacher();
                }

                currentOptionId = i;
                break;
            case R.id.search_topics_spinner:
                query = menuItems.get(i);
                if (currentQueryId == i) {
                    return;
                } else {
                    page = 1;
                    startSearchTeacher();
                }
                currentQueryId = i;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startSearchTeacher() {
        scrollPosition = 0;
        teacherList.clear();
        getSearchResults(query, currentOption, 1);

    }

    public void getSearchResults(final String query, final String searchSortingOption, int pageNumber) {
        optionList.clear();
        startProgressDialog();
        Call<JsonResponse> call = service.getSearchResults(query, searchSortingOption, pageNumber, user.getEmail(), user.getToken());
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
                } else if (users.size() == 0 && page == 1) {
                    teacherList.clear();
                    progressDialog.dismiss();
                    actionBar.setTitle("Résultats pour " + query);
                    Toast.makeText(getApplication(), R.string.no_result_found_toast_message, Toast.LENGTH_SHORT).show();
                    displayTeacherListView();
                } else {
                    progressDialog.dismiss();
                }

                for (int i = 0; i < searchOptions.size(); i++) {
                    optionList.add(searchOptions.get(i).get(0));
                }

                displaySearchTopicsSpinner();
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
        Call<JsonResponse> call = service.getUserInfos(userId, user.getEmail(), user.getToken());
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
        List<String> reviewAvatars = response.body().getAvatars();

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
            reviews.get(i).setAvatar(reviewAvatars.get(i));
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
                loading = true;
                progressDialog.dismiss();
                actionBar.setTitle("Résultats pour " + query);
                displayTeacherListView();
            }
        }

    }

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

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    public void getMoreTeachers() {
        loading = false;
        page += 1;
        scrollPosition = teacherList.size() - 1;
        getSearchResults(query,currentOption, page);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPressed) {
                    chevronButton.setImageDrawable(getResources().getDrawable(R.drawable.chevron_up_icon));
                    searchFilterLinearLayout.setVisibility(View.VISIBLE);
                    isPressed = false;
                } else {
                    chevronButton.setImageDrawable(getResources().getDrawable(R.drawable.chevron_down_icon));
                    searchFilterLinearLayout.setVisibility(View.GONE);
                    isPressed = true;
                }
                break;
        }
        return true;
    }
}
