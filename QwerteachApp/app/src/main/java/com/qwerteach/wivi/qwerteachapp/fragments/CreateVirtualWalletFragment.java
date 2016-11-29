package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateNewWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosTopicsAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by wivi on 29/11/16.
 */

public class CreateVirtualWalletFragment extends Fragment implements DisplayInfosProfileAsyncTask.IDisplayInfosProfile,
        AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CreateNewWalletAsyncTask.ICreateNewWallet {

    View view;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText,
            postalCodeEditText, cityEditText, regionEditText;
    ArrayList<String> countries;
    String currentCountry, currentRegion, currentNationality;
    String countryCode, regionCode, nationalityCode;
    String userId, email, token;
    Button saveButton;
    Locale[] locales;

    public static CreateVirtualWalletFragment newInstance() {
        CreateVirtualWalletFragment createVirtualWalletFragment = new CreateVirtualWalletFragment();
        return createVirtualWalletFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locales = Locale.getAvailableLocales();
        countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startDisplayInfosProfileAsyncTask();
        view = inflater.inflate(R.layout.fragment_create_virtual_wallet, container, false);
        firstNameEditText = (EditText) view.findViewById(R.id.firstname_edit_text);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname_edit_text);
        addressEditText = (EditText) view.findViewById(R.id.address_edit_text);
        streetNumberEditText = (EditText) view.findViewById(R.id.street_number_edit_text);
        postalCodeEditText = (EditText) view.findViewById(R.id.postal_code_edit_text);
        cityEditText = (EditText) view.findViewById(R.id.city_edit_text);
        regionEditText = (EditText) view.findViewById(R.id.region_edit_text);

        countrySpinner = (Spinner) view.findViewById(R.id.country_name_spinner);
        setSpinner(countries, countrySpinner);
        residenceSpinner = (Spinner) view.findViewById(R.id.residence_place_spinner);
        setSpinner(countries, residenceSpinner);
        nationalitySpinner = (Spinner) view.findViewById(R.id.nationality_spinner);
        setSpinner(countries, nationalitySpinner);

        saveButton = (Button) view.findViewById(R.id.save_infos_button);
        saveButton.setOnClickListener(this);

        return  view;
    }

    public void setSpinner(ArrayList arrayList, Spinner spinner) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void startDisplayInfosProfileAsyncTask() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        DisplayInfosProfileAsyncTask displayInfosProfileAsyncTask = new DisplayInfosProfileAsyncTask(this);
        displayInfosProfileAsyncTask.execute(userId, email, token);
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

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.country_name_spinner:
                currentCountry = adapterView.getItemAtPosition(i).toString();
                countryCode = getCountryCode(currentCountry);
                break;
            case R.id.residence_place_spinner:
                currentRegion = adapterView.getItemAtPosition(i).toString();
                regionCode = getCountryCode(currentRegion);
                break;
            case R.id.nationality_spinner:
                currentNationality = adapterView.getItemAtPosition(i).toString();
                nationalityCode = getCountryCode(currentNationality);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String streetNumber = streetNumberEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String region = regionEditText.getText().toString();

        CreateNewWalletAsyncTask createNewWalletAsyncTask = new CreateNewWalletAsyncTask(this);
        createNewWalletAsyncTask.execute(userId, email, token, firstName, lastName, address,
                streetNumber, postalCode, city, region, countryCode, regionCode, nationalityCode);
    }

    @Override
    public void confirmationCreationNewWallet(String string) {

    }

    public String getCountryCode(String countryName) {
        String newCountryCode = "";
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.equals(countryName)) {
                newCountryCode = locale.getCountry();
            }
        }

        return newCountryCode;
    }
}
