package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.qwerteach.wivi.qwerteachapp.MyLessonsActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.SearchTeacherActivity;
import com.qwerteach.wivi.qwerteachapp.VirtualWalletActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 3/01/17.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {

    TextView upcomingLessonsTextView, pastLessonsTextView, toDoListTextView, totalWalletTextView,
            upcomingLessonsButton, pastLessonsButton, toDoListButton, totalWalletButton, allTeachersButton;
    List<Teacher> teachers;
    View view;
    ProgressDialog progressDialog;
    QwerteachService service;
    User user;
    Intent intent;
    String query;

    public static DashboardFragment newInstance() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        teachers = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        setUpPusherNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        upcomingLessonsTextView = (TextView) view.findViewById(R.id.upcoming_lessons_text_view);
        pastLessonsTextView = (TextView) view.findViewById(R.id.past_lessons_text_view);
        toDoListTextView = (TextView) view.findViewById(R.id.to_do_list_text_view);
        totalWalletTextView = (TextView) view.findViewById(R.id.total_wallet_text_view);
        upcomingLessonsButton = (TextView) view.findViewById(R.id.upcoming_lessons_button);
        pastLessonsButton = (TextView) view.findViewById(R.id.past_lessons_button);
        toDoListButton = (TextView) view.findViewById(R.id.to_do_list_button);
        totalWalletButton = (TextView) view.findViewById(R.id.total_wallet_button);
        allTeachersButton = (TextView) view.findViewById(R.id.all_teachers_button);

        pastLessonsButton.setOnClickListener(this);
        toDoListButton.setOnClickListener(this);
        totalWalletButton.setOnClickListener(this);
        upcomingLessonsButton.setOnClickListener(this);
        allTeachersButton.setOnClickListener(this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch();
        }

        getDashboardInfos();

        return  view;
    }

    public void doMySearch() {
        Intent intent = new Intent(getContext(), SearchTeacherActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    public void getDashboardInfos() {
        startProgressDialog();
        Call<JsonResponse> call = service.getDashboardInfos(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                List<Lesson> toDoList = response.body().getToDoList();
                List<Lesson> upcomingLessons = response.body().getUpcomingLessons();
                List<Lesson> pastLessons = response.body().getPastLessons();
                Integer totalWallet = response.body().getTotalWallet();

                progressDialog.dismiss();
                upcomingLessonsTextView.setText(upcomingLessons.size() + " cours à venir");
                toDoListTextView.setText(toDoList.size() + " cours en attente");

                if (user.getPostulanceAccepted()) {
                    pastLessonsTextView.setText(pastLessons.size() + " cours donné(s)");
                } else {
                    pastLessonsTextView.setText(pastLessons.size() + " cours suivi(s)");
                }

                if (totalWallet != null) {
                    totalWalletTextView.setText(totalWallet + "€");
                } else {
                    totalWalletButton.setText("Configurer mon portefeuille");
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.getMessage());
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

    public void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void setUpPusherNotification() {
        try {
            PusherAndroid pusher = new PusherAndroid("1e87927ec5fb91180bb0");
            PushNotificationRegistration nativePusher = pusher.nativePusher();
            nativePusher.subscribe(String.valueOf(user.getUserId()));
            nativePusher.registerFCM(getContext());

            nativePusher.setFCMListener(new FCMPushNotificationReceivedListener() {
                @Override
                public void onMessageReceived(RemoteMessage remoteMessage) {
                }
            });

        } catch (ManifestValidator.InvalidManifestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10004) && (resultCode == Activity.RESULT_OK)) {
            refreshFragment();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.upcoming_lessons_button:
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
                intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
                startActivity(intent);
                break;
            case R.id.past_lessons_button:
                intent = new Intent(getContext(), MyLessonsActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
                break;
            case R.id.to_do_list_button:
                intent = new Intent(getContext(), MyLessonsActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);
                break;
            case R.id.total_wallet_button:
                intent = new Intent(getContext(), VirtualWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.all_teachers_button:
                doMySearch();
                break;
        }

    }
}
