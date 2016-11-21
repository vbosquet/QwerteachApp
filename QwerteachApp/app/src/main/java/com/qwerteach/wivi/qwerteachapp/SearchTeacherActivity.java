package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplaySmallAdPriceAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SearchTeacherAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SearchTeacherActivity extends AppCompatActivity implements SearchTeacherAsyncTask.ISearchTeacher,
        DisplayInfosSmallAdAsyncTask.IDisplayInfosSmallAd, DisplaySmallAdPriceAsyncTask.IDisplaySmallAdPrice {

    ArrayList<Teacher> teacherList;
    ArrayList<SmallAd>smallAds;
    ListView listView;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.teacher_list_view);

        String query = getIntent().getStringExtra("query");

        teacherList = new ArrayList<>();
        smallAds = new ArrayList<>();

        SearchTeacherAsyncTask searchTeacherAsyncTask = new SearchTeacherAsyncTask(this);
        searchTeacherAsyncTask.execute(query);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_teacher_menu, menu);
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
    public void displaySearchResults(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray searchJsonArray = jsonObject.getJSONArray("search");

            for (int i = 0; i < searchJsonArray.length(); i++) {
                JSONObject jsonData = searchJsonArray.getJSONObject(i);
                int id = jsonData.getInt("id");
                String firstName = jsonData.getString("firstname");
                String lastName = jsonData.getString("lastname");
                String description = jsonData.getString("description");
                String occupation = jsonData.getString("occupation");
                String birthDate = jsonData.getString("birthdate");
                Teacher teacher = new Teacher(id, firstName, lastName, description, occupation, birthDate);
                teacherList.add(teacher);

                DisplayInfosSmallAdAsyncTask displayInfosSmallAdAsyncTask = new DisplayInfosSmallAdAsyncTask(this);
                displayInfosSmallAdAsyncTask.execute(String.valueOf(id));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayInfosSmallAd(String string) {

        ArrayList<String> topicTitleList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray topicTitleJsonArray = jsonObject.getJSONArray("topic_title");
            JSONArray advertJsonArray = jsonObject.getJSONArray("advert");

            for (int i = 0; i < advertJsonArray.length(); i++) {
                JSONObject jsonData = advertJsonArray.getJSONObject(i);
                userId = jsonData.getInt("user_id");
                int smallAdId = jsonData.getInt("id");
                String otherName = jsonData.getString("other_name");
                String topicTitle = topicTitleJsonArray.getString(i);
                String description = jsonData.getString("description");
                int topicId = jsonData.getInt("topic_id");
                int topicGroupId = jsonData.getInt("topic_group_id");

                if(topicTitle.equals("Other")) {
                    topicTitleList.add(otherName);
                    SmallAd smallAd = new SmallAd(otherName, smallAdId, topicId, topicGroupId, description);
                    smallAd.setUserId(userId);
                    smallAds.add(smallAd);
                } else {
                    topicTitleList.add(topicTitle);
                    SmallAd smallAd = new SmallAd(topicTitle, smallAdId, topicId, topicGroupId, description);
                    smallAd.setUserId(userId);
                    smallAds.add(smallAd);
                }

                DisplaySmallAdPriceAsyncTask displaySmallAdPriceAsyncTask = new DisplaySmallAdPriceAsyncTask(this);
                displaySmallAdPriceAsyncTask.execute(smallAdId);

            }

            for (int i = 0; i < teacherList.size(); i++) {
                if (userId == teacherList.get(i).getTeacherId()) {
                    teacherList.get(i).setTopicTitleList(topicTitleList);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayTeacherListView() {
        TeacherAdapter teacherAdapter = new TeacherAdapter(this, teacherList);
        listView.setAdapter(teacherAdapter);
    }

    public void didTouchReadMoreTextView(View view) {
        int position = listView.getPositionForView(view);
        int teacherId = teacherList.get(position).getTeacherId();

        SmallAd smallAd = new SmallAd();

        for (int i = 0; i < smallAds.size(); i++) {
            if (smallAds.get(i).getUserId()== teacherId) {
                smallAd = smallAds.get(i);
            }
        }


        Intent intent = new Intent(this, TeacherProfile.class);
        intent.putExtra("teacher", teacherList.get(position));
        intent.putExtra("smallAd", smallAd);
        startActivity(intent);
    }

    @Override
    public void displaySmallAdPrice(String string) {

        ArrayList<SmallAdPrice> smallAdPrices = new ArrayList<>();
        ArrayList<Double> coursePriceList = new ArrayList<>();
        int smallAdId = 0;
        int newUserId = 0;

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("advert_price");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int id = jsonData.getInt("id");
                smallAdId = jsonData.getInt("advert_id");
                int levelId = jsonData.getInt("level_id");
                double price = jsonData.getDouble("price");
                SmallAdPrice smallAdPrice = new SmallAdPrice(id, smallAdId, levelId, price);
                smallAdPrices.add(smallAdPrice);
            }

            for (int i = 0; i < smallAdPrices.size(); i++) {
                coursePriceList.add(smallAdPrices.get(i).getPrice());
            }

            for (int i = 0; i < smallAds.size(); i++) {
                if (smallAdId == smallAds.get(i).getAdvertId()) {
                    newUserId = smallAds.get(i).getUserId();
                    smallAds.get(i).setSmallAdPrices(smallAdPrices);
                }
            }

            for (int i = 0; i < teacherList.size(); i++) {
                if (newUserId == teacherList.get(i).getTeacherId()) {
                    teacherList.get(i).addPriceToMinPriceList(coursePriceList);
                }
            }

            if (userId == teacherList.get(teacherList.size() - 1).getTeacherId()) {
                displayTeacherListView();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
