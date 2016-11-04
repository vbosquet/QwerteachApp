package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DeleteSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosSmallAdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.CreateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 26/10/16.
 */

public class AdTabFragment extends Fragment implements DisplayInfosSmallAdAsyncTask.IDisplayInfosSmallAd,
        DeleteSmallAdAsyncTask.IDeleteSmallAd {

    FloatingActionButton floatingActionButton;
    View view;
    ArrayList<SmallAd> topicList;
    ImageView deleteImageView, editImageView;

    public static AdTabFragment newInstance() {
        AdTabFragment adTabFragment = new AdTabFragment();
        return adTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ad_tab, container, false);
        topicList = new ArrayList<>();
        startDisplayInfosSmallAdAsyncTask();
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(view.getId() == R.id.floating_action_button) {
                    Intent intent = new Intent(getContext(), CreateSmallAdActivity.class);
                    startActivity(intent);
                }
            }
        });

        return  view;
    }

    @Override
    public void displayInfosSmallAd(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String confirmationMessage = jsonObject.getString("success");

            if (confirmationMessage.equals("true")) {
                JSONArray jsonArray = jsonObject.getJSONArray("topic_title");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SmallAd smallAd = new SmallAd(jsonArray.getString(i));
                    topicList.add(smallAd);
                }

                ListView listView = (ListView) getActivity().findViewById(R.id.title_ad);
                SmallAdAdapter smallAdAdapter = new SmallAdAdapter(getActivity(), topicList);
                listView.setAdapter(smallAdAdapter);

                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {

                    }

                    @Override
                    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {
                            floatingActionButton.setVisibility(View.INVISIBLE);
                        }else {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                        for(int i = 0; i < topicList.size(); i++) {
                            if(position == i) {

                                deleteImageView = (ImageView) view.findViewById(R.id.delete_image_view);
                                editImageView = (ImageView) view.findViewById(R.id.edit_image_view);

                                deleteImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startDeleteSmallAdAsyncTask(topicList.get(position).getTitle());
                                    }
                                });

                                editImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i("EDIT", "OK");
                                    }
                                });
                            }
                        }


                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startDisplayInfosSmallAdAsyncTask() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");

        DisplayInfosSmallAdAsyncTask displayInfosSmallAdAsyncTask = new DisplayInfosSmallAdAsyncTask(this);
        displayInfosSmallAdAsyncTask.execute(userId);
    }

    public void startDeleteSmallAdAsyncTask(String title) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");

        DeleteSmallAdAsyncTask deleteSmallAdAsyncTask = new DeleteSmallAdAsyncTask(this);
        deleteSmallAdAsyncTask.execute(userId, title);
    }

    @Override
    public void confirmationDeleteSmallAdMessage(String string) {

    }
}
