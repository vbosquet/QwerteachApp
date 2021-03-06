package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.MyLessonsActivity;
import com.qwerteach.wivi.qwerteachapp.NewVirtualWalletActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.SearchTeacherActivity;
import com.qwerteach.wivi.qwerteachapp.TeacherReviewActivity;
import com.qwerteach.wivi.qwerteachapp.VirtualWalletActivity;
import com.qwerteach.wivi.qwerteachapp.common.Common;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.ToReviewLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.ToUnlockLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 3/01/17.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {

    TextView pastLessonsReceivedTextView, pastLessonsGivenTextView,
            pendingLessonsTextView, totalWalletTextView, teacherStatsTextView, walletTextView;
    LinearLayout searchCard, pastLessonsGivenCard, pastLessonsReceivedCard,
            pendingLessonsCard, walletDetailsCard, teacherStatsCard;
    List<User> teachers;
    List<Lesson> toUnlockLessons, toReviewLessons;
    View view;
    ProgressDialog progressDialog;
    QwerteachService service;
    User currentUser;
    Intent intent;
    String query;
    RecyclerView toUnlockLessonRecyclerView, toReviewLessonRecyclerView;
    RecyclerView.Adapter lessonAdapter;
    RecyclerView.LayoutManager lessonLayoutManager;
    Integer totalWallet;

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
        currentUser = gson.fromJson(json, User.class);

        teachers = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pastLessonsReceivedTextView = (TextView) view.findViewById(R.id.past_lessons_received_text_view);
        pastLessonsGivenTextView = (TextView) view.findViewById(R.id.past_lessons_given_text_view);
        pendingLessonsTextView = (TextView) view.findViewById(R.id.pending_lessons_text_view);
        totalWalletTextView = (TextView) view.findViewById(R.id.total_wallet_text_view);
        pastLessonsReceivedCard = (LinearLayout) view.findViewById(R.id.past_lessons_received_card);
        pendingLessonsCard = (LinearLayout) view.findViewById(R.id.pending_lessons_card);
        walletDetailsCard = (LinearLayout) view.findViewById(R.id.wallet_details_card);
        searchCard = (LinearLayout) view.findViewById(R.id.search_card);
        pastLessonsGivenCard = (LinearLayout) view.findViewById(R.id.past_lessons_given_card);
        toUnlockLessonRecyclerView = (RecyclerView) view.findViewById(R.id.to_unlock_lessons_recycler_view);
        toReviewLessonRecyclerView = (RecyclerView) view.findViewById(R.id.to_review_lessons_recycler_view);
        teacherStatsCard = (LinearLayout) view.findViewById(R.id.teacher_stats_card);
        teacherStatsTextView = (TextView) view.findViewById(R.id.teacher_stats_text_view);
        walletTextView = (TextView) view.findViewById(R.id.wallet_text_view);

        pastLessonsGivenCard.setOnClickListener(this);
        pendingLessonsCard.setOnClickListener(this);
        walletDetailsCard.setOnClickListener(this);
        pastLessonsReceivedCard.setOnClickListener(this);
        searchCard.setOnClickListener(this);

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
        Call<JsonResponse> call = service.getDashboardInfos(currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if (response != null) {
                    List<Lesson> pendingLessons = response.body().getToDoList();
                    List<Lesson> pastLessons = response.body().getPastLessons();
                    List<Lesson> pastLessonsGiven = response.body().getPastLessonsGiven();
                    toUnlockLessons = response.body().getToUnlockLessons();
                    toReviewLessons = response.body().getToReviewLessons();
                    totalWallet = response.body().getTotalWallet();

                    pendingLessonsTextView.setText(pendingLessons.size() + " cours en attente");

                    if (currentUser.getPostulanceAccepted()) {
                        float sum = 0;
                        List<Integer> studentIds = new ArrayList();
                        for(int i = 0; i < pastLessonsGiven.size(); i++) {
                            sum += Float.parseFloat(pastLessonsGiven.get(i).getPrice());
                            studentIds.add(pastLessonsGiven.get(i).getStudentId());
                        }
                        pastLessonsGivenCard.setVisibility(View.VISIBLE);
                        teacherStatsCard.setVisibility(View.VISIBLE);
                        pastLessonsGivenTextView.setText(pastLessonsGiven.size() + " cours donné(s)");
                        teacherStatsTextView.setText(Html.fromHtml("<font color='#22de80'>" + pastLessonsGiven.size()
                                + "</font> cours donnés à <font color='#22de80'>" + Common.getUniqueElement(studentIds)
                                + "</font> élèves pour un total de <font color='#22de80'>" + sum + "€ <font color='#22de80'>"));
                    } else {
                        pastLessonsReceivedTextView.setText(pastLessons.size() + " cours suivi(s)");
                        searchCard.setVisibility(View.VISIBLE);
                        pastLessonsReceivedCard.setVisibility(View.VISIBLE);
                    }

                    if (totalWallet != null) {
                        totalWalletTextView.setText(totalWallet + "€");
                        walletTextView.setText("Voir");
                    } else {
                        totalWalletTextView.setText("O€");
                        walletTextView.setText("Configurer");
                    }

                    if (toUnlockLessons.size() > 0) {
                        toUnlockLessonRecyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < toUnlockLessons.size(); i++) {
                            getToUnlockLessonInfos(toUnlockLessons.get(i).getLessonId(), i);
                        }
                    }

                    if (toReviewLessons.size() > 0) {
                        toReviewLessonRecyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < toReviewLessons.size(); i++) {
                            getToReviewLessonInfo(toReviewLessons.get(i).getLessonId(), i);
                        }
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

    public void getToUnlockLessonInfos(int lessonId, final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessonInfos(lessonId, currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String username = response.body().getUserName();
                    String avatar = response.body().getAvatar();
                    String topicTitle = response.body().getTopicTitle();

                    toUnlockLessons.get(index).setAvatar(avatar);
                    toUnlockLessons.get(index).setUserName(username);
                    toUnlockLessons.get(index).setTopicTitle(topicTitle);

                    if (index == toUnlockLessons.size() - 1) {
                        displayToUnlockLessons();
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

    public void getToReviewLessonInfo(int lessonId, final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessonInfos(lessonId, currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String username = response.body().getUserName();
                    String avatar = response.body().getAvatar();

                    toReviewLessons.get(index).setAvatar(avatar);
                    toReviewLessons.get(index).setUserName(username);

                    if (index == toReviewLessons.size() - 1) {
                        displayToReviewLessons();
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

    public void displayToReviewLessons() {
        lessonAdapter = new ToReviewLessonsAdapter(toReviewLessons, getContext(), this);
        toReviewLessonRecyclerView.setHasFixedSize(true);
        lessonLayoutManager = new LinearLayoutManager(getContext());
        toReviewLessonRecyclerView.setLayoutManager(lessonLayoutManager);
        toReviewLessonRecyclerView.setItemAnimator(new DefaultItemAnimator());
        toReviewLessonRecyclerView.setAdapter(lessonAdapter);
    }

    public void displayToUnlockLessons() {
        lessonAdapter = new ToUnlockLessonsAdapter(toUnlockLessons, getContext(), this);
        toUnlockLessonRecyclerView.setHasFixedSize(true);
        lessonLayoutManager = new LinearLayoutManager(getContext());
        toUnlockLessonRecyclerView.setLayoutManager(lessonLayoutManager);
        toUnlockLessonRecyclerView.setItemAnimator(new DefaultItemAnimator());
        toUnlockLessonRecyclerView.setAdapter(lessonAdapter);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.past_lessons_received_card:
                intent = new Intent(getContext(), MyLessonsActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
                break;
            case R.id.past_lessons_given_card:
                intent = new Intent(getContext(), MyLessonsActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);
                break;
            case R.id.pending_lessons_card:
                intent = new Intent(getContext(), MyLessonsActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);
                break;
            case R.id.wallet_details_card:
                if (currentUser.getMangoId() != null) {
                    intent = new Intent(getContext(), VirtualWalletActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getContext(), NewVirtualWalletActivity.class);
                    intent.putExtra("status", 0);
                    startActivity(intent);
                }
                break;
            case R.id.search_card:
                doMySearch();
                break;
        }

    }

    public void didTouchPayTeacherButton(int lessonId, final int teacherId) {
        startProgressDialog();
        Call<JsonResponse> call = service.payTeacher(lessonId, currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String success = response.body().getSuccess();
                    String message = response.body().getMessage();
                    Boolean reviewNeeded = response.body().getReviewNeeded();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equals("true")) {
                        if (reviewNeeded) {
                            Intent intent = new Intent(getContext(), TeacherReviewActivity.class);
                            intent.putExtra("teacherId", teacherId);
                            startActivity(intent);
                        } else {
                            refreshFragment();
                        }
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

    public void didTouchLockPayment(int lessonId) {
        startProgressDialog();
        Call<JsonResponse> call = service.disputeLesson(lessonId, currentUser.getEmail(), currentUser.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String success = response.body().getSuccess();
                    String message = response.body().getMessage();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equals("true")) {
                        refreshFragment();
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

    public void didTouchReviewButton(final int teacherId) {
        Intent intent = new Intent(getContext(), TeacherReviewActivity.class);
        intent.putExtra("teacherId", teacherId);
        startActivity(intent);
    }
}
