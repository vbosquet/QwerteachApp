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
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 15/12/16.
 */

public class MyLessonsListViewFragment extends Fragment implements GetAllMyLessonsAsyncTask.IGetAllLessons,
        CancelLessonAsyncTask.ICancelLesson,
        RefuseLessonAsyncTask.IRefuseLesson,
        AcceptLessonAsyncTask.IAcceptLesson,
        GetLessonsInfosAsyncTask.IGetLessonInfos,
        PayTeacherAsyncTack.IPayTeacher,
        DisputeAsyncTack.IDispute,
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
        lessonRecyclerView = (RecyclerView) view.findViewById(R.id.lesson_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        GetAllMyLessonsAsyncTask getAllMyLessonsAsyncTask = new GetAllMyLessonsAsyncTask(this);
        getAllMyLessonsAsyncTask.execute(email, token, page);
        startProgressDialog();

        return  view;
    }

    @Override
    public void getAllMyLessons(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("lessons");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int lessonId = jsonData.getInt("id");
                int studentId = jsonData.getInt("student_id");
                int teacherId = jsonData.getInt("teacher_id");
                int topicId = jsonData.getInt("topic_id");
                int topicGroupId = jsonData.getInt("topic_group_id");
                int levelId = jsonData.getInt("level_id");
                String status = jsonData.getString("status");
                String price = jsonData.getString("price");
                String timeStart = jsonData.getString("time_start");

                Lesson lesson = new Lesson(lessonId, studentId,
                        teacherId, topicId, topicGroupId, levelId, status, price, timeStart);
                lessons.add(lesson);
            }

            startGetLessonInfosAsyncTask();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startGetLessonInfosAsyncTask() {

        for (int i = 0; i < lessons.size(); i++) {
            int userToFind;
            boolean checkIfNeedReview;

            if (String.valueOf(lessons.get(i).getTeacherId()).equals(userId)) {
                userToFind = lessons.get(i).getStudentId();
                checkIfNeedReview = false;
            } else {
                userToFind = lessons.get(i).getTeacherId();
                checkIfNeedReview = true;
            }

            int topicId = lessons.get(i).getTopicId();
            int topicGroupId = lessons.get(i).getTopicGroupId();
            int levelId = lessons.get(i).getLevelId();
            int lessonId = lessons.get(i).getLessonId();

            GetLessonsInfosAsyncTask getLessonsInfosAsyncTask = new GetLessonsInfosAsyncTask(this);
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind, checkIfNeedReview);
        }
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

    public void didTouchCancelLessonButton(int lessonId) {
        CancelLessonAsyncTask cancelLessonAsyncTask = new CancelLessonAsyncTask(this);
        cancelLessonAsyncTask.execute(lessonId, email, token);
        startProgressDialog();
    }

    public void didTouchUpdateLessonButton(Lesson lesson) {
        Fragment newFragment = UpdateLessonFragment.newInstance(lesson);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void didTouchAcceptLessonButton(int lessonId) {
        AcceptLessonAsyncTask acceptLessonAsyncTask = new AcceptLessonAsyncTask(this);
        acceptLessonAsyncTask.execute(lessonId, email, token);
        startProgressDialog();
    }

    public void didTouchRefuseLessonButton(int lessonId) {
        RefuseLessonAsyncTask refuseLessonAsyncTask = new RefuseLessonAsyncTask(this);
        refuseLessonAsyncTask.execute(lessonId, email, token);
        startProgressDialog();
    }

    public void didTouchPositiveReviewButton(int lessonId) {
        PayTeacherAsyncTack payTeacherAsyncTack = new PayTeacherAsyncTack(this);
        payTeacherAsyncTack.execute(lessonId, email, token);
        startProgressDialog();
    }

    public void didTouchNegativeReviewButton(int lessonId) {
        DisputeAsyncTack disputeAsyncTack = new DisputeAsyncTack(this);
        disputeAsyncTack.execute(lessonId, email, token);
        startProgressDialog();
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

    @Override
    public void cancelConfirmationMessage(String string) {
        displayConfirmationMessage(string);
    }

    @Override
    public void refuseConfirmationMessage(String string) {
        displayConfirmationMessage(string);

    }

    @Override
    public void acceptConfirmationMessage(String string) {
        displayConfirmationMessage(string);
    }

    public void displayConfirmationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");
            String message = jsonObject.getString("message");

            if (success.equals("true")) {
                JSONObject lessonJson = jsonObject.getJSONObject("lesson");
                int lessonId = lessonJson.getInt("id");
                String status = lessonJson.getString("status");

                for (int i = 0; i < lessons.size(); i++) {
                    if (lessonId == lessons.get(i).getLessonId()) {
                        lessons.get(i).setStatus(status);
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                displayLessonListView();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayLessonInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject durationJson = jsonObject.getJSONObject("duration");
            JSONObject userJson = jsonObject.getJSONObject("user");

            boolean expired = jsonObject.getBoolean("expired");
            boolean past = jsonObject.getBoolean("past");
            boolean reviewNeed = jsonObject.getBoolean("review_needed");

            int lessonId = jsonObject.getInt("lesson_id");
            int hours = durationJson.getInt("hours");
            int minutes = durationJson.getInt("minutes");

            String topicTitle = jsonObject.getString("topic");
            String topicGroupTitle = jsonObject.getString("topic_group");
            String level = jsonObject.getString("level");
            String userFirstName = userJson.getString("firstname");
            String userLastName = userJson.getString("lastname");
            String paymentStatus = jsonObject.getString("payment_status");

            for (int i = 0; i < lessons.size(); i++) {
                int id = lessons.get(i).getLessonId();

                if (id == lessonId) {
                    lessons.get(i).setUserFirstName(userFirstName);
                    lessons.get(i).setUserLastName(userLastName);
                    lessons.get(i).setTopicTitle(topicTitle);
                    lessons.get(i).setTopicGroupTitle(topicGroupTitle);
                    lessons.get(i).setLevel(level);
                    lessons.get(i).setDuration(hours, minutes);
                    lessons.get(i).setPaymentStatus(paymentStatus);

                    if (expired) {
                        lessons.get(i).setStatus("expired");
                    } else if (past && lessons.get(i).getStatus().equals("created")) {
                        lessons.get(i).setStatus("past");
                    }

                    if (reviewNeed) {
                        lessons.get(i).setReviewNeeded(true);
                    } else {
                        lessons.get(i).setReviewNeeded(false);
                    }
                }


                if (lessonId == lessons.get(lessons.size() - 1).getLessonId()) {
                    progressDialog.dismiss();
                    displayLessonListView();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void payTeacherConfirmationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");
            String message = jsonObject.getString("message");

            progressDialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            if (success.equals("true")) {
                refreshFragment();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disputeConfirmationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");
            String message = jsonObject.getString("message");

            progressDialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            if (success.equals("true")) {
                refreshFragment();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void startCreateReviewAsyncTask(int teacherId) {
        CreateReviewAsyncTask createReviewAsyncTask = new CreateReviewAsyncTask(this);
        createReviewAsyncTask.execute(teacherId, email, token, comment, note);
        startProgressDialog();
    }

    public void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void createReviewConfirmationMessage(String string) {
        try {
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
        }
    }

    @Override
    public void onClick(View view) {
        page += 1;
        scrollPosition = lessons.size() - 1;
        GetAllMyLessonsAsyncTask getAllMyLessonsAsyncTask = new GetAllMyLessonsAsyncTask(this);
        getAllMyLessonsAsyncTask.execute(email, token, page);
        startProgressDialog();

    }
}
