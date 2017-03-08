package com.qwerteach.wivi.qwerteachapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.qwerteach.wivi.qwerteachapp.fragments.CreateNewLessonFragment;

public class LessonReservationActivity extends AppCompatActivity {

    private static final String TAG_CREATE_NEW_LESSON_FRAGMENT = "createNewLessonFragment";
    private CreateNewLessonFragment createNewLessonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_reservation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            createNewLessonFragment = CreateNewLessonFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, createNewLessonFragment, TAG_CREATE_NEW_LESSON_FRAGMENT).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson_reservation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
        }

        return super.onOptionsItemSelected(item);
    }
}

