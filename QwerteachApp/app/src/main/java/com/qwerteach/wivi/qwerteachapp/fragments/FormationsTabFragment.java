package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 26/10/16.
 */

public class FormationsTabFragment extends Fragment implements DisplaySchoolLevelsAsyncTask.IDisplaySchoolLevels,
        AdapterView.OnItemSelectedListener,
        SaveInfosFormationAsyncTask.ISaveInfosFormation,
        View.OnClickListener {

    EditText professionEditText, userDescriptionEditTet;
    Button saveInfosButton;
    Spinner levelSpinner;
    ArrayList<Level> levels;
    View view;
    String levelName, defaultTextForLevelSpinner, userId, email, token;
    int levelId;
    ProgressDialog progressDialog;
    Teacher teacher;
    User user;

    public static FormationsTabFragment newInstance() {
        FormationsTabFragment formationsTabFragment = new FormationsTabFragment();
        return formationsTabFragment;
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

        levels = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());


        if (user != null) {
            levelId = user.getLevelId();
        }

        if (teacher != null) {
            levelId = teacher.getUser().getLevelId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_formations_tab, container, false);

        professionEditText = (EditText) view.findViewById(R.id.profession);
        userDescriptionEditTet = (EditText) view.findViewById(R.id.description);
        levelSpinner = (Spinner) view.findViewById(R.id.spinner_level);
        saveInfosButton = (Button) view.findViewById(R.id.save_infos_button);
        saveInfosButton.setOnClickListener(this);

        DisplaySchoolLevelsAsyncTask displaySchoolLevelsAsyncTask = new DisplaySchoolLevelsAsyncTask(this);
        displaySchoolLevelsAsyncTask.execute();

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

            if (user != null) {
                professionEditText.setText(user.getOccupation());
                userDescriptionEditTet.setText(user.getDescription());
            }

            if (teacher != null) {
                professionEditText.setText(teacher.getUser().getOccupation());
                userDescriptionEditTet.setText(teacher.getUser().getDescription());
            }

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

    public void startSaveInfosFormationTabAsyncTask() {
        int levelId = 0;
        String profession = professionEditText.getText().toString();
        String userDescription = userDescriptionEditTet.getText().toString();


        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelName().equals(levelName)) {
                levelId = levels.get(i).getLevelId();
            }
        }

        SaveInfosFormationAsyncTask saveInfosFormationAsyncTask = new SaveInfosFormationAsyncTask(this);
        saveInfosFormationAsyncTask.execute(userId, profession, userDescription, levelId, email, token);
        startProgressDialog();

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void confirmationRegistrationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String regsitrationConfirmation = jsonObject.getString("success");
            progressDialog.dismiss();

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_infos_button) {
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
