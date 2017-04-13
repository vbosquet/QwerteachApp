package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.ToBecomeATeacherActivity;
import com.qwerteach.wivi.qwerteachapp.UpdateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.CreateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdAdapter;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.TeacherAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 26/10/16.
 */

public class AdTabFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    View view;
    ArrayList<SmallAd> smallAds;
    RecyclerView smallAdRecyclerView;
    RecyclerView.Adapter smallAdAdapter;
    RecyclerView.LayoutManager smallAdLayoutManager;
    User user;
    Teacher teacher;
    ProgressDialog progressDialog;
    QwerteachService service;

    public static AdTabFragment newInstance() {
        AdTabFragment adTabFragment = new AdTabFragment();
        return adTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
            user = (User) getActivity().getIntent().getSerializableExtra("user");
        }

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ad_tab, container, false);
        smallAdRecyclerView = (RecyclerView) view.findViewById(R.id.title_ad);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        if (teacher != null) {
            smallAds = teacher.getSmallAds();
            if (getActivity() != null) {
                displaySmallAdListView();
            }
        }

        return  view;
    }

    public void displaySmallAdListView() {
        smallAdAdapter = new SmallAdAdapter(getActivity(), smallAds, this);
        smallAdRecyclerView.setHasFixedSize(true);
        smallAdLayoutManager = new LinearLayoutManager(getContext());
        smallAdRecyclerView.setLayoutManager(smallAdLayoutManager);
        smallAdRecyclerView.setItemAnimator(new DefaultItemAnimator());
        smallAdRecyclerView.setAdapter(smallAdAdapter);
    }

    public void didTouchOnEditButton(int position) {
        SmallAd smallAd = smallAds.get(position);
        Intent intent = new Intent(getContext(), UpdateSmallAdActivity.class);
        intent.putExtra("smallAd", smallAd);
        startActivityForResult(intent, 10002);
    }

    public void startDisplayInfosSmallAd() {
        Call<JsonResponse> call = service.getAdverts(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                smallAds = response.body().getSmallAds();
                ArrayList<String> topics = response.body().getTopicTitles();
                ArrayList<ArrayList<SmallAdPrice>> smallAdPrices = response.body().getSmallAdPrices();

                for (int i = 0; i < smallAds.size(); i++) {
                    smallAds.get(i).setTitle(topics.get(i));
                    ArrayList<SmallAdPrice> smallAdPriceArrayList = new ArrayList<>();

                    if (smallAdPrices.get(i).size() > 0) {
                        for (int j = 0; j < smallAdPrices.get(i).size(); j++) {
                            smallAdPriceArrayList.add(smallAdPrices.get(i).get(j));
                        }
                    }

                    smallAds.get(i).setSmallAdPrices(smallAdPriceArrayList);
                }

                displaySmallAdListView();
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            smallAds.clear();
            startProgressDialog();
            startDisplayInfosSmallAd();
        } else if ((requestCode == 10002) && (resultCode == Activity.RESULT_OK)) {
            smallAds.clear();
            startProgressDialog();
            startDisplayInfosSmallAd();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), CreateSmallAdActivity.class);
        startActivityForResult(intent, 10001);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
