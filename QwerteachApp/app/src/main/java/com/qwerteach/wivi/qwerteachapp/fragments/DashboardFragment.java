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

import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.SearchTeacherActivity;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.AcceptLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateReviewAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayDashboardInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisputeAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetLessonsInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.PayTeacherAsyncTack;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.RefuseLessonAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.TeacherToReviewAdapter;
import com.qwerteach.wivi.qwerteachapp.models.ToDoListAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UpcomingLessonAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

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
        RefuseLessonAsyncTask.IRefuseLesson,
        PayTeacherAsyncTack.IPayTeacher,
        DisputeAsyncTack.IDispute,
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile,
        CreateReviewAsyncTask.ICreateReview {

    LinearLayout upcomingLessonLinearLayout, toDoListLinearLayout;
    TextView upcomingLessonsTextView;
    String email, token, userId, note, comment;
    ArrayList<Lesson> upcomingLessons, toDoList;
    ArrayList<Teacher> teachersToReview;
    View view;
    ProgressDialog progressDialog;

    public static DashboardFragment newInstance() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        upcomingLessonsTextView = (TextView) view.findViewById(R.id.upcoming_lesson_text_view);
        upcomingLessonLinearLayout = (LinearLayout) view.findViewById(R.id.upcoming_lesson_linear_layout);
        toDoListLinearLayout = (LinearLayout) view.findViewById(R.id.to_do_list_linear_layout);

        upcomingLessons = new ArrayList<>();
        toDoList = new ArrayList<>();
        teachersToReview = new ArrayList<>();

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

        DisplayDashboardInfosAsyncTask displayDashboardInfosAsyncTask = new DisplayDashboardInfosAsyncTask(this);
        displayDashboardInfosAsyncTask.execute(email, token);
        startProgressDialog();

        return  view;
    }

    public void doMySearch(String query) {
        Intent intent = new Intent(getContext(), SearchTeacherActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    public void displayDashboardInfos(String string) {

        Log.i("DASHBOARD", string);

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray upcomingLessonsJsonArray = jsonObject.getJSONArray("upcoming_lessons");
            JSONArray reviewAskedJsonArray = jsonObject.getJSONArray("review_asked");
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

            if (reviewAskedJsonArray.length() > 0) {
                for (int i = 0; i < reviewAskedJsonArray.length(); i++) {
                    int teacherId = (int) reviewAskedJsonArray.get(i);
                    User user = new User();
                    user.setUserId(teacherId);
                    Teacher teacher = new Teacher();
                    teacher.setUser(user);
                    teachersToReview.add(teacher);
                }

                getTeachersInfos();
            }

            if (reviewAskedJsonArray.length() > 0  || toDoListJsonArray.length() > 0) {
                toDoListLinearLayout.setVisibility(View.VISIBLE);
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
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind, false);
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
            getLessonsInfosAsyncTask.execute(email, token, topicId, topicGroupId, levelId, lessonId, userToFind, false);
        }
    }

    public void getTeachersInfos() {
        for (int i = 0; i < teachersToReview.size(); i++) {
            int teacherId = teachersToReview.get(i).getUser().getUserId();
            DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
            displayInfosProfileAsyncTask.execute(String.valueOf(teacherId), email, token);
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
        LinearLayout toDoListView = (LinearLayout) view.findViewById(R.id.to_do_list);
        toDoListView.setVisibility(View.VISIBLE);
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(getContext(), toDoList);
        toDoListAdapter.setCallback(this);
        View view = toDoListAdapter.getView(i, null, null);
        toDoListView.addView(view);
        progressDialog.dismiss();

    }

    public void displayUpcomingLessonListView(int i) {
        upcomingLessonsTextView.setText(upcomingLessons.size() + " cours à venir");
        upcomingLessonLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout upcomingLessonView = (LinearLayout) view.findViewById(R.id.upcoming_lesson);
        UpcomingLessonAdapter upcomingLessonAdapter = new UpcomingLessonAdapter(getContext(), upcomingLessons);
        View view = upcomingLessonAdapter.getView(i, null, null);
        upcomingLessonView.addView(view);
        progressDialog.dismiss();
    }

    public void displayTeachersToReviewListView(int i) {
        LinearLayout teacherToReviewListView = (LinearLayout) view.findViewById(R.id.teacher_to_review);
        teacherToReviewListView.setVisibility(View.VISIBLE);
        TeacherToReviewAdapter teacherToReviewAdapter = new TeacherToReviewAdapter(getContext(), teachersToReview, DashboardFragment.this);
        View view = teacherToReviewAdapter.getView(i, null, null);
        teacherToReviewListView.addView(view);
        progressDialog.dismiss();
    }

    @Override
    public void didTouchRefuseLessonButton(int lessonId) {
        RefuseLessonAsyncTask refuseLessonAsyncTask = new RefuseLessonAsyncTask(this);
        refuseLessonAsyncTask.execute(lessonId, email, token);
        startProgressDialog();
    }

    @Override
    public void didTouchAcceptLessonButton(int lessonId) {
        AcceptLessonAsyncTask acceptLessonAsyncTask = new AcceptLessonAsyncTask(this);
        acceptLessonAsyncTask.execute(lessonId, email, token);
        startProgressDialog();
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
    public void didTouchPositiveFeedBackButton(int lessonId) {
        PayTeacherAsyncTack payTeacherAsyncTack = new PayTeacherAsyncTack(this);
        payTeacherAsyncTack.execute(lessonId, email, token);
        startProgressDialog();
    }

    @Override
    public void didTouchNegativeFeedBackButton(int lessonId) {
        DisputeAsyncTack disputeAsyncTack = new DisputeAsyncTack(this);
        disputeAsyncTack.execute(lessonId, email, token);
        startProgressDialog();

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

            progressDialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), DashboardActivity.class);
            startActivity(intent);

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
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayUserInfosProfile(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");

            int teacherId = userJson.getInt("id");
            String firstName = userJson.getString("firstname");
            String lastName = userJson.getString("lastname");

            for (int i = 0; i < teachersToReview.size(); i++) {
                int id = teachersToReview.get(i).getUser().getUserId();

                if (id == teacherId) {
                    teachersToReview.get(i).getUser().setFirstName(firstName);
                    teachersToReview.get(i).getUser().setLastName(lastName);
                }

                if (teacherId == teachersToReview.get(teachersToReview.size() - 1).getUser().getUserId()) {
                    displayTeachersToReviewListView(i);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
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
    public void createReviewConfirmationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.create_review_positive_success_message, Toast.LENGTH_LONG).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            } else {
                Toast.makeText(getContext(), R.string.create_review_negative_success_message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startCreateReviewAsyncTask(int teacherId) {
        CreateReviewAsyncTask createReviewAsyncTask = new CreateReviewAsyncTask(this);
        createReviewAsyncTask.execute(teacherId, email, token, comment, note);
        startProgressDialog();
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
