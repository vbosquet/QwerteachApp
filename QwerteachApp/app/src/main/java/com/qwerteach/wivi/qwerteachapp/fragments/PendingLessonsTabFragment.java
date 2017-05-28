package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.MyLessonsActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ShowLessonActivity;
import com.qwerteach.wivi.qwerteachapp.UpdateLessonActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.HistoryLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Payment;
import com.qwerteach.wivi.qwerteachapp.models.PendingLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 15/05/17.
 */

public class PendingLessonsTabFragment extends Fragment {

    View view;
    User user;
    ProgressDialog progressDialog;
    QwerteachService service;
    RecyclerView lessonRecyclerView;
    RecyclerView.Adapter lessonAdapter;
    RecyclerView.LayoutManager lessonLayoutManager;
    int scrollPosition, page = 1;
    boolean loading = true;
    List<Lesson> pendingLessons;

    public static PendingLessonsTabFragment newInstance() {
        PendingLessonsTabFragment pendingLessonsTabFragment = new PendingLessonsTabFragment();
        return pendingLessonsTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        getPendingLessons();
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_pending_lessons_tab, container, false);
        lessonRecyclerView = (RecyclerView) view.findViewById(R.id.pending_lessons_recycler_view);

        return view;
    }

    public void getPendingLessons() {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessons(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                pendingLessons = response.body().getToDoList();

                for (int i = 0; i < pendingLessons.size(); i++) {
                    getPendingLessonInfos(pendingLessons.get(i).getLessonId(), i);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void getPendingLessonInfos(int lessonId, final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessonInfos(lessonId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                String username = response.body().getUserName();
                String avatar = response.body().getAvatar();
                String topicTitle = response.body().getTopicTitle();
                List<Payment> payments = response.body().getPayments();

                pendingLessons.get(index).setAvatar(avatar);
                pendingLessons.get(index).setUserName(username);
                pendingLessons.get(index).setTopicTitle(topicTitle);
                pendingLessons.get(index).setPayments(payments);

                if (index == pendingLessons.size() - 1) {
                    displayPendingLessons();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void displayPendingLessons() {
        lessonAdapter = new PendingLessonsAdapter(pendingLessons, getContext(), user, this);
        lessonRecyclerView.setHasFixedSize(true);
        lessonLayoutManager = new LinearLayoutManager(getContext());
        lessonRecyclerView.setLayoutManager(lessonLayoutManager);
        lessonRecyclerView.setItemAnimator(new DefaultItemAnimator());
        lessonRecyclerView.setAdapter(lessonAdapter);
        lessonRecyclerView.scrollToPosition(scrollPosition);
        lessonRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int total = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (loading) {
                    if (total > 0) {
                        if ((total - 1) == lastVisibleItem) {
                            loading = false;
                            page += 1;
                            scrollPosition = pendingLessons.size() - 1;
                            getMorePendingLessons();
                        }
                    }
                }
            }
        });
    }

    public void getMorePendingLessons() {

        startProgressDialog();
        Call<JsonResponse> call = service.getMoreHistoryLessons("pending", page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                List<Lesson> newPendingLessons = response.body().getToDoList();
                if (newPendingLessons.size() > 0) {
                    for (int i = 0; i < newPendingLessons.size(); i++) {
                        pendingLessons.add(newPendingLessons.get(i));
                    }

                    for (int i = scrollPosition; i < pendingLessons.size(); i++) {
                        getPendingLessonInfos(pendingLessons.get(i).getLessonId(), i);
                    }

                    loading = true;
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void didTouchAcceptLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.acceptLesson(pendingLessons.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                if (response.body().getSuccess().equals("true")) {
                    Intent intent = new Intent(getContext(), DashboardActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchRefuseLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.refuseLesson(pendingLessons.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                if (response.body().getSuccess().equals("true")) {
                    Intent intent = new Intent(getContext(), MyLessonsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchUpdateLessonButton(Lesson lesson) {
        Intent intent = new Intent(getContext(), UpdateLessonActivity.class);
        intent.putExtra("lesson", lesson);
        startActivityForResult(intent, 10003);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10003) && (resultCode == Activity.RESULT_OK)) {
            page = 1;
            getPendingLessons();
        }
    }

    public void seeLessonDetails(int index) {
        Lesson lesson = pendingLessons.get(index);
        String lessonType = "pending";
        Intent intent = new Intent(getContext(), ShowLessonActivity.class);
        intent.putExtra("lesson", lesson);
        intent.putExtra("lessonType", lessonType);
        startActivity(intent);
    }
}
