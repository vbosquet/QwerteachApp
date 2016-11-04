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
        DisplayInfosProfileAsyncTask.IDisplayInfosProfile {

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText userDescriptionEditTet;
    EditText birthDateEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    Button buttonBloc1, buttonBloc2;
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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_description_tab, container, false);
        startDisplayInfosProfileAsynTack();
        calendar = Calendar.getInstance(TimeZone.getDefault());
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };
        buttonBloc1 = (Button) view.findViewById(R.id.save_infos_profile_button_bloc_1);
        buttonBloc2 = (Button) view.findViewById(R.id.save_infos_profile_button_bloc_2);

        firstNameEditText = (EditText) view.findViewById(R.id.firstname);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname);
        birthDateEditText = (EditText) view.findViewById(R.id.birthdate);
        birthDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        userDescriptionEditTet = (EditText) view.findViewById(R.id.description);
        emailEditText = (EditText) view.findViewById(R.id.email);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber);

        buttonBloc1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.save_infos_profile_button_bloc_1) {
                    startSaveInfosProfileTabAsyncTask(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                            birthDateEditText.getText().toString(), userDescriptionEditTet.getText().toString(),
                            emailEditText.getText().toString(), phoneNumberEditText.getText().toString());
                }
            }
        });

        buttonBloc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.save_infos_profile_button_bloc_2) {
                    startSaveInfosProfileTabAsyncTask(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                            birthDateEditText.getText().toString(), userDescriptionEditTet.getText().toString(),
                            emailEditText.getText().toString(), phoneNumberEditText.getText().toString());
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(sdf.format(calendar.getTime()));
    }

    public void startSaveInfosProfileTabAsyncTask(String firstName, String lastName, String birthDate, String userDescription, String email, String phoneNumber) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");

        SaveInfosProfileAsyncTask saveInfosProfileAsyncTask = new SaveInfosProfileAsyncTask(this);
        saveInfosProfileAsyncTask.execute(firstName, lastName, birthDate, userDescription, userId, email, phoneNumber);
    }

    @Override
    public void displayConfirmationRegistrationInfosProfile(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String regsitrationConfirmation = jsonObject.getString("success");

            if (regsitrationConfirmation.equals("true")) {
                Toast.makeText(getContext(), R.string.infos_profile_registration_success_toast, Toast.LENGTH_SHORT).show();
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
                String description = jsonData.getString("description");
                String email = jsonData.getString("email");
                String phonenumber = jsonData.getString("phonenumber");

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                birthDateEditText.setText(birthDate);
                userDescriptionEditTet.setText(description);
                emailEditText.setText(email);
                phoneNumberEditText.setText(phonenumber);
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
}
