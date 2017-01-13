package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.CreateNewWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayInfosProfileAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.ShowProfileInfosAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by wivi on 29/11/16.
 */

public class CreateVirtualWalletFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        CreateNewWalletAsyncTask.ICreateNewWallet,
        ShowProfileInfosAsyncTask.IShowProfileInfos {

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
    ProgressDialog progressDialog;

    public static CreateVirtualWalletFragment newInstance() {
        CreateVirtualWalletFragment createVirtualWalletFragment = new CreateVirtualWalletFragment();
        return createVirtualWalletFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        locales = Locale.getAvailableLocales();
        countries = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);

        setHasOptionsMenu(true);
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
        setSpinner(countries, countrySpinner);
        residenceSpinner = (Spinner) view.findViewById(R.id.residence_place_spinner);
        setSpinner(countries, residenceSpinner);
        nationalitySpinner = (Spinner) view.findViewById(R.id.nationality_spinner);
        setSpinner(countries, nationalitySpinner);

        saveButton = (Button) view.findViewById(R.id.save_infos_button);
        saveButton.setOnClickListener(this);

        ShowProfileInfosAsyncTask showProfileInfosAsyncTask = new ShowProfileInfosAsyncTask(this);
        showProfileInfosAsyncTask.execute(userId, email, token);

        return  view;
    }

    public void setSpinner(ArrayList arrayList, Spinner spinner) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
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
        createNewWalletAsyncTask.execute(email, token, firstName, lastName, address,
                streetNumber, postalCode, city, region, countryCode, regionCode, nationalityCode);
        startProgressDialog();
    }

    @Override
    public void confirmationCreationNewWallet(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");

            if (message.equals("true")) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.registration_new_wallet_success_toast_message, Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();

            } else if (message.equals("errors")) {
                Toast.makeText(getContext(), R.string.registration_new_wallet_erros_toast_messsage, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        if (menu.findItem(R.id.reload_wallet_button) != null) {
            menu.findItem(R.id.reload_wallet_button).setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void showProfileInfos(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject userJson = jsonObject.getJSONObject("user");

            String firstName = userJson.getString("firstname");
            String lastName = userJson.getString("lastname");

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}