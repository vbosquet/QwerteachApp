package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ShowLessonActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Payment;
import com.qwerteach.wivi.qwerteachapp.models.PendingLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.PlannedLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 15/05/17.
 */

public class PlannedLessonsTabFragment extends Fragment {

    View view;
    User user;
    ProgressDialog progressDialog;
    QwerteachService service;
    RecyclerView lessonRecyclerView;
    RecyclerView.Adapter lessonAdapter;
    RecyclerView.LayoutManager lessonLayoutManager;
    int scrollPosition, page = 1;
    boolean loading = true;
    List<Lesson> plannedLessons;
    TextView noLessonsTitle;

    public static PlannedLessonsTabFragment newInstance() {
        PlannedLessonsTabFragment plannedLessonsTabFragment = new PlannedLessonsTabFragment();
        return plannedLessonsTabFragment;
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
        getPlannedLessons();
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_planned_lessons_tab, container, false);
        lessonRecyclerView = (RecyclerView) view.findViewById(R.id.planned_lessons_recycler_view);
        noLessonsTitle = (TextView) view.findViewById(R.id.no_lessons_title);

        return view;
    }

    public void getPlannedLessons() {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessons(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    plannedLessons = response.body().getUpcomingLessons();
                    if (plannedLessons.size() > 0) {
                        for (int i = 0; i < plannedLessons.size(); i++) {
                            getPlannedLessonInfos(plannedLessons.get(i).getLessonId(), i);
                        }
                    } else {
                        noLessonsTitle.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPlannedLessonInfos(int lessonId, final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessonInfos(lessonId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String username = response.body().getUserName();
                    String avatar = response.body().getAvatar();
                    String topicTitle = response.body().getTopicTitle();
                    List<Payment> payments = response.body().getPayments();

                    plannedLessons.get(index).setAvatar(avatar);
                    plannedLessons.get(index).setUserName(username);
                    plannedLessons.get(index).setTopicTitle(topicTitle);
                    plannedLessons.get(index).setPayments(payments);

                    if (index == plannedLessons.size() - 1) {
                        displayPlannedLessons();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void displayPlannedLessons() {
        lessonAdapter = new PlannedLessonsAdapter(plannedLessons, getContext(), this);
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
                            scrollPosition = plannedLessons.size() - 1;
                            getMorePendingLessons();
                        }
                    }
                }
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

    public void getMorePendingLessons() {
        startProgressDialog();
        Call<JsonResponse> call = service.getMoreHistoryLessons("pending", page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    List<Lesson> newPendingLessons = response.body().getToDoList();
                    if (newPendingLessons.size() > 0) {
                        for (int i = 0; i < newPendingLessons.size(); i++) {
                            plannedLessons.add(newPendingLessons.get(i));
                        }

                        for (int i = scrollPosition; i < plannedLessons.size(); i++) {
                            getPlannedLessonInfos(plannedLessons.get(i).getLessonId(), i);
                        }

                        loading = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void seeLessonDetails(int index) {
        Lesson lesson = plannedLessons.get(index);
        String lessonType = "planned";
        Intent intent = new Intent(getContext(), ShowLessonActivity.class);
        intent.putExtra("lesson", lesson);
        intent.putExtra("lessonType", lessonType);
        startActivity(intent);
    }
}
