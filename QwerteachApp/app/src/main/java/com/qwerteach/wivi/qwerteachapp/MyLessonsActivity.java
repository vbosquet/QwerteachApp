package com.qwerteach.wivi.qwerteachapp;

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
import android.widget.ListView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetTopicAndTeacherInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.UpdateLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.CreateNewLessonFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.CreateVirtualWalletFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.MyLessonsListViewFragment;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyLessonsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lessons);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, MyLessonsListViewFragment.newInstance());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_lessons_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    getSupportFragmentManager().popBackStack();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
