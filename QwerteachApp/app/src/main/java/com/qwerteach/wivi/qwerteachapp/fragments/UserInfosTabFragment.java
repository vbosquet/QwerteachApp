package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
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
    String countryCode, nationalityCode, residencePlaceCode, currentCountry, currentNationality, currentResidencePlace;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText, postalCodeEditText, cityEditText, regionEditText;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    ArrayList<String> countries;
    Locale[] locales;
    Button saveButton;
    UserWalletInfos userWalletInfos;
    QwerteachService service;
    User user;
    ProgressDialog progressDialog;

    public static UserInfosTabFragment newInstance() {
        UserInfosTabFragment userInfosTabFragment = new UserInfosTabFragment();
        return  userInfosTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        locales = Locale.getAvailableLocales();
        countries = new ArrayList<>();
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(getContext());

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        userWalletInfos = (UserWalletInfos) getArguments().getSerializable("user");

        if (userWalletInfos != null) {
            countryCode = userWalletInfos.getCountryCode();
            nationalityCode = userWalletInfos.getNationalityCode();
            residencePlaceCode = userWalletInfos.getResidencePlaceCode();
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

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        int position = getIndexByString(spinner, countryName);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(this);
    }

    public void displayUserInfos() {
        firstNameEditText.setText(userWalletInfos.getFirstName());
        lastNameEditText.setText(userWalletInfos.getLastName());
        addressEditText.setText(userWalletInfos.getAddress());
        streetNumberEditText.setText(userWalletInfos.getStreetNumber());
        postalCodeEditText.setText(userWalletInfos.getPostalCode());
        cityEditText.setText(userWalletInfos.getCity());
        regionEditText.setText(userWalletInfos.getRegion());

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
        startProgressDialog();

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

        Call<JsonResponse> call = service.updateUserWallet(requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();
                progressDialog.dismiss();
                if (message.equals("true")) {
                    Toast.makeText(getContext(), R.string.updating_wallet_success_toast_message, Toast.LENGTH_SHORT).show();

                } else if (message.equals("false")) {
                    Toast.makeText(getContext(), R.string.updating_wallet_error_toast_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.toString());
            }
        });
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
