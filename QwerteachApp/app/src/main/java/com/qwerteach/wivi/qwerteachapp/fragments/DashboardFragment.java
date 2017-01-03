package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.SearchTeacherActivity;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.AcceptLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayDashboardInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetLessonsInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RefuseLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.ToDoListAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UpcomingLessonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 3/01/17.
 */

public class DashboardFragment extends Fragment implements DisplayDashboardInfosAsyncTask.IGetDashboardInfos,
        GetLessonsInfosAsyncTask.IGetLessonInfos,
        ToDoListAdapter.ILessonManagementButtons,
        AcceptLessonAsyncTask.IAcceptLesson,
        RefuseLessonAsyncTask.IRefuseLesson {

    LinearLayout upcomingLessonLinearLayout, toDoListLinearLayout;
    TextView upcomingLessonsTextView;
    String email, token, userId;
    ArrayList<Lesson> upcomingLessons, toDoList;
    View view;

    public static DashboardFragment newInstance() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        upcomingLessonsTextView = (TextView) view.findViewById(R.id.upcoming_lesson_text_view);
        upcomingLessonLinearLayout = (LinearLayout) view.findViewById(R.id.upcoming_lesson_linear_layout);
        toDoListLinearLayout = (LinearLayout) view.findViewById(R.id.to_do_list_linear_layout);

        upcomingLessons = new ArrayList<>();
        toDoList = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        DisplayDashboardInfosAsyncTask displayDashboardInfosAsyncTask = new DisplayDashboardInfosAsyncTask(this);
        displayDashboardInfosAsyncTask.execute(email, token);

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

        return  view;
    }

    public void doMySearch(String query) {
        Intent intent = new Intent(getContext(), SearchTeacherActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    public void displayDashboardInfos(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray upcomingLessonsJsonArray = jsonObject.getJSONArray("upcoming_lessons");
            JSONArray toDoListJsonArray = jsonObject.getJSONArray("to_do_list");

            if (upcomingLessonsJsonArray.length() > 0) {
                for (int i = 0; i < upcomingLessonsJsonArray.length(); i++) {
                    upcomingLessons.add(getNewLesson(upcomingLessonsJsonArray.getJSONObject(i)));
                }

                getUpcomingLessonInfos();
            }

            if (toDoListJsonArray.length() > 0) {
                for (int i = 0; i < toDoListJsonArray.length(); i++) {
                    toDoList.add(getNewLesson(toDoListJsonArray.getJSONObject(i)));
                }

                getToDoListInfos();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Lesson getNewLesson(JSONObject jsonData) {

        Lesson lesson = new Lesson();

        try {
            int lessonId = jsonData.getInt("id");
            int studentId = jsonData.getInt("student_id");
            int teacherId = jsonData.getInt("teacher_id");
            int topicId =jsonData.getInt("topic_id");
            int topicGroupId = jsonData.getInt("topic_group_id");
            int levelId = jsonData.getInt("level_id");
            String status = jsonData.getString("status");
            String price = jsonData.getString("price");
            String timeStart = jsonData.getString("time_start");

            lesson.setLessonId(lessonId);
            lesson.setStudentId(studentId);
            lesson.setTeacherId(teacherId);
            lesson.setTopicId(topicId);
            lesson.setTopicGroupId(topicGroupId);
            lesson.setLevelId(levelId);
            lesson.setStatus(status);
            lesson.setPrice(price);
            lesson.setTimeStart(timeStart);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  lesson;

    }

    public void getToDoListInfos() {
        for (int i = 0; i < toDoList.size(); i++) {
            int userToFind;

            if (String.valueOf(toDoList.get(i).getTeacherId()).equals(userId)) {
                userToFind = toDoList.get(i).getStudentId();
            } else {
                userToFind = toDoList.get(i).getTeacherId();
            }

            int topicId = toDoList.get(i).getTopicId();
            int topicGroupId = toDoList.get(i).getTopicGroupId();
            int levelId = toDoList.get(i).getLevelId();
            int lessonId = toDoList.get(i).getLessonId();

            GetLessonsInfosAsyncTask getLessonsInfosAsyncTask = new GetLessonsInfosAsyncTask(this);
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind);
        }
    }

    public void getUpcomingLessonInfos() {
        for (int i = 0; i < upcomingLessons.size(); i++) {
            int userToFind;

            if (String.valueOf(upcomingLessons.get(i).getTeacherId()).equals(userId)) {
                userToFind = upcomingLessons.get(i).getStudentId();
            } else {
                userToFind = upcomingLessons.get(i).getTeacherId();
            }

            int topicId = upcomingLessons.get(i).getTopicId();
            int topicGroupId = upcomingLessons.get(i).getTopicGroupId();
            int levelId = upcomingLessons.get(i).getLevelId();
            int lessonId = upcomingLessons.get(i).getLessonId();

            GetLessonsInfosAsyncTask getLessonsInfosAsyncTask = new GetLessonsInfosAsyncTask(this);
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind);
        }
    }

    @Override
    public void displayLessonInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");
            JSONObject durationJson = jsonObject.getJSONObject("duration");

            String firstName = userJson.getString("firstname");
            String lastName = userJson.getString("lastname");
            String topicTitle = jsonObject.getString("topic");
            String topicGroupTitle = jsonObject.getString("topic_group");
            String levelTitle = jsonObject.getString("level");
            int lessonId = jsonObject.getInt("lesson_id");
            int hours = durationJson.getInt("hours");
            int minutes = durationJson.getInt("minutes");
            boolean past = jsonObject.getBoolean("past");

            for (int i = 0; i < toDoList.size(); i++) {
                int id = toDoList.get(i).getLessonId();

                if (id == lessonId) {
                    toDoList.get(i).setUserFirstName(firstName);
                    toDoList.get(i).setUserLastName(lastName);
                    toDoList.get(i).setTopicTitle(topicTitle);
                    toDoList.get(i).setTopicGroupTitle(topicGroupTitle);
                    toDoList.get(i).setLevel(levelTitle);
                    toDoList.get(i).setDuration(hours, minutes);

                    if (past && toDoList.get(i).getStatus().equals("created")) {
                        toDoList.get(i).setStatus("past");
                    }
                }

                if (lessonId == toDoList.get(toDoList.size() - 1).getLessonId()) {
                    displayToDoListView(i);
                }

            }

            for (int i = 0; i < upcomingLessons.size(); i++) {
                int id = upcomingLessons.get(i).getLessonId();

                if (id == lessonId) {
                    upcomingLessons.get(i).setUserFirstName(firstName);
                    upcomingLessons.get(i).setUserLastName(lastName);
                    upcomingLessons.get(i).setTopicTitle(topicTitle);
                    upcomingLessons.get(i).setDuration(hours, minutes);
                }

                if (lessonId == upcomingLessons.get(upcomingLessons.size() - 1).getLessonId()) {
                    displayUpcomingLessonListView(i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayToDoListView(int i) {
        toDoListLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout toDoListView = (LinearLayout) view.findViewById(R.id.to_do_list);
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(getContext(), toDoList);
        toDoListAdapter.setCallback(this);
        View view = toDoListAdapter.getView(i, null, null);
        toDoListView.addView(view);

    }

    public void displayUpcomingLessonListView(int i) {
        upcomingLessonsTextView.setText(upcomingLessons.size() + " cours à venir");
        upcomingLessonLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout upcomingLessonView = (LinearLayout) view.findViewById(R.id.upcoming_lesson);
        UpcomingLessonAdapter upcomingLessonAdapter = new UpcomingLessonAdapter(getContext(), upcomingLessons);
        View view = upcomingLessonAdapter.getView(i, null, null);
        upcomingLessonView.addView(view);
    }

    @Override
    public void didTouchRefuseLessonButton(int lessonId) {
        RefuseLessonAsyncTask refuseLessonAsyncTask = new RefuseLessonAsyncTask(this);
        refuseLessonAsyncTask.execute(lessonId, email, token);
    }

    @Override
    public void didTouchAcceptLessonButton(int lessonId) {
        AcceptLessonAsyncTask acceptLessonAsyncTask = new AcceptLessonAsyncTask(this);
        acceptLessonAsyncTask.execute(lessonId, email, token);
    }

    @Override
    public void didTouchUpdateLessonButton(Lesson lesson) {
        Fragment newFragment = UpdateLessonFragment.newInstance(lesson);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void acceptConfirmationMessage(String string) {
        displayConfirmationMessage(string);
    }

    @Override
    public void refuseConfirmationMessage(String string) {
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

                for (int i = 0; i < toDoList.size(); i++) {
                    if (lessonId == toDoList.get(i).getLessonId()) {
                        toDoList.get(i).setStatus(status);
                    }
                }
            }

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), DashboardActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
