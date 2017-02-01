package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.UserWalletInfos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 8/12/16.
 */

public class UserInfosTabFragment extends Fragment implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    View view;
    String email, token, countryCode, nationalityCode, residencePlaceCode, currentCountry, currentNationality, currentResidencePlace;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText, postalCodeEditText, cityEditText, regionEditText;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    ArrayList<String> countries;
    Locale[] locales;
    Button saveButton;
    UserWalletInfos user;
    QwerteachService service;

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
        service = ApiClient.getClient().create(QwerteachService.class);

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);

        user = (UserWalletInfos) getArguments().getSerializable("user");

        if (user != null) {
            countryCode = user.getCountryCode();
            nationalityCode = user.getNationalityCode();
            residencePlaceCode = user.getResidencePlaceCode();
        }
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

        displayUserInfos();

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

    public void displayUserInfos() {

        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        addressEditText.setText(user.getAddress());
        streetNumberEditText.setText(user.getStreetNumber());
        postalCodeEditText.setText(user.getPostalCode());
        cityEditText.setText(user.getCity());
        regionEditText.setText(user.getRegion());

        setSpinner(countries, countrySpinner, countryCode);
        setSpinner(countries, residenceSpinner, residencePlaceCode);
        setSpinner(countries, nationalitySpinner, nationalityCode);

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
        UserWalletInfos userWalletInfos = new UserWalletInfos();
        userWalletInfos.setFirstName(firstNameEditText.getText().toString());
        userWalletInfos.setLastName(lastNameEditText.getText().toString());
        userWalletInfos.setAddress(addressEditText.getText().toString());
        userWalletInfos.setStreetNumber(streetNumberEditText.getText().toString());
        userWalletInfos.setPostalCode(postalCodeEditText.getText().toString());
        userWalletInfos.setCity(cityEditText.getText().toString());
        userWalletInfos.setRegion(regionEditText.getText().toString());
        userWalletInfos.setCountryCode(countryCode);
        userWalletInfos.setResidencePlaceCode(residencePlaceCode);
        userWalletInfos.setNationalityCode(nationalityCode);

        Map<String, UserWalletInfos> requestBody = new HashMap<>();
        requestBody.put("account", userWalletInfos);

        Call<JsonResponse> call = service.updateUserWallet(requestBody, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();
                if (message.equals("true")) {

                    Toast.makeText(getContext(), R.string.updating_wallet_success_toast_message, Toast.LENGTH_SHORT).show();

                } else if (message.equals("errors")) {
                    Toast.makeText(getContext(), R.string.updating_wallet_error_toast_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }
}
