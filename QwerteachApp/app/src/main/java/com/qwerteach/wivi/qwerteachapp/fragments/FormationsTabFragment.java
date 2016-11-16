package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.DashboardActivity;
import com.qwerteach.wivi.qwerteachapp.EditProfileActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplaySchoolLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SaveInfosFormationAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SaveInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 26/10/16.
 */

public class FormationsTabFragment extends Fragment implements DisplaySchoolLevelsAsyncTask.IDisplaySchoolLevels,
        AdapterView.OnItemSelectedListener,
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile, SaveInfosFormationAsyncTask.ISaveInfosFormation {

    EditText professionEditText, userDescriptionEditTet;
    Spinner levelSpinner;
    ArrayList<Level> levels;
    View view;
    String levelName, defaultTextForLevelSpinner;
    int levelId;

    public static FormationsTabFragment newInstance() {
        FormationsTabFragment formationsTabFragment = new FormationsTabFragment();
        return formationsTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        levels = new ArrayList<>();
        startDisplayInfosProfileAsynTack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_formations_tab, container, false);
        professionEditText = (EditText) view.findViewById(R.id.profession);
        userDescriptionEditTet = (EditText) view.findViewById(R.id.description);
        levelSpinner = (Spinner) view.findViewById(R.id.spinner_level);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void displaySchoolLevels(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("level");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int levelId = jsonData.getInt("id");
                String levelName = jsonData.getString("fr");
                Level level = new Level(levelId, levelName);
                levels.add(level);
            }

            ArrayList<String> levelNames = new ArrayList<>();

            for (int i = 0; i < levels.size(); i++) {
                levelNames.add(levels.get(i).getLevelName());
            }

            for (int i = 0; i < levels.size(); i++) {
                if (levels.get(i).getLevelId() == levelId) {
                    defaultTextForLevelSpinner = levels.get(i).getLevelName();
                }
            }

            ArrayAdapter levelAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, levelNames);
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            levelSpinner.setAdapter(levelAdapter);
            int position = getIndexByString(levelSpinner, defaultTextForLevelSpinner);
            levelSpinner.setSelection(position);
            levelSpinner.setOnItemSelectedListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        levelName = levels.get(i).getLevelName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startDisplayInfosProfileAsynTack() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");
        String email = preferences.getString("email", "");
        String token = preferences.getString("token", "");

        DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        displayInfosProfileAsyncTask.execute(userId, email, token);
    }

    public void startSaveInfosFormationTabAsyncTask() {
        int levelId = 0;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");
        String email = preferences.getString("email", "");
        String token = preferences.getString("token", "");

        String profession = professionEditText.getText().toString();
        String userDescription = userDescriptionEditTet.getText().toString();


        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelName().equals(levelName)) {
                levelId = levels.get(i).getLevelId();
            }
        }

        SaveInfosFormationAsyncTask saveInfosFormationAsyncTask = new SaveInfosFormationAsyncTask(this);
        saveInfosFormationAsyncTask.execute(userId, profession, userDescription, levelId, email, token);

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_profile_button:
                startSaveInfosFormationTabAsyncTask();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayUserInfosProfile(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String getUserInfosProfile = jsonObject.getString("success");

            if (getUserInfosProfile.equals("true")) {
                JSONObject jsonData = jsonObject.getJSONObject("user");
                String description = jsonData.getString("description");
                String profession = jsonData.getString("occupation");
                levelId = jsonData.getInt("level_id");

                professionEditText.setText(profession);
                userDescriptionEditTet.setText(description);

                DisplaySchoolLevelsAsyncTask displaySchoolLevelsAsyncTask = new DisplaySchoolLevelsAsyncTask(this);
                displaySchoolLevelsAsyncTask.execute();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void confirmationRegistrationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String regsitrationConfirmation = jsonObject.getString("success");

            if (regsitrationConfirmation.equals("true")) {
                Toast.makeText(getContext(), R.string.infos_profile_registration_success_toast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.infos_profile_registration_error_toast, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

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
}
