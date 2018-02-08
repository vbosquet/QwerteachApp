package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CheckInternetAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.HistoryLessonsTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.PendingLessonsTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.PlannedLessonsTabFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;

public class MyLessonsActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    int numItems = 3, position = 0;
    QwerteachService service;
    User user;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lessons);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        service = ApiClient.getClient().create(QwerteachService.class);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("position");
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), numItems));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(position);
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
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends FragmentPagerAdapter {

        private int numItems;

        public MyAdapter(FragmentManager fm, int numItems) {
            super(fm);
            this.numItems = numItems;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlannedLessonsTabFragment.newInstance();
                case 1:
                    return PendingLessonsTabFragment.newInstance();
                case 2:
                    return HistoryLessonsTabFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Cours prévus";
                case 1:
                    return "Cours à approuver";
                case 2:
                    return "Historique";
                default:
                    return null;
            }
        }
    }
}
