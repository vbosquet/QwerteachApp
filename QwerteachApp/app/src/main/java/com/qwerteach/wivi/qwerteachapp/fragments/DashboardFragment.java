package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.SearchTeacherActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.TeacherToReviewAdapter;
import com.qwerteach.wivi.qwerteachapp.models.ToDoListAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UpcomingLessonAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 3/01/17.
 */

public class DashboardFragment extends Fragment {

    LinearLayout upcomingLessonLinearLayout, toDoListLinearLayout;
    TextView upcomingLessonsTextView;
    String note, comment;
    List<Lesson> upcomingLessons, toDoList;
    List<User> teachersToReview;
    View view;
    ProgressDialog progressDialog;
    QwerteachService service;
    RecyclerView toDoListRecyclerView;
    RecyclerView.Adapter toDoListAdapter;
    RecyclerView.LayoutManager toDoListLayoutManager;
    RecyclerView teacherToReviewRecyclerView;
    RecyclerView.Adapter teacherToReviewAdapter;
    RecyclerView.LayoutManager teacherToReviewLayoutManager;
    User user;

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

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        setUpPusherNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        upcomingLessonsTextView = (TextView) view.findViewById(R.id.upcoming_lesson_text_view);
        upcomingLessonLinearLayout = (LinearLayout) view.findViewById(R.id.upcoming_lesson_linear_layout);
        toDoListLinearLayout = (LinearLayout) view.findViewById(R.id.to_do_list_linear_layout);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        getDashboardInfos();

        return  view;
    }

    public void doMySearch(String query) {
        Intent intent = new Intent(getContext(), SearchTeacherActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    public void getDashboardInfos() {
        Call<JsonResponse> call = service.getDashboardInfos(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                toDoList = response.body().getToDoList();
                upcomingLessons = response.body().getUpcomingLesson();
                teachersToReview = response.body().getTeachersToReview();

                if (teachersToReview.size() > 0) {
                    displayTeachersToReviewListView();
                }

                if (upcomingLessons.size() > 0) {
                    for (int i = 0; i < upcomingLessons.size(); i++) {
                        getLessonInfos(upcomingLessons.get(i).getLessonId(), i, upcomingLessons);
                    }
                }

                if (toDoList.size() > 0) {
                    for (int i = 0; i < toDoList.size(); i++) {
                        getLessonInfos(toDoList.get(i).getLessonId(), i, toDoList);
                    }
                }

                if (teachersToReview.size() > 0  || toDoList.size() > 0) {
                    toDoListLinearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void getLessonInfos(final int lessonId, final int index, final List<Lesson> lessons) {
        Call<JsonResponse> call = service.getLessonInfos(lessonId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                lessons.get(index).setUserName(response.body().getUserName());
                lessons.get(index).setTopicTitle(response.body().getTopicTitle());
                lessons.get(index).setTopicGroupTitle(response.body().getTopicGroupTitle());
                lessons.get(index).setLevel(response.body().getLevelTitle());
                lessons.get(index).setDuration(response.body().getDuration().getHours(), response.body().getDuration().getMinutes());
                lessons.get(index).setStatus(response.body().getLessonStatus());

                if (toDoList.size() > 0) {
                    if (lessonId == toDoList.get(toDoList.size() - 1).getLessonId()) {
                        displayToDoListView();
                    }
                }


                if (upcomingLessons.size() > 0) {
                    if (lessonId == upcomingLessons.get(upcomingLessons.size() - 1).getLessonId()) {
                        displayUpcomingLessonListView();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void displayToDoListView() {
        toDoListRecyclerView = (RecyclerView) view.findViewById(R.id.to_do_list);
        toDoListRecyclerView.setVisibility(View.VISIBLE);
        toDoListAdapter = new ToDoListAdapter(toDoList, this);
        toDoListLayoutManager = new LinearLayoutManager(getContext());
        toDoListRecyclerView.setLayoutManager(toDoListLayoutManager);
        toDoListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        toDoListRecyclerView.setAdapter(toDoListAdapter);

    }

    public void displayUpcomingLessonListView() {
        upcomingLessonsTextView.setText(upcomingLessons.size() + " cours à venir");
        upcomingLessonLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout upcomingLessonView = (LinearLayout) view.findViewById(R.id.upcoming_lesson);

        for (int i = 0; i < upcomingLessons.size(); i++) {
            UpcomingLessonAdapter upcomingLessonAdapter = new UpcomingLessonAdapter(getContext(), upcomingLessons);
            View view = upcomingLessonAdapter.getView(i, null, null);
            upcomingLessonView.addView(view);
        }
    }

    public void displayTeachersToReviewListView() {
        teacherToReviewRecyclerView = (RecyclerView) view.findViewById(R.id.teacher_to_review);
        teacherToReviewRecyclerView.setVisibility(View.VISIBLE);
        teacherToReviewAdapter = new TeacherToReviewAdapter(teachersToReview, this);
        teacherToReviewLayoutManager = new LinearLayoutManager(getContext());
        teacherToReviewRecyclerView.setLayoutManager(teacherToReviewLayoutManager);
        teacherToReviewRecyclerView.setItemAnimator(new DefaultItemAnimator());
        teacherToReviewRecyclerView.setAdapter(teacherToReviewAdapter);
    }

    public void didTouchRefuseLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.refuseLesson(toDoList.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response, index);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchAcceptLessonButton(final int index) {
        Call<JsonResponse> call = service.acceptLesson(toDoList.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response, index);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
        startProgressDialog();
    }

    public void didTouchUpdateLessonButton(Lesson lesson) {
        Fragment newFragment = UpdateLessonFragment.newInstance(lesson);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void didTouchPositiveFeedBackButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.payTeacher(toDoList.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response, index);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void didTouchNegativeFeedBackButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.disputeLesson(toDoList.get(index).getLessonId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response, index);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void displayConfirmationMessage(Response<JsonResponse> response, int index) {
        String success = response.body().getSuccess();
        String message = response.body().getMessage();

        progressDialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        if (success.equals("true")) {
            Lesson lesson = response.body().getLesson();
            if (lesson != null) {
                toDoList.get(index).setStatus(lesson.getStatus());
            }


            refreshFragment();
        }

    }

    public void didTouchTeacherReviewButton(final int teacherId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_teacher_review, null);
        final EditText commentEditText = (EditText) dialogView.findViewById(R.id.comment_edit_text);
        final Spinner noteSpinner = (Spinner) dialogView.findViewById(R.id.note_spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.note_spinner_item, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noteSpinner.setAdapter(adapter);
        noteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                note = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(dialogView);
        builder.setTitle(R.string.teacher_review_dialog_title);
        builder.setPositiveButton(R.string.teacher_review_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                comment = commentEditText.getText().toString();
                startCreateReview(teacherId);
            }
        });

        builder.setNegativeButton(R.string.teacher_review_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();
    }

    public void startCreateReview(final int index) {
        Review review = new Review();
        review.setReviewText(comment);
        review.setNote(Integer.valueOf(note));

        Map<String, Review> requestBody = new HashMap<>();
        requestBody.put("review", review);

        startProgressDialog();
        Call<JsonResponse> call = service.letReviewToTeacher(teachersToReview.get(index).getUserId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                displayConfirmationMessage(response, index);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

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
            nativePusher.subscribe("qwerteach");
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
}
