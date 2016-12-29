package com.qwerteach.wivi.qwerteachapp.fragments;

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

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.AcceptLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetTopicAndUserInfosAsyncTask;
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
        GetTopicAndUserInfosAsyncTask.IGetTopicAndUserInfos,
        CancelLessonAsyncTask.ICancelLesson,
        RefuseLessonAsyncTask.IRefuseLesson,
        AcceptLessonAsyncTask.IAcceptLesson {

    View view;
    String email, token, userId;
    ArrayList<Lesson> lessons;
    ListView lessonListView;

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

        GetAllMyLessonsAsyncTask getAllMyLessonsAsyncTask = new GetAllMyLessonsAsyncTask(this);
        getAllMyLessonsAsyncTask.execute(email, token);

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
                Lesson lesson = new Lesson(lessonId, studentId, teacherId, topicId, topicGroupId, levelId, status, price);
                lessons.add(lesson);
            }


            for (int i = 0; i < lessons.size(); i++) {
                int userToFind;

                if (String.valueOf(lessons.get(i).getTeacherId()).equals(userId)) {
                    userToFind = lessons.get(i).getStudentId();
                } else {
                    userToFind = lessons.get(i).getTeacherId();
                }

                GetTopicAndUserInfosAsyncTask getTopicAndUserInfosAsyncTask = new GetTopicAndUserInfosAsyncTask(this);
                getTopicAndUserInfosAsyncTask.execute(userToFind, lessons.get(i).getTopicId(),
                        email, token, lessons.get(i).getLessonId());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTopicAndUserInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");
            JSONObject topicJson = jsonObject.getJSONObject("topic");
            JSONObject lessonJson = jsonObject.getJSONObject("lesson");
            JSONObject durationJson = jsonObject.getJSONObject("duration");
            String timeStart = jsonObject.getString("time_start");
            boolean expired = jsonObject.getBoolean("expired");

            String userFirstName = userJson.getString("firstname");
            String userLastName = userJson.getString("lastname");
            String topicTitle = topicJson.getString("title");
            String status = lessonJson.getString("status");
            int lessonId = lessonJson.getInt("id");
            int hours = durationJson.getInt("hours");
            int minutes = durationJson.getInt("minutes");
            String duration;

            if (minutes == 0) {
                duration = hours + "h";
            } else {
                duration = hours + "h" + minutes;
            }

            for (int i = 0; i < lessons.size(); i++) {
                int id = lessons.get(i).getLessonId();

                if (id == lessonId) {
                    lessons.get(i).setUserFirstName(userFirstName);
                    lessons.get(i).setUserLastName(userLastName);
                    lessons.get(i).setTopicTitle(topicTitle);
                    lessons.get(i).setDuration(duration);
                    lessons.get(i).setTimeStart(timeStart);

                    if (expired) {
                        lessons.get(i).setStatus("expired");
                    } else {
                        lessons.get(i).setStatus(status);
                    }
                }


                if (lessonId == lessons.get(lessons.size() - 1).getLessonId()) {
                    displayLessonListView();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
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

                displayLessonListView();
            }

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
