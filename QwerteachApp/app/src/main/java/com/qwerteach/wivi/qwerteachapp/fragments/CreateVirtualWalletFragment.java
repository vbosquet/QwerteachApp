package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.qwerteach.wivi.qwerteachapp.VirtualWalletActivity;
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
 * Created by wivi on 29/11/16.
 */

public class CreateVirtualWalletFragment extends Fragment implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener {

    View view;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText,
            postalCodeEditText, cityEditText, regionEditText;
    ArrayList<String> countries;
    String currentCountry, currentRegion, currentNationality, countryCode, regionCode, nationalityCode;
    Button saveButton;
    Locale[] locales;
    ProgressDialog progressDialog;
    QwerteachService service;
    User user;

    public static CreateVirtualWalletFragment newInstance() {
        CreateVirtualWalletFragment createVirtualWalletFragment = new CreateVirtualWalletFragment();
        return createVirtualWalletFragment;
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
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);

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
        view = inflater.inflate(R.layout.fragment_create_virtual_wallet, container, false);

        getActivity().setTitle(getResources().getString(R.string.create_new_virtual_wallet_fragment_title));

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

        setSpinner(countries, countrySpinner);
        setSpinner(countries, residenceSpinner);
        setSpinner(countries, nationalitySpinner);

        getUserInfos();

        return  view;
    }

    public void setSpinner(ArrayList arrayList, Spinner spinner) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void getUserInfos() {
        Call<JsonResponse> call = service.getUserInfos(user.getUserId(), user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                User user = response.body().getUser();
                firstNameEditText.setText(user.getFirstName());
                lastNameEditText.setText(user.getLastName());
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
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
        UserWalletInfos userWalletInfos = new UserWalletInfos();
        userWalletInfos.setFirstName(firstNameEditText.getText().toString());
        userWalletInfos.setLastName(lastNameEditText.getText().toString());
        userWalletInfos.setAddress(addressEditText.getText().toString());
        userWalletInfos.setStreetNumber(streetNumberEditText.getText().toString());
        userWalletInfos.setPostalCode(postalCodeEditText.getText().toString());
        userWalletInfos.setCity(cityEditText.getText().toString());
        userWalletInfos.setRegion(regionEditText.getText().toString());
        userWalletInfos.setCountryCode(countryCode);
        userWalletInfos.setResidencePlaceCode(regionCode);
        userWalletInfos.setNationalityCode(nationalityCode);

        Map<String, UserWalletInfos> requestBody = new HashMap<>();
        requestBody.put("account", userWalletInfos);

        startProgressDialog();
        Call<JsonResponse> call = service.updateUserWallet(requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();
                progressDialog.dismiss();
                if (message.equals("true")) {
                    Toast.makeText(getContext(), R.string.registration_new_wallet_success_toast_message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), VirtualWalletActivity.class);
                    startActivity(intent);
                } else if (message.equals("false")) {
                    String errorMessage = response.body().getErrorMesage();
                    if (errorMessage.equals("Internal Server Error")) {
                        Toast.makeText(getContext(), R.string.internal_server_error, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.registration_new_wallet_error_toast_messsage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.toString());
            }
        });
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

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
