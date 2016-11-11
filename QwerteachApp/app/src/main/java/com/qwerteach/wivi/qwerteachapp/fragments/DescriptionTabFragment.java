package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SaveInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by wivi on 26/10/16.
 */

public class DescriptionTabFragment extends Fragment implements SaveInfosProfileAsyncTask.ISaveInfosProfile,
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile, View.OnClickListener {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText birthDateEditText;
    EditText phoneNumberEditText;
    Calendar calendar;
    View view;
    DatePickerDialog.OnDateSetListener dateSetListener;

    public static DescriptionTabFragment newInstance() {
        DescriptionTabFragment descriptionTabFragment = new DescriptionTabFragment();
        return descriptionTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDisplayInfosProfileAsynTack();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_description_tab, container, false);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };

        firstNameEditText = (EditText) view.findViewById(R.id.firstname);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname);
        birthDateEditText = (EditText) view.findViewById(R.id.birthdate);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber);
        birthDateEditText.setOnClickListener(this);

        setHasOptionsMenu(true);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(sdf.format(calendar.getTime()));
    }

    public void startSaveInfosProfileTabAsyncTask() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");
        String token = preferences.getString("token", "");

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        SaveInfosProfileAsyncTask saveInfosProfileAsyncTask = new SaveInfosProfileAsyncTask(this);
        saveInfosProfileAsyncTask.execute(firstName, lastName, birthDate, userId, phoneNumber, token);
    }

    @Override
    public void displayConfirmationRegistrationInfosProfile(String string) {
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

    @Override
    public void displayUserInfosProfile(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String getUserInfosProfile = jsonObject.getString("success");

            if (getUserInfosProfile.equals("true")) {
                JSONObject jsonData = jsonObject.getJSONObject("user");
                String firstName = jsonData.getString("firstname");
                String lastName = jsonData.getString("lastname");
                String birthDate = jsonData.getString("birthdate");
                String phoneNumber = jsonData.getString("phonenumber");

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                birthDateEditText.setText(birthDate);
                phoneNumberEditText.setText(phoneNumber);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void startDisplayInfosProfileAsynTack() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");

        DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        displayInfosProfileAsyncTask.execute(userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.birthdate) {
            new DatePickerDialog(getContext(), dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_profile_button:
                startSaveInfosProfileTabAsyncTask();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
