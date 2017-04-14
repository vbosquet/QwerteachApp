package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.UpdateLessonActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 15/12/16.
 */

public class MyLessonsListViewFragment extends Fragment {


    View view;
    String note, comment;
    ArrayList<Lesson> lessons;
    RecyclerView lessonRecyclerView;
    RecyclerView.Adapter lessonAdapter;
    RecyclerView.LayoutManager lessonLayoutManager;
    ProgressDialog progressDialog;
    int page = 1, scrollPosition = 0;
    QwerteachService service;
    User user;
    TextView noLessonsTitle;
    boolean loading = true;

    public static MyLessonsListViewFragment newInstance() {
        MyLessonsListViewFragment myLessonsListViewFragment = new MyLessonsListViewFragment();
        return myLessonsListViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_lessons_list_view, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        lessons = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        noLessonsTitle = (TextView) view.findViewById(R.id.no_lessons_title);
        lessonRecyclerView = (RecyclerView) view.findViewById(R.id.lesson_recycler_view);
        getLessons();

        return  view;
    }

    public void getLessons() {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessons(page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                ArrayList<Lesson> lessonList = response.body().getLessons();
                if (lessonList.size() > 0) {

                    for (int i = 0; i < lessonList.size(); i++) {
                        lessons.add(lessonList.get(i));
                    }

                    for (int i = 0; i < lessons.size(); i++) {
                        startGetLessonInfos(lessons.get(i).getLessonId(), i);
                    }

                } else {
                   progressDialog.dismiss();
                    noLessonsTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void startGetLessonInfos(final int lessonId, final int index) {
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
                lessons.get(index).setAvatar(response.body().getAvatar());
                lessons.get(index).setPayments(response.body().getPayments());

                if (lessonId == lessons.get(lessons.size() - 1).getLessonId()) {
                    loading = true;
                    progressDialog.dismiss();
                    displayLessonListView();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.toString());

            }
        });
    }

    public void displayLessonListView() {
        lessonAdapter = new LessonsAdapter(getContext(), lessons, this);
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
                            getMoreLessons();
                        }
                    }
                }
            }
        });

    }

    public void didTouchCancelLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.cancelLesson(lessons.get(index).getLessonId(), user.getEmail(), user.getToken());
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
        startProgressDialog();
        Call<JsonResponse> call = service.acceptLesson(lessons.get(index).getLessonId(), user.getEmail(), user.getToken());
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

    public void didTouchRefuseLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.refuseLesson(lessons.get(index).getLessonId(), user.getEmail(), user.getToken());
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

    public void didTouchPositiveReviewButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.payTeacher(lessons.get(index).getLessonId(), user.getEmail(), user.getToken());
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

    public void didTouchNegativeReviewButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.disputeLesson(lessons.get(index).getLessonId(), user.getEmail(), user.getToken());
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

    public void didTouchUpdateLessonButton(Lesson lesson) {
        Intent intent = new Intent(getContext(), UpdateLessonActivity.class);
        intent.putExtra("lesson", lesson);
        startActivityForResult(intent, 10003);
    }

    public void didTouchReviewButton(final int teacherId) {
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

    public void displayConfirmationMessage(Response<JsonResponse> response, int index) {
        String success = response.body().getSuccess();
        String message = response.body().getMessage();

        progressDialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        if (success.equals("true")) {
            Lesson lesson = response.body().getLesson();
            if (lesson != null) {
                lessons.get(index).setStatus(lesson.getStatus());
            }

            refreshFragment();
        }

    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void startCreateReview(final int index) {
        Review review = new Review();
        review.setReviewText(comment);
        review.setNote(Integer.valueOf(note));

        Map<String, Review> requestBody = new HashMap<>();
        requestBody.put("review", review);

        startProgressDialog();
        Call<JsonResponse> call = service.letReviewToTeacher(lessons.get(index).getTeacherId(), requestBody, user.getEmail(), user.getToken());
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

    public void refreshFragment() {
        page = 1;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void getMoreLessons() {
        loading = false;
        page += 1;
        scrollPosition = lessons.size() - 1;
        getLessons();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10003) && (resultCode == Activity.RESULT_OK)) {
            refreshFragment();
        }
    }
}
