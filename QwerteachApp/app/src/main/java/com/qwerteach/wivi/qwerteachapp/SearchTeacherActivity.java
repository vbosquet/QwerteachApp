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
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllTopicsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SearchTeacherAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.ShowProfileInfosAsyncTask;
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

public class SearchTeacherActivity extends AppCompatActivity implements
        SearchTeacherAsyncTask.ISearchTeacher,
        AdapterView.OnItemSelectedListener,
        GetAllTopicsAsyncTask.IGetAllTopics,
        ShowProfileInfosAsyncTask.IShowProfileInfos {

    ArrayList<Teacher> teacherList;
    ArrayList<String> menuItems, searchSortingOptionNameToDisplay, searchSortingOptionNameToSendToAsyncTask;
    ListView listView;
    Spinner searchSortingOptionsSpinner;
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
            JSONArray pagin = jsonObject.getJSONArray("pagin");
            JSONArray optionsJsonArray = jsonObject.getJSONArray("options");

            if (pagin.length() > 0) {
                for (int i = 0; i < pagin.length(); i++) {
                    JSONObject jsonData = pagin.getJSONObject(i);
                    int userId = jsonData.getInt("id");
                    String firstName = jsonData.getString("firstname");
                    String lastName = jsonData.getString("lastname");
                    String birthdate = jsonData.getString("birthdate");
                    String description = jsonData.getString("description");
                    String occupation = jsonData.getString("occupation");

                    User user = new User(userId, firstName, lastName, birthdate, occupation, description);
                    Teacher teacher = new Teacher();
                    teacher.setUser(user);
                    teacherList.add(teacher);

                    ShowProfileInfosAsyncTask showProfileInfosAsyncTask = new ShowProfileInfosAsyncTask(this);
                    showProfileInfosAsyncTask.execute(String.valueOf(userId), email, token);
                }

            } else {
                Toast.makeText(this, R.string.no_result_found_toast_message, Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < optionsJsonArray.length(); i++) {
                JSONArray jsonData = optionsJsonArray.getJSONArray(i);

                String searchOptionNameToDisplay = jsonData.getString(0);
                String searchOptionNameToSend = jsonData.getString(1);

                searchSortingOptionNameToDisplay.add(searchOptionNameToDisplay);
                searchSortingOptionNameToSendToAsyncTask.add(searchOptionNameToSend);
            }

            displaySearchSortingOptionsSpinner();

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
        Intent intent = new Intent(this, TeacherProfileActivity.class);
        intent.putExtra("teacher", teacherList.get(position));
        intent.putExtra("query", query);
        startActivity(intent);
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

    @Override
    public void showProfileInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);

            float avg = 0;
            if (!jsonObject.isNull("avg")) {
                avg = jsonObject.getLong("avg");
            }

            double minPrice = jsonObject.getDouble("min_price");
            JSONArray notesJson = jsonObject.getJSONArray("notes");
            JSONArray reviewsJson = jsonObject.getJSONArray("reviews");
            JSONArray advertsJson = jsonObject.getJSONArray("adverts");
            JSONArray topicsJson = jsonObject.getJSONArray("topics");
            JSONArray reviewsSanderNamesJson = jsonObject.getJSONArray("review_sender_names");
            JSONObject userJson = jsonObject.getJSONObject("user");
            int userId = userJson.getInt("id");
            JSONArray advertPricesJson =jsonObject.getJSONArray("advert_prices");

            ArrayList<SmallAd> smallAds = new ArrayList<>();
            ArrayList<Review> reviews = new ArrayList<>();

            for (int i = 0; i < advertsJson.length(); i++) {

                JSONObject jsonData = advertsJson.getJSONObject(i);
                String topicTitle = topicsJson.getString(i);
                int smallAdId = jsonData.getInt("id");
                int topicId = jsonData.getInt("topic_id");
                int topicGroupId = jsonData.getInt("topic_group_id");
                int teacherId = jsonData.getInt("user_id");
                String smallAdDescription = jsonData.getString("description");

                JSONArray advertPrices = advertPricesJson.getJSONArray(i);
                ArrayList<SmallAdPrice> smallAdPrices = new ArrayList<>();

                for (int j = 0; j < advertPrices.length(); j++) {
                    JSONObject advertPricesData = advertPrices.getJSONObject(j);
                    int id = advertPricesData.getInt("id");
                    int levelId = advertPricesData.getInt("level_id");
                    double price = advertPricesData.getDouble("price");

                    SmallAdPrice smallAdPrice = new SmallAdPrice(id, levelId, price);
                    smallAdPrices.add(smallAdPrice);
                }

                SmallAd smallAd = new SmallAd();
                smallAd.setAdvertId(smallAdId);
                smallAd.setTopicId(topicId);
                smallAd.setTopicGroupId(topicGroupId);
                smallAd.setUserId(teacherId);
                smallAd.setDescription(smallAdDescription);
                smallAd.setTitle(topicTitle);
                smallAd.setSmallAdPrices(smallAdPrices);

                smallAds.add(smallAd);
            }

            for (int i = 0; i < reviewsJson.length(); i++) {
                JSONObject jsonData = reviewsJson.getJSONObject(i);

                int reviewId = jsonData.getInt("id");
                int senderId = jsonData.getInt("sender_id");
                int subjectId = jsonData.getInt("subject_id");
                String reviewText = jsonData.getString("review_text");
                int note = jsonData.getInt("note");
                String creationDate = jsonData.getString("created_at");
                String senderFirstName = reviewsSanderNamesJson.getString(i);

                Review review = new Review(reviewId, senderId, subjectId, reviewText, note, creationDate);
                review.setSenderFirstName(senderFirstName);
                reviews.add(review);
            }

            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getUser().getUserId() == userId) {
                    teacherList.get(i).setSmallAds(smallAds);
                    teacherList.get(i).setReviews(reviews);
                    teacherList.get(i).setRating(avg);
                    teacherList.get(i).setNumberOfReviews(notesJson.length());
                    teacherList.get(i).setMinPrice(minPrice);
                }

                if (userId == teacherList.get(teacherList.size() - 1).getUser().getUserId()) {
                    progressDialog.dismiss();
                    displayTeacherListView();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
