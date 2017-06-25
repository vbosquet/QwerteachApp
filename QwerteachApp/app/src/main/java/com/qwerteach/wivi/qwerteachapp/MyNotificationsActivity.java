package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Notification;
import com.qwerteach.wivi.qwerteachapp.models.NotificationAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNotificationsActivity extends AppCompatActivity {

    User user;
    ProgressDialog progressDialog;
    QwerteachService service;
    List<Notification> notificationList;
    RecyclerView notificationRecyclerView;
    RecyclerView.Adapter notificationAdapter;
    RecyclerView.LayoutManager notificationLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(this);
        service = ApiClient.getClient().create(QwerteachService.class);
        notificationRecyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);
        getNotifications();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_notifications_menu, menu);
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

    public void getNotifications() {
        startProgressDialog();
        Call<JsonResponse> call = service.getNotifications(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                notificationList = response.body().getNotifications();
                for(int i = 0; i < notificationList.size(); i++) {
                    getNotificationInfos(notificationList.get(i).getSender_id(), i);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", toString());

            }
        });

    }

    public void getNotificationInfos(int senderId, final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.getNotificationInfos(senderId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                String avatar = response.body().getAvatar();
                notificationList.get(index).setAvatar(avatar);
                if (index == notificationList.size() - 1) {
                    dislayNotifications();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void seeLessonDetails() {
        Intent intent = new Intent(this, MyLessonsActivity.class);
        startActivity(intent);
    }

    public void dislayNotifications(){
        notificationAdapter = new NotificationAdapter(notificationList, this);
        notificationRecyclerView.setHasFixedSize(true);
        notificationLayoutManager = new LinearLayoutManager(this);
        notificationRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationRecyclerView.setAdapter(notificationAdapter);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
