package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.AcceptLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisputeAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetLessonsInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.PayTeacherAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RefuseLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;

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
        DisputeAsyncTack.IDispute {

    View view;
    String email, token, userId;
    ArrayList<Lesson> lessons;
    ListView lessonListView;
    ProgressDialog progressDialog;

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
        lessonListView = (ListView) view.findViewById(R.id.lesson_list_view);
        progressDialog = new ProgressDialog(getContext());

        GetAllMyLessonsAsyncTask getAllMyLessonsAsyncTask = new GetAllMyLessonsAsyncTask(this);
        getAllMyLessonsAsyncTask.execute(email, token);
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
        LessonsAdapter lessonAdapter = new LessonsAdapter(getContext(), lessons, MyLessonsListViewFragment.this);
        lessonListView.setAdapter(lessonAdapter);

    }

    public void didTouchCancelLessonButton(int lessonId) {
        CancelLessonAsyncTask cancelLessonAsyncTask = new CancelLessonAsyncTask(this);
        cancelLessonAsyncTask.execute(lessonId, email, token);
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
    }

    public void didTouchRefuseLessonButton(int lessonId) {
        RefuseLessonAsyncTask refuseLessonAsyncTask = new RefuseLessonAsyncTask(this);
        refuseLessonAsyncTask.execute(lessonId, email, token);
    }

    public void didTouchPositiveReviewButton(int lessonId) {
        PayTeacherAsyncTack payTeacherAsyncTack = new PayTeacherAsyncTack(this);
        payTeacherAsyncTack.execute(lessonId, email, token);
    }

    public void didTouchNegativeReviewButton(int lessonId) {
        DisputeAsyncTack disputeAsyncTack = new DisputeAsyncTack(this);
        disputeAsyncTack.execute(lessonId, email, token);
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

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            if (success.equals("true")) {
                JSONObject lessonJson = jsonObject.getJSONObject("lesson");
                int lessonId = lessonJson.getInt("id");
                String status = lessonJson.getString("status");

                for (int i = 0; i < lessons.size(); i++) {
                    if (lessonId == lessons.get(i).getLessonId()) {
                        lessons.get(i).setStatus(status);
                    }
                }

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
                    displayLessonListView();
                    progressDialog.dismiss();
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

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            if (success.equals("true")) {
                displayLessonListView();
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

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

            if (success.equals("true")) {
                displayLessonListView();
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
}
