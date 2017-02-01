package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.AcceptLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateReviewAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisputeAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetLessonsInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.PayTeacherAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RefuseLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 15/12/16.
 */

public class MyLessonsListViewFragment extends Fragment implements
        CreateReviewAsyncTask.ICreateReview,
        View.OnClickListener {

    View view;
    String email, token, userId, note, comment;
    ArrayList<Lesson> lessons;
    RecyclerView lessonRecyclerView;
    RecyclerView.Adapter lessonAdapter;
    RecyclerView.LayoutManager lessonLayoutManager;
    ProgressDialog progressDialog;
    FloatingActionButton floatingActionButton;
    int page = 1, scrollPosition = 0;
    QwerteachService service;

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
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        lessons = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);

        lessonRecyclerView = (RecyclerView) view.findViewById(R.id.lesson_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        getLessons();

        return  view;
    }

    public void getLessons() {
        startProgressDialog();
        Call<JsonResponse> call = service.getLessons(page, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                ArrayList<Lesson> lessonList = response.body().getLessons();

                for (int i = 0; i < lessonList.size(); i++) {
                    lessons.add(lessonList.get(i));
                }

                for (int i = 0; i < lessons.size(); i++) {
                    startGetLessonInfos(lessons.get(i).getLessonId(), i);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void startGetLessonInfos(final int lessonId, final int index) {
        Call<JsonResponse> call = service.getLessonInfos(lessonId, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                lessons.get(index).setUserName(response.body().getUserName());
                lessons.get(index).setTopicTitle(response.body().getTopicTitle());
                lessons.get(index).setTopicGroupTitle(response.body().getTopicGroupTitle());
                lessons.get(index).setLevel(response.body().getLevelTitle());
                lessons.get(index).setDuration(response.body().getDuration().getHours(), response.body().getDuration().getMinutes());
                lessons.get(index).setPaymentStatus(response.body().getPaymentStatus());
                lessons.get(index).setReviewNeeded(response.body().isReviewNeed());

                if (response.body().isExpired()) {
                    lessons.get(index).setStatus("expired");
                } else if (response.body().isPast()
                        && lessons.get(index).getStatus().equals("created")) {
                    lessons.get(index).setStatus("past");
                }

                if (lessonId == lessons.get(lessons.size() - 1).getLessonId()) {
                    progressDialog.dismiss();
                    displayLessonListView();
                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

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

    }

    public void didTouchCancelLessonButton(final int index) {
        startProgressDialog();
        Call<JsonResponse> call = service.cancelLesson(lessons.get(index).getLessonId(), email, token);
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
        Call<JsonResponse> call = service.acceptLesson(lessons.get(index).getLessonId(), email, token);
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
        Call<JsonResponse> call = service.refuseLesson(lessons.get(index).getLessonId(), email, token);
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
        Call<JsonResponse> call = service.payTeacher(lessons.get(index).getLessonId(), email, token);
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
        Call<JsonResponse> call = service.disputeLesson(lessons.get(index).getLessonId(), email, token);
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
        Fragment newFragment = UpdateLessonFragment.newInstance(lesson);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                startCreateReviewAsyncTask(teacherId);
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

            page = 1;
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

    public void startCreateReviewAsyncTask(final int index) {
        //CreateReviewAsyncTask createReviewAsyncTask = new CreateReviewAsyncTask(this);
        //createReviewAsyncTask.execute(teacherId, email, token, comment, note);

        Review review = new Review();
        review.setReviewText(comment);
        review.setNote(Integer.valueOf(note));

        Map<String, Review> requestBody = new HashMap<>();
        requestBody.put("review", review);

        startProgressDialog();
        Call<JsonResponse> call = service.letReviewToTeacher(lessons.get(index).getTeacherId(), requestBody, email, token);
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void createReviewConfirmationMessage(String string) {
        /*try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.create_review_positive_success_message, Toast.LENGTH_LONG).show();
                refreshFragment();

            } else {
                Toast.makeText(getContext(), R.string.create_review_negative_success_message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onClick(View view) {
        page += 1;
        scrollPosition = lessons.size() - 1;
        getLessons();

    }
}
