package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateNewWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllWalletInfosAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by wivi on 8/12/16.
 */

public class UserInfosTabFragment extends Fragment implements GetAllWalletInfosAsyncTask.IGetAllWalletInfos,
        AdapterView.OnItemSelectedListener,
        View.OnClickListener, CreateNewWalletAsyncTask.ICreateNewWallet {

    View view;
    String email, token, countryCode, nationalityCode, residencePlaceCode, currentCountry, currentNationality, currentResidencePlace;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText, postalCodeEditText, cityEditText, regionEditText;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    ArrayList<String> countries;
    Locale[] locales;
    Button saveButton;

    public static UserInfosTabFragment newInstance() {
        UserInfosTabFragment userInfosTabFragment = new UserInfosTabFragment();
        return  userInfosTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        locales = Locale.getAvailableLocales();
        countries = new ArrayList<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);

        startGetAllWalletInfosAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_user_infos_tab, container, false);
        firstNameEditText = (EditText) view.findViewById(R.id.firstname_edit_text);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname_edit_text);
        addressEditText = (EditText) view.findViewById(R.id.address_edit_text);
        streetNumberEditText = (EditText) view.findViewById(R.id.street_number_edit_text);
        postalCodeEditText = (EditText) view.findViewById(R.id.postal_code_edit_text);
        cityEditText = (EditText) view.findViewById(R.id.city_edit_text);
        regionEditText = (EditText) view.findViewById(R.id.region_edit_text);
        countrySpinner = (Spinner) view.findViewById(R.id.country_name_spinner);
        residenceSpinner = (Spinner) view.findViewById(R.id.residence_place_spinner);
        nationalitySpinner = (Spinner) view.findViewById(R.id.nationality_spinner);
        saveButton = (Button) view.findViewById(R.id.save_infos_button);
        saveButton.setOnClickListener(this);

        return view;
    }

    public void setSpinner(ArrayList arrayList, Spinner spinner, String countryCode) {
        String countryName = getCountryNameFromCountryCode(countryCode);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        int position = getIndexByString(spinner, countryName);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(this);
    }

    public void startGetAllWalletInfosAsyncTask() {
        GetAllWalletInfosAsyncTask getAllWalletInfosAsyncTask = new GetAllWalletInfosAsyncTask(this);
        getAllWalletInfosAsyncTask.execute(email, token);

    }

    @Override
    public void getAllWalletInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject accountJson = jsonObject.getJSONObject("account");

            String firstName = accountJson.getString("first_name");
            String lastName = accountJson.getString("last_name");
            String address = accountJson.getString("address_line1");
            String streetNumber = accountJson.getString("address_line2");
            String postalCode = accountJson.getString("postal_code");
            String city = accountJson.getString("city");
            String region = accountJson.getString("region");
            countryCode = accountJson.getString("country");
            nationalityCode = accountJson.getString("nationality");
            residencePlaceCode = accountJson.getString("country_of_residence");

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            addressEditText.setText(address);
            streetNumberEditText.setText(streetNumber);
            postalCodeEditText.setText(postalCode);
            cityEditText.setText(city);
            regionEditText.setText(region);

            setSpinner(countries, countrySpinner, countryCode);
            setSpinner(countries, residenceSpinner, residencePlaceCode);
            setSpinner(countries, nationalitySpinner, nationalityCode);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.country_name_spinner:
                currentCountry = adapterView.getItemAtPosition(i).toString();
                countryCode = getCountryCodeFromCountryName(currentCountry);
                break;
            case R.id.residence_place_spinner:
                currentResidencePlace = adapterView.getItemAtPosition(i).toString();
                residencePlaceCode = getCountryCodeFromCountryName(currentResidencePlace);
                break;
            case R.id.nationality_spinner:
                currentNationality = adapterView.getItemAtPosition(i).toString();
                nationalityCode = getCountryCodeFromCountryName(currentNationality);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String getCountryNameFromCountryCode(String countryCode) {
        String countryName = "";
        for (Locale locale : locales) {
            String newCountryCode = locale.getCountry();
            if (newCountryCode.equals(countryCode)) {
                countryName = locale.getDisplayCountry();
            }
        }

        return countryName;
    }

    public String getCountryCodeFromCountryName(String countryName) {
        String newCountryCode = "";
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.equals(countryName)) {
                newCountryCode = locale.getCountry();
            }
        }

        return newCountryCode;
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
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String streetNumber = streetNumberEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String region = regionEditText.getText().toString();

        CreateNewWalletAsyncTask createNewWalletAsyncTask = new CreateNewWalletAsyncTask(this);
        createNewWalletAsyncTask.execute(email, token, firstName, lastName, address,
                streetNumber, postalCode, city, region, countryCode, residencePlaceCode, nationalityCode);

    }

    @Override
    public void confirmationCreationNewWallet(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");

            if (message.equals("true")) {
                Toast.makeText(getContext(), R.string.updating_wallet_success_toast_message, Toast.LENGTH_SHORT).show();

            } else if (message.equals("errors")) {
                Toast.makeText(getContext(), R.string.updating_wallet_error_toast_message, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
