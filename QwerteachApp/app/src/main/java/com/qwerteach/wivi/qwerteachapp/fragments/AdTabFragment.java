package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.ToBecomeATeacherActivity;
import com.qwerteach.wivi.qwerteachapp.UpdateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DeleteSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.CreateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 26/10/16.
 */

public class AdTabFragment extends Fragment implements DisplayInfosSmallAdAsyncTask.IDisplayInfosSmallAd,
        DeleteSmallAdAsyncTask.IDeleteSmallAd, View.OnClickListener {

    FloatingActionButton floatingActionButton;
    View view;
    ArrayList<SmallAd> topicList;
    ListView listView;
    SmallAdAdapter smallAdAdapter;
    String userId, email, token;
    User user;
    Teacher teacher;

    public static AdTabFragment newInstance() {
        AdTabFragment adTabFragment = new AdTabFragment();
        return adTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
            user = (User) getActivity().getIntent().getSerializableExtra("student");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ad_tab, container, false);
        topicList = new ArrayList<>();
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);


        if (teacher != null) {
            startDisplayInfosSmallAdAsyncTask();
        }

        return  view;
    }

    @Override
    public void displayInfosSmallAd(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String confirmationMessage = jsonObject.getString("success");

            if (confirmationMessage.equals("true")) {
                JSONArray jsonTopicTitleArray = jsonObject.getJSONArray("topic_title");
                JSONArray jsonArray = jsonObject.getJSONArray("advert");

                for (int i = 0; i < jsonTopicTitleArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    int advertId = jsonData.getInt("id");
                    int topicId = jsonData.getInt("topic_id");
                    int topicGroupId = jsonData.getInt("topic_group_id");
                    String otherName = jsonData.getString("other_name");
                    String description = jsonData.getString("description");
                    String topicTitle = jsonTopicTitleArray.getString(i);

                    if(!topicTitle.equals("Other")) {
                        SmallAd smallAd = new SmallAd(topicTitle, advertId, topicId, topicGroupId, description);
                        topicList.add(smallAd);
                    } else {
                        SmallAd smallAd = new SmallAd(otherName, advertId, topicId, topicGroupId, description);
                        topicList.add(smallAd);
                    }

                }

                listView = (ListView) getActivity().findViewById(R.id.title_ad);
                smallAdAdapter = new SmallAdAdapter(getActivity(), topicList, new SmallAdAdapter.IButtonClickListener() {
                    @Override
                    public void onDeleteClick(int position) {
                        int advertId = topicList.get(position).getAdvertId();
                        startDeleteSmallAdAsyncTask(advertId);
                    }

                    @Override
                    public void onEditClick(int position) {
                        SmallAd smallAd = topicList.get(position);
                        Intent intent = new Intent(getContext(), UpdateSmallAdActivity.class);
                        intent.putExtra("smallAd", smallAd);
                        startActivityForResult(intent, 10002);
                    }
                });

                listView.setAdapter(smallAdAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startDisplayInfosSmallAdAsyncTask() {
        DisplayInfosSmallAdAsyncTask displayInfosSmallAdAsyncTask = new DisplayInfosSmallAdAsyncTask(this);
        displayInfosSmallAdAsyncTask.execute(userId, email, token);
    }

    public void startDeleteSmallAdAsyncTask(int advertId) {
        DeleteSmallAdAsyncTask deleteSmallAdAsyncTask = new DeleteSmallAdAsyncTask(this);
        deleteSmallAdAsyncTask.execute(advertId, email, token);
    }

    @Override
    public void confirmationDeleteSmallAdMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String confirmationMessage = jsonObject.getString("success");

            if (confirmationMessage.equals("true")) {
                topicList.clear();
                startDisplayInfosSmallAdAsyncTask();
                Toast.makeText(getContext(), R.string.delete_small_ad_success_true_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.delete_small_ad_success_false_message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            topicList.clear();
            startDisplayInfosSmallAdAsyncTask();
        } else if ((requestCode == 10002) && (resultCode == Activity.RESULT_OK)) {
            topicList.clear();
            startDisplayInfosSmallAdAsyncTask();
        }
    }

    @Override
    public void onClick(View view) {
        if (teacher != null) {
            Intent intent = new Intent(getContext(), CreateSmallAdActivity.class);
            startActivityForResult(intent, 10001);
        } else {
            Intent intent = new Intent(getContext(), ToBecomeATeacherActivity.class);
            startActivity(intent);
        }
    }
}
