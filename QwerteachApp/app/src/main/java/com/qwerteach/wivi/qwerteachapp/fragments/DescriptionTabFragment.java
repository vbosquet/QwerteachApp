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

import com.qwerteach.wivi.qwerteachapp.AsyncTasks.DescriptionTabAsyncTask;
import com.qwerteach.wivi.qwerteachapp.R;

import java.util.Locale;

/**
 * Created by wivi on 26/10/16.
 */

public class DescriptionTabFragment extends Fragment implements DescriptionTabAsyncTask.ISaveInfosProfile {

    EditText firstName;
    EditText lastName;
    EditText userDescription;
    EditText birthDate;
    Button button;
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
        button = (Button) view.findViewById(R.id.save_infos_profile_button);
        firstName = (EditText) view.findViewById(R.id.firstname);
        lastName = (EditText) view.findViewById(R.id.lastname);
        birthDate = (EditText) view.findViewById(R.id.birthdate);
        birthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        userDescription = (EditText) view.findViewById(R.id.description);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.save_infos_profile_button) {
                    startDescriptionTabAsyncTask(firstName.getText().toString(), lastName.getText().toString(),
                            birthDate.getText().toString(), userDescription.getText().toString());
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthDate.setText(sdf.format(calendar.getTime()));
    }

    public void startDescriptionTabAsyncTask(String firstName, String lastName, String birthDate, String userDescription) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userId", "");

        DescriptionTabAsyncTask descriptionTabAsyncTask = new DescriptionTabAsyncTask(this);
        descriptionTabAsyncTask.execute(firstName, lastName, birthDate, userDescription, userId);
    }

    @Override
    public void displayConfirmationRegistrationInfosProfile(String string) {

    }
}
