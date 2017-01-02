package com.qwerteach.wivi.qwerteachapp;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayDashboardInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetLessonsInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.UpcomingLessonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements
        DisplayDashboardInfosAsyncTask.IGetDashboardInfos,
        GetLessonsInfosAsyncTask.IGetLessonInfos {

    String[] menuDrawerItems = {"Home", "Mes cours", "Mes messages", "Mon portefeuille", "Mon profil", "Devenir professeur"};
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mDrawerList, upcomingLessonListView;
    LinearLayout upcomingLessonLinearLayout;
    TextView upcomingLessonsTextView;
    CharSequence mDrawerTitle, mTitle;
    String email, token, userId;
    ArrayList<Lesson> upcomingLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        upcomingLessonListView = (ListView) findViewById(R.id.upcoming_lesson_list_view);
        upcomingLessonsTextView = (TextView) findViewById(R.id.upcoming_lesson_text_view);
        upcomingLessonLinearLayout = (LinearLayout) findViewById(R.id.upcoming_lesson_linear_layout);

        upcomingLessons = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        DisplayDashboardInfosAsyncTask displayDashboardInfosAsyncTask = new DisplayDashboardInfosAsyncTask(this);
        displayDashboardInfosAsyncTask.execute(email, token);

        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.menu_drawer_item_list_view, menuDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = mDrawerList.getItemAtPosition(position);
                String action = o.toString();

                if (action.equals(menuDrawerItems[0])) {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                }

                if(action.equals(menuDrawerItems[1])) {
                    Intent intent = new Intent(getApplicationContext(), MyLessonsActivity.class);
                    startActivity(intent);
                }

                if (action.equals(menuDrawerItems[2])) {
                    Intent intent = new Intent(getApplicationContext(), MyMessagesActivity.class);
                    startActivity(intent);
                }

                if (action.equals(menuDrawerItems[3])) {
                    Intent intent = new Intent(getApplicationContext(), VirtualWalletActivity.class);
                    startActivity(intent);
                }

                if(action.equals(menuDrawerItems[4])) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }

                if(action.equals(menuDrawerItems[5])) {
                    Intent intent = new Intent(getApplicationContext(), ToBecomeATeacherActivity.class);
                    startActivity(intent);
                }

            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(mDrawerTitle);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.sign_out_button).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.sign_out_button:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLogin", false);
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doMySearch(String query) {
        Intent intent = new Intent(this, SearchTeacherActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    public void displayDashboardInfos(String string) {

        Log.i("DASHBOARD", string);

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray upcomingLessonsJsonArray = jsonObject.getJSONArray("upcoming_lessons");
            JSONArray toDoListJsonArray = jsonObject.getJSONArray("to_do_list");

            for (int i = 0; i < upcomingLessonsJsonArray.length(); i++) {
                JSONObject jsonData = upcomingLessonsJsonArray.getJSONObject(i);
                int lessonId = jsonData.getInt("id");
                int studentId = jsonData.getInt("student_id");
                int teacherId = jsonData.getInt("teacher_id");
                int topicId =jsonData.getInt("topic_id");
                int topicGroupId = jsonData.getInt("topic_group_id");
                int levelId = jsonData.getInt("level_id");
                String status = jsonData.getString("status");
                String price = jsonData.getString("price");
                String timeStart = jsonData.getString("time_start");

                Lesson lesson = new Lesson(lessonId, studentId,
                        teacherId, topicId, topicGroupId, levelId, status, price, timeStart);
                upcomingLessons.add(lesson);
            }

            startGetLessonInfosAsyncTask();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startGetLessonInfosAsyncTask() {
        for (int i = 0; i < upcomingLessons.size(); i++) {
            int userToFind;

            if (String.valueOf(upcomingLessons.get(i).getTeacherId()).equals(userId)) {
                userToFind = upcomingLessons.get(i).getStudentId();
            } else {
                userToFind = upcomingLessons.get(i).getTeacherId();
            }

            int topicId = upcomingLessons.get(i).getTopicId();
            int topicGroupId = upcomingLessons.get(i).getTopicGroupId();
            int levelId = upcomingLessons.get(i).getLevelId();
            int lessonId = upcomingLessons.get(i).getLessonId();

            GetLessonsInfosAsyncTask getLessonsInfosAsyncTask = new GetLessonsInfosAsyncTask(this);
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind);
        }
    }

    @Override
    public void displayLessonInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");
            JSONObject durationJson = jsonObject.getJSONObject("duration");

            String firstName = userJson.getString("firstname");
            String topicTitle = jsonObject.getString("topic");
            int lessonId = jsonObject.getInt("lesson_id");
            int hours = durationJson.getInt("hours");
            int minutes = durationJson.getInt("minutes");

            for (int i = 0; i < upcomingLessons.size(); i++) {
                int id = upcomingLessons.get(i).getLessonId();

                if (id == lessonId) {
                    upcomingLessons.get(i).setUserFirstName(firstName);
                    upcomingLessons.get(i).setTopicTitle(topicTitle);
                    upcomingLessons.get(i).setDuration(hours, minutes);
                }

                if (lessonId == upcomingLessons.get(upcomingLessons.size() - 1).getLessonId()
                        && upcomingLessons.size() > 0) {
                    displayUpcomingLessonListView();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void displayUpcomingLessonListView() {
        upcomingLessonsTextView.setText(upcomingLessons.size() + " cours à venir");
        upcomingLessonLinearLayout.setVisibility(View.VISIBLE);

        UpcomingLessonAdapter upcomingLessonAdapter = new UpcomingLessonAdapter(this, upcomingLessons);
        upcomingLessonListView.setAdapter(upcomingLessonAdapter);
    }
}
