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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetTopicAndTeacherInfosAsyncTask;
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
        GetTopicAndTeacherInfosAsyncTask.IGetTopicAndTeacherInfos,
        CancelLessonAsyncTask.ICancelLesson {

    View view;
    String email, token;
    ArrayList<Lesson> lessons;
    ListView lessonListView;

    public static MyLessonsListViewFragment newInstance() {
        MyLessonsListViewFragment myLessonsListViewFragment= new MyLessonsListViewFragment();
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
        token =preferences.getString("token", "");

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
                int teacherId = jsonData.getInt("teacher_id");
                int topicId = jsonData.getInt("topic_id");
                int topicGroupId = jsonData.getInt("topic_group_id");
                int levelId = jsonData.getInt("level_id");
                String price = jsonData.getString("price");
                Lesson lesson = new Lesson(lessonId, teacherId, topicId, topicGroupId, levelId, price);
                lessons.add(lesson);
            }


            for (int i = 0; i < lessons.size(); i++) {
                GetTopicAndTeacherInfosAsyncTask getTopicAndTeacherInfosAsyncTask = new GetTopicAndTeacherInfosAsyncTask(this);
                getTopicAndTeacherInfosAsyncTask.execute(lessons.get(i).getTeacherId(), lessons.get(i).getTopicId(),
                        email, token, lessons.get(i).getLessonId());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTopicAndTeacherInfos(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject teacherJson = jsonObject.getJSONObject("teacher");
            JSONObject topicJson = jsonObject.getJSONObject("topic");
            JSONObject lessonJson = jsonObject.getJSONObject("lesson");
            JSONObject durationJson = lessonJson.getJSONObject("duration");

            String teacherFirstName = teacherJson.getString("firstname");
            String teacherLastName = teacherJson.getString("lastname");
            String topicTitle = topicJson.getString("title");
            String timeStart = lessonJson.getString("time_start");
            boolean isExpired = lessonJson.getBoolean("expired");
            boolean isCanceled = lessonJson.getBoolean("canceled");
            int lessonId = lessonJson.getInt("lesson_id");
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
                    lessons.get(i).setTeacherFirstName(teacherFirstName);
                    lessons.get(i).setTeacherLastName(teacherLastName);
                    lessons.get(i).setTopicTitle(topicTitle);
                    lessons.get(i).setDuration(duration);
                    lessons.get(i).setTimeStart(timeStart);
                    lessons.get(i).setExpired(isExpired);
                    lessons.get(i).setCanceled(isCanceled);
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

    @Override
    public void cancelConfirmationMessage(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                JSONObject lessonJson = jsonObject.getJSONObject("lesson");
                int lessonId = lessonJson.getInt("id");

                for (int i = 0; i < lessons.size(); i++) {
                    if (lessonId == lessons.get(i).getLessonId()) {
                        lessons.get(i).setCanceled(true);
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
