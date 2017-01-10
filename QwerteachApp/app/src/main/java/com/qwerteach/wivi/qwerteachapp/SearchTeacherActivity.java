package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplaySmallAdPriceAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SearchTeacherAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchTeacherActivity extends AppCompatActivity implements SearchTeacherAsyncTask.ISearchTeacher,
        DisplayInfosSmallAdAsyncTask.IDisplayInfosSmallAd,
        DisplaySmallAdPriceAsyncTask.IDisplaySmallAdPrice,
        AdapterView.OnItemSelectedListener,
        GetAllTopicsAsyncTask.IGetAllTopics {

    ArrayList<Teacher> teacherList;
    ArrayList<SmallAd>smallAds;
    ArrayList<String> menuItems;
    ArrayList<String> searchSortingOptionNameToDisplay, searchSortingOptionNameToSendToAsyncTask;
    ListView listView;
    Spinner searchSortingOptionsSpinner;
    int userId;
    int currentSearchSortingOption = 0;
    String query, email, token;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        query = getIntent().getStringExtra("query");

        listView = (ListView) findViewById(R.id.teacher_list_view);
        searchSortingOptionsSpinner = (Spinner) findViewById(R.id.search_sorting_options_spinner);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        teacherList = new ArrayList<>();
        smallAds = new ArrayList<>();
        menuItems = new ArrayList<>();
        searchSortingOptionNameToDisplay = new ArrayList<>();
        searchSortingOptionNameToSendToAsyncTask = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        menuItems.add(query);

        startSearchTeacherAsyncTask(query, "");

        GetAllTopicsAsyncTask getAllTopicsAsyncTask = new GetAllTopicsAsyncTask(this);
        getAllTopicsAsyncTask.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_teacher_menu, menu);
        MenuItem item = menu.findItem(R.id.teacher_search_filter);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter topicAdapter = new ArrayAdapter(this, R.layout.drop_down_menu_item, menuItems) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(topicAdapter);
        spinner.setOnItemSelectedListener(this);

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
            JSONArray searchJsonArray = jsonObject.getJSONArray("pagin");
            JSONArray optionsJsonArray = jsonObject.getJSONArray("options");
            JSONArray ratingJsonArray = jsonObject.getJSONArray("rating");
            JSONArray numberOfReviewsJsonArray = jsonObject.getJSONArray("number_of_reviews");
            JSONArray reviewsReceived = jsonObject.getJSONArray("reviews_received");

            if (searchJsonArray.length() > 0) {
                for (int i = 0; i < searchJsonArray.length(); i++) {
                    JSONObject teacherData = searchJsonArray.getJSONObject(i);
                    JSONArray teacherReviews = reviewsReceived.getJSONArray(i);

                    int id = teacherData.getInt("id");
                    String firstName = teacherData.getString("firstname");
                    String lastName = teacherData.getString("lastname");
                    String description = teacherData.getString("description");
                    String occupation = teacherData.getString("occupation");
                    String birthDate = teacherData.getString("birthdate");

                    User user = new User(id, firstName, lastName, birthDate, occupation, description);
                    Teacher teacher = new Teacher();
                    teacher.setUser(user);

                    float rating;
                    if (!ratingJsonArray.get(i).toString().equals("null")) {
                        rating = ratingJsonArray.getLong(i);
                        teacher.setRating(rating);
                    }

                    int numberOfReviews = numberOfReviewsJsonArray.getInt(i);
                    teacher.setNumberOfReviews(numberOfReviews);

                    if (teacherReviews.length() > 0) {
                        ArrayList<Review> reviews = new ArrayList<>();
                        for (int j = 0; j < teacherReviews.length(); j++) {
                            JSONObject reviewData = teacherReviews.getJSONObject(j);
                            int reviewId = reviewData.getInt("id");
                            int senderId = reviewData.getInt("sender_id");
                            int subjectId = reviewData.getInt("subject_id");
                            String reviewText = reviewData.getString("review_text");
                            int note = reviewData.getInt("note");
                            String creationDate = reviewData.getString("created_at");
                            Review review = new Review(reviewId, senderId, subjectId, reviewText, note, creationDate);
                            reviews.add(review);
                        }

                        teacher.setReviews(reviews);
                    }

                    teacherList.add(teacher);

                    DisplayInfosSmallAdAsyncTask displayInfosSmallAdAsyncTask = new DisplayInfosSmallAdAsyncTask(this);
                    displayInfosSmallAdAsyncTask.execute(String.valueOf(id), email, token);
                }

            } else {
                Toast.makeText(this, R.string.no_result_found_toast_message, Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < optionsJsonArray.length(); i++) {
                JSONArray jsonData = optionsJsonArray.getJSONArray(i);

                String searchOptionNameToDisplay = jsonData.getString(0);
                String searOtionNameToSend = jsonData.getString(1);

                searchSortingOptionNameToDisplay.add(searchOptionNameToDisplay);
                searchSortingOptionNameToSendToAsyncTask.add(searOtionNameToSend);
            }

            displaySearchSortingOptionsSpinner();

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
                displaySmallAdPriceAsyncTask.execute(smallAdId, email, token);

            }

            for (int i = 0; i < teacherList.size(); i++) {
                if (userId == teacherList.get(i).getUser().getUserId()) {
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
        int teacherId = teacherList.get(position).getUser().getUserId();

        SmallAd smallAd = new SmallAd();

        for (int i = 0; i < smallAds.size(); i++) {
            if (smallAds.get(i).getUserId()== teacherId) {
                smallAd = smallAds.get(i);
            }
        }


        Intent intent = new Intent(this, TeacherProfileActivity.class);
        intent.putExtra("teacher", teacherList.get(position));
        intent.putExtra("smallAd", smallAd);
        intent.putExtra("query", query);
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
                    for (int j = 0; j < teacherList.size(); j++) {
                        if (newUserId == teacherList.get(j).getUser().getUserId()) {
                            teacherList.get(j).addSmallAds(smallAds.get(i));
                        }
                    }
                }
            }

            for (int i = 0; i < teacherList.size(); i++) {
                if (newUserId == teacherList.get(i).getUser().getUserId()) {
                    teacherList.get(i).addPriceToPriceList(coursePriceList);
                }
            }

            if (teacherList.size() > 0
                    && userId == teacherList.get(teacherList.size() - 1).getUser().getUserId()) {
                progressDialog.dismiss();
                displayTeacherListView();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String newQuery = menuItems.get(i);

        if (!newQuery.equals(query)) {
            Intent intent = new Intent(this, SearchTeacherActivity.class);
            intent.putExtra("query", newQuery);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void displayTopics(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("topics");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String topicTitle = jsonData.getString("title");

                if (!topicTitle.equals("Other")) {
                    menuItems.add(topicTitle);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displaySearchSortingOptionsSpinner() {
        ArrayAdapter searchOptionsAdapter = new ArrayAdapter(this, R.layout.drop_down_menu_item, searchSortingOptionNameToDisplay);
        searchOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSortingOptionsSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        searchSortingOptionsSpinner.setAdapter(searchOptionsAdapter);
        searchSortingOptionsSpinner.setSelection(currentSearchSortingOption);
        searchSortingOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String searchSortingOption = searchSortingOptionNameToSendToAsyncTask.get(i);
                if (currentSearchSortingOption == i) {
                    return;
                } else {
                    startSearchTeacherAsyncTask(query, searchSortingOption);
                }

                currentSearchSortingOption = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void startSearchTeacherAsyncTask(String query, String searchSortingOption) {
        teacherList.clear();
        smallAds.clear();
        searchSortingOptionNameToDisplay.clear();
        searchSortingOptionNameToSendToAsyncTask.clear();

        SearchTeacherAsyncTask searchTeacherAsyncTask = new SearchTeacherAsyncTask(this);
        searchTeacherAsyncTask.execute(query, searchSortingOption);

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
