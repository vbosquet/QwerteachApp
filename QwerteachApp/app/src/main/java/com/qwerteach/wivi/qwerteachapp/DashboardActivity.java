package com.qwerteach.wivi.qwerteachapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CheckInternetAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.DashboardFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    String[] menuDrawerItems = {"Mes cours", "Mon portefeuille", "Mon profil", "Déconnexion"};
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mDrawerList;
    CharSequence mDrawerTitle, mTitle;
    Intent intent;
    User user;
    QwerteachService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);
        service = ApiClient.getClient().create(QwerteachService.class);

        if(isOnline()) {
            setUpPusherNotification();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, DashboardFragment.newInstance());
        transaction.commit();

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.menu_drawer_item_list_view, menuDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object o = mDrawerList.getItemAtPosition(position);
                String action = o.toString();

                if(action.equals(menuDrawerItems[0])) {
                    Intent intent = new Intent(getApplicationContext(), MyLessonsActivity.class);
                    startActivity(intent);
                }

                if (action.equals(menuDrawerItems[1])) {
                    if (user.getMangoId() != null) {
                        Intent intent = new Intent(getApplicationContext(), VirtualWalletActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NewVirtualWalletActivity.class);
                        intent.putExtra("status", 0);
                        startActivity(intent);
                    }
                }

                if(action.equals(menuDrawerItems[2])) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }

                if (action.equals(menuDrawerItems[3])) {
                    Call<JsonResponse> call = service.deleteSession(user.getEmail(), user.getToken());
                    call.enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            if (response.isSuccessful()) {
                                String success = response.body().getSuccess();
                                if (Objects.equals(success, "true")) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isLogin", false);
                                    editor.apply();

                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setElevation(0);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.email_button:
                intent = new Intent(this, MyMessagesActivity.class);
                startActivity(intent);
                return true;

            case R.id.notification_button:
                intent = new Intent(this, MyNotificationsActivity.class);
                startActivity(intent);
                break;

            case android.R.id.home:
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    getSupportFragmentManager().popBackStack();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    public void setUpPusherNotification() {
        try {
            PusherAndroid pusher = new PusherAndroid("1e87927ec5fb91180bb0");
            PushNotificationRegistration nativePusher = pusher.nativePusher();
            nativePusher.subscribe(String.valueOf(user.getUserId()));
            nativePusher.registerFCM(this);

            nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
                @Override
                public void onMessageReceived(RemoteMessage remoteMessage) {
                }
            });

        } catch (ManifestValidator.InvalidManifestException e) {
            e.printStackTrace();
        }
    }

}
