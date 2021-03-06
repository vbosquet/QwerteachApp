package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 26/10/16.
 */

public class FormationsTabFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    EditText professionEditText, userDescriptionEditTet;
    Button saveInfosButton;
    Spinner levelSpinner;
    ArrayList<Level> levels;
    View view;
    String levelName, defaultTextForLevelSpinner;
    int levelId;
    ProgressDialog progressDialog;
    User user;
    QwerteachService service;

    public static FormationsTabFragment newInstance() {
        FormationsTabFragment formationsTabFragment = new FormationsTabFragment();
        return formationsTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        /*Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            user = (User) getActivity().getIntent().getSerializableExtra("user");
        }*/

        levels = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        levelId = user.getLevelId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_formations_tab, container, false);

        professionEditText = (EditText) view.findViewById(R.id.profession);
        userDescriptionEditTet = (EditText) view.findViewById(R.id.description);
        levelSpinner = (Spinner) view.findViewById(R.id.spinner_level);
        saveInfosButton = (Button) view.findViewById(R.id.save_infos_button);
        saveInfosButton.setOnClickListener(this);

        userDescriptionEditTet.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.description) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        Call<JsonResponse> call = service.getLevels(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if(response.isSuccessful()) {
                    levels = response.body().getLevels();
                    displayLevels();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void displayLevels() {
        ArrayList<String> levelNames = new ArrayList<>();
        for (int i = 0; i < levels.size(); i++) {
            levelNames.add(levels.get(i).getFrLevelName());
        }

        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelId() == levelId) {
                defaultTextForLevelSpinner = levels.get(i).getFrLevelName();
            }
        }

        if (getActivity() != null) {
            ArrayAdapter levelAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, levelNames);
            levelAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            levelSpinner.setAdapter(levelAdapter);
            int position = getIndexByString(levelSpinner, defaultTextForLevelSpinner);
            levelSpinner.setSelection(position);
            levelSpinner.setOnItemSelectedListener(this);
        }

        professionEditText.setText(user.getOccupation());
        userDescriptionEditTet.setText(Html.fromHtml(user.getDescription()), TextView.BufferType.SPANNABLE);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        levelName = levels.get(i).getFrLevelName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startSaveInfosFormationTabAsyncTask() {
        int levelId = 0;
        final String profession = professionEditText.getText().toString();
        String userDescription = userDescriptionEditTet.getText().toString();


        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getFrLevelName().equals(levelName)) {
                levelId = levels.get(i).getLevelId();
            }
        }

        User newUser = new User();
        newUser.setOccupation(profession);
        newUser.setDescription(userDescription);
        newUser.setLevelId(levelId);

        Map<String, User> requestBody = new HashMap<>();
        requestBody.put("user", newUser);

        Call<JsonResponse> call = service.getStudentInfos(user.getUserId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private int getIndexByString(Spinner spinner, String string) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_infos_button) {
            startProgressDialog();
            startSaveInfosFormationTabAsyncTask();
        }
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
