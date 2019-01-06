package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.qwerteach.wivi.qwerteachapp.asyncTasks.EndlessRecyclerViewScrollListener;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.OptionAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Topic;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTeacherActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, TeacherAdapter.ISearcTeacher {

    ArrayList<User> teacherList;
    ArrayList<String> menuItems;
    RecyclerView teacherRecyclerView;
    RecyclerView.Adapter teacherAdapter;
    RecyclerView.LayoutManager teacherLayoutManager;
    Spinner searchTopicsSpinner;
    int topicId = 0;
    String topic;
    ProgressDialog progressDialog;
    QwerteachService service;
    User user;
    LinearLayout searchFilterLinearLayout;
    ActionBar actionBar;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        searchTopicsSpinner = (Spinner) findViewById(R.id.search_topics_spinner);
        teacherRecyclerView = (RecyclerView) findViewById(R.id.teacher_recycler_view);
        searchFilterLinearLayout = (LinearLayout) findViewById(R.id.search_filter_linear_layout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        teacherList = new ArrayList<>();
        menuItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);
        actionBar.setTitle("0 résultat(s)");

        displayTeacherListView();
        getQuery();
        getAllTopics();
        displaySearchTopicsSpinner();
    }

    public void getQuery() {
        teacherList.clear();
        scrollListener.resetState();
        topic = getIntent().getStringExtra("query");
        menuItems.add(topic);
        getTeachers(1);
    }

    public void getAllTopics() {
        Call<JsonResponse> call = service.getAllTopics(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    ArrayList<Topic> topics = response.body().getTopics();
                    for (int i = 0; i < topics.size(); i++) {
                        if (!topics.get(i).getTopicTitle().equals("Autre")) {
                            menuItems.add(topics.get(i).getTopicTitle());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
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
        searchTopicsSpinner.setSelection(topicId);
        searchTopicsSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
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
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) teacherLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getTeachers(page);
            }
        };
        teacherRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {

            case R.id.search_topics_spinner:
                topic = menuItems.get(i);
                if (topicId == i) {
                    return;
                } else {
                    teacherList.clear();
                    scrollListener.resetState();
                    getTeachers(1);
                }
                topicId = i;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getTeachers(int pageNumber) {
        startProgressDialog();
        Call<JsonResponse> call = service.getAllTeachers(topic, pageNumber, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    actionBar.setTitle(response.body().getUserListTotal() + " résultat(s)");
                    ArrayList<User> users = response.body().getUsers();
                    setTeachersList(users);
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTeachersList(ArrayList<User> users) {
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i) != null) {
                    getTeacherInfos(users.get(i).getUserId());
                }
            }
        } else {
            progressDialog.dismiss();
            teacherAdapter.notifyDataSetChanged();
        }
    }

    public void getTeacherInfos(int userId) {
        Call<JsonResponse> call = service.getUserInfos(userId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.body() != null) {
                    teacherList.add(response.body().getUser());
                    progressDialog.dismiss();
                    teacherAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
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

    public void onClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), TeacherProfileActivity.class);
        intent.putExtra("teacher", teacherList.get(position));
        intent.putExtra("query", topic);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        //getAllTeachers(page);

    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

}
