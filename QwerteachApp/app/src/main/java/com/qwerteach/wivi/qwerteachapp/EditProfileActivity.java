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
import com.qwerteach.wivi.qwerteachapp.fragments.AdTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.DescriptionTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.FormationsTabFragment;
import com.qwerteach.wivi.qwerteachapp.models.User;

public class EditProfileActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    int numItems = 0;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        if (user.getPostulanceAccepted()) {
            numItems = 3;
        } else numItems = 2;

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), numItems));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MyAdapter extends FragmentPagerAdapter {

        private int numItems;

        public MyAdapter(FragmentManager fm, int numItems) {
            super(fm);
            this.numItems = numItems;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return DescriptionTabFragment.newInstance();
                case 1:
                    return FormationsTabFragment.newInstance();
                case 2:
                    return AdTabFragment.newInstance();
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
                    return "Infos générales";
                case 1:
                    return "Formations";
                case 2:
                    return "Annonces";
                default:
                    return null;
            }
        }

    }

}