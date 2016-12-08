package com.qwerteach.wivi.qwerteachapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.CancelLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMyLessonsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetTopicAndTeacherInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyLessonsActivity extends AppCompatActivity implements GetAllMyLessonsAsyncTask.IGetAllLessons,
        GetTopicAndTeacherInfosAsyncTask.IGetTopicAndTeacherInfos,
        CancelLessonAsyncTask.ICancelLesson {

    String email, token;
    ArrayList<Lesson> lessons;
    ListView lessonListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lessons);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token =preferences.getString("token", "");

        lessons = new ArrayList<>();
        lessonListView = (ListView) findViewById(R.id.lesson_list_view);

        GetAllMyLessonsAsyncTask getAllMyLessonsAsyncTask = new GetAllMyLessonsAsyncTask(this);
        getAllMyLessonsAsyncTask.execute(email, token);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_lessons_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                String price = jsonData.getString("price");
                Lesson lesson = new Lesson(lessonId, teacherId, topicId, price);
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
        LessonsAdapter lessonAdapter = new LessonsAdapter(this, lessons);
        lessonListView.setAdapter(lessonAdapter);

    }

    public void didTouchCancelLessonButton(View view) {
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        int lessonId = lessons.get(position).getLessonId();

        CancelLessonAsyncTask cancelLessonAsyncTask = new CancelLessonAsyncTask(this);
        cancelLessonAsyncTask.execute(lessonId, email, token);
    }

    @Override
    public void cancelConfirmationMessage(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject lessonJson = jsonObject.getJSONObject("lesson");
            String message = jsonObject.getString("message");
            String success = jsonObject.getString("success");
            int lessonId = lessonJson.getInt("id");

            if (success.equals("true")) {
                for (int i = 0; i < lessons.size(); i++) {
                    if (lessonId == lessons.get(i).getLessonId()) {
                        lessons.get(i).setCanceled(true);
                    }
                }

                displayLessonListView();
            }


            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
