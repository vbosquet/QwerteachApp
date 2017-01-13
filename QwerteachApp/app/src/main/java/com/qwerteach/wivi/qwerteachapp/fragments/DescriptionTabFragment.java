package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by wivi on 26/10/16.
 */

public class DescriptionTabFragment extends Fragment implements SaveInfosProfileAsyncTask.ISaveInfosProfile, View.OnClickListener {

    EditText firstNameEditText, lastNameEditText, birthDateEditText, phoneNumberEditText;
    Calendar calendar;
    View view;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String userId, email, token;
    ProgressDialog progressDialog;
    Button saveInfosButton;
    Teacher teacher;
    User user;

    public static DescriptionTabFragment newInstance() {
        DescriptionTabFragment descriptionTabFragment = new DescriptionTabFragment();
        return descriptionTabFragment;
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

        progressDialog = new ProgressDialog(getContext());
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
        saveInfosButton = (Button) view.findViewById(R.id.save_infos_button);

        birthDateEditText.setOnClickListener(this);
        saveInfosButton.setOnClickListener(this);

        displayUserInfos();

        return view;
    }

    public void displayUserInfos() {
        if (user != null) {
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            birthDateEditText.setText(user.getBirthdate());
            phoneNumberEditText.setText(user.getPhoneNumber());
        }

        if (teacher != null) {
            firstNameEditText.setText(teacher.getUser().getFirstName());
            lastNameEditText.setText(teacher.getUser().getLastName());
            birthDateEditText.setText(teacher.getUser().getBirthdate());
            phoneNumberEditText.setText(teacher.getUser().getPhoneNumber());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(sdf.format(calendar.getTime()));
    }

    public void startSaveInfosProfileTabAsyncTask() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        SaveInfosProfileAsyncTask saveInfosProfileAsyncTask = new SaveInfosProfileAsyncTask(this);
        saveInfosProfileAsyncTask.execute(firstName, lastName, birthDate, userId, phoneNumber, email, token);
        startProgressDialog();
    }

    @Override
    public void displayConfirmationRegistrationInfosProfile(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String regsitrationConfirmation = jsonObject.getString("success");
            progressDialog.dismiss();

            if (regsitrationConfirmation.equals("true")) {
                JSONObject userJson = jsonObject.getJSONObject("user");
                String firstName = userJson.getString("firstname");
                String lastName = userJson.getString("lastname");

                Toast.makeText(getContext(), R.string.infos_profile_registration_success_toast, Toast.LENGTH_SHORT).show();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lastName", lastName);
                editor.putString("firstName", firstName);
                editor.apply();
            } else {
                Toast.makeText(getContext(), R.string.infos_profile_registration_error_toast, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.birthdate) {
            new DatePickerDialog(getContext(), dateSetListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        if (view.getId() == R.id.save_infos_button) {
            startSaveInfosProfileTabAsyncTask();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
