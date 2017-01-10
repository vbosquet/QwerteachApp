package com.qwerteach.wivi.qwerteachapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.ReviewAdapter;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;

import java.util.ArrayList;

public class ReadCommentsActivity extends AppCompatActivity {

    ArrayList<Review> reviews;
    ListView commentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_comments);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        commentListView = (ListView) findViewById(R.id.comments_list_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reviews = (ArrayList<Review>) getIntent().getSerializableExtra("reviews");
        }

        displayCommentListView();
    }

    public void displayCommentListView() {
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);
        commentListView.setAdapter(reviewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_comments_menu, menu);
        return true;
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
}
