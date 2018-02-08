package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ReloadWalletActivity;
import com.qwerteach.wivi.qwerteachapp.UnloadWalletActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccountAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCardAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UserWalletInfos;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 7/05/17.
 */

public class EditVirtualWalletFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    View view;
    ArrayList<UserCreditCard> creditCards;
    ArrayList<UserBankAccount> bankAccounts;
    RecyclerView creditCardsRecyclerView, bankAccountsRecyclerView;
    RecyclerView.Adapter creditCardAdapter, bankAccountAdapter;
    RecyclerView.LayoutManager creditCardLayoutManager, bankAccountLayoutManager;
    LinearLayout newBankAccount, ibanLinearLayout, ukLinearLayout, usaLinearLayout, canadaLinearLayout, otherLinearLayout, bankAccountsCard;
    ImageView newBankAccountIcon;
    UserWalletInfos accountInfos;
    EditText firstNameEditText, lastNameEditText, addressEditText, streetNumberEditText, postalCodeEditText, cityEditText,
            regionEditText, ibanEditText, bicEditText, ukBankAccountNumber, ukBankAccountCode, usaBankAccountNumber,
            usaABA, usaBankAccountType, canadaBankName, canadaBankNumber, canadaBranchCode,
            canadaBankAccountNumber, otherCountry, otherBIC, otherBankAccountNumber;
    QwerteachService service;
    ArrayList<String> countries;
    Locale[] locales;
    String countryCode, nationalityCode, residencePlaceCode, currentCountry, currentNationality, currentResidencePlace, type;
    Spinner countrySpinner, residenceSpinner, nationalitySpinner;
    Button saveButton;
    ProgressDialog progressDialog;
    User user;
    CheckBox ibanCheckbox, ukCheckbox, usaCheckbox, canadaCheckbox, otherCheckbox;
    Intent intent;

    public static EditVirtualWalletFragment newInstance() {
        EditVirtualWalletFragment editVirtualWalletFragment = new EditVirtualWalletFragment();
        return editVirtualWalletFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        Bundle bundle = this.getArguments();

        if(bundle != null){
            creditCards = (ArrayList<UserCreditCard>) getArguments().getSerializable("creditCards");
            bankAccounts = (ArrayList<UserBankAccount>) getArguments().getSerializable("bankAccounts");
            accountInfos = (UserWalletInfos) getArguments().getSerializable("accountInfos");
        }

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(getContext());
        locales = Locale.getAvailableLocales();
        countries = new ArrayList<>();
        setUpCountries();
        setHasOptionsMenu(true);

        if (accountInfos != null) {
            countryCode = accountInfos.getCountryCode();
            nationalityCode = accountInfos.getNationalityCode();
            residencePlaceCode = accountInfos.getResidencePlaceCode();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_virtual_wallet, container, false);
        creditCardsRecyclerView = (RecyclerView) view.findViewById(R.id.credit_cards_list);
        bankAccountsRecyclerView = (RecyclerView) view.findViewById(R.id.bank_accounts_list);
        newBankAccount = (LinearLayout) view.findViewById(R.id.new_bank_account);
        newBankAccountIcon = (ImageView) view.findViewById(R.id.new_bank_account_icon);
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
        bankAccountsCard = (LinearLayout) view.findViewById(R.id.bank_accounts_card);
        saveButton.setOnClickListener(this);
        newBankAccount.setOnClickListener(this);

        if (creditCards.size() > 0) {
            displayCreditCardsList();
        }

        if (user.getPostulanceAccepted()) {
            bankAccountsCard.setVisibility(View.VISIBLE);
            if (bankAccounts.size() > 0) {
                bankAccountsRecyclerView.setVisibility(View.VISIBLE);
                displayBankAccountsList();
            }
        }

        displayUserInfos();

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.virtual_wallet_menu, menu);

        if(user.getPostulanceAccepted()) {
            MenuItem menuItem = menu.findItem(R.id.unload_wallet_button);
            menuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload_wallet_button:
                intent = new Intent(getContext(), ReloadWalletActivity.class);
                intent.putExtra("easy_payment", creditCards);
                startActivity(intent);
                return true;
            case R.id.unload_wallet_button:
                intent = new Intent(getContext(), UnloadWalletActivity.class);
                intent.putExtra("userBankAccounts", bankAccounts);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpCountries() {
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
    }

    public void displayCreditCardsList() {
        creditCardAdapter = new UserCreditCardAdapter(creditCards, getContext());
        creditCardsRecyclerView.setHasFixedSize(true);
        creditCardLayoutManager = new LinearLayoutManager(getContext());
        creditCardsRecyclerView.setLayoutManager(creditCardLayoutManager);
        creditCardsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        creditCardsRecyclerView.setAdapter(creditCardAdapter);
    }

    public void displayBankAccountsList() {
        bankAccountAdapter = new UserBankAccountAdapter(bankAccounts, getContext());
        bankAccountsRecyclerView.setHasFixedSize(true);
        bankAccountLayoutManager = new LinearLayoutManager(getContext());
        bankAccountsRecyclerView.setLayoutManager(bankAccountLayoutManager);
        bankAccountsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bankAccountsRecyclerView.setAdapter(bankAccountAdapter);
    }

    public void displayUserInfos() {
        firstNameEditText.setText(accountInfos.getFirstName());
        lastNameEditText.setText(accountInfos.getLastName());
        addressEditText.setText(accountInfos.getAddress());
        streetNumberEditText.setText(accountInfos.getStreetNumber());
        postalCodeEditText.setText(accountInfos.getPostalCode());
        cityEditText.setText(accountInfos.getCity());
        regionEditText.setText(accountInfos.getRegion());

        setSpinner(countries, countrySpinner, countryCode);
        setSpinner(countries, residenceSpinner, residencePlaceCode);
        setSpinner(countries, nationalitySpinner, nationalityCode);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_infos_button:
                saveUserAccountInfos();
                break;
            case R.id.new_bank_account:
                createNewBankAccountAlertDialog();
                break;
        }
    }

    public void saveUserAccountInfos() {
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
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    if (message.equals("true")) {
                        getFragmentManager().popBackStack();
                        Toast.makeText(getContext(), R.string.updating_wallet_success_toast_message, Toast.LENGTH_SHORT).show();

                    } else if (message.equals("false")) {
                        Toast.makeText(getContext(), R.string.updating_wallet_error_toast_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("failure", String.valueOf(t.getMessage()));
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
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

    public void createNewBankAccountAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.alert_dialog_new_bank_account, null);
        builder.setView(content);

        ibanEditText = (EditText) content.findViewById(R.id.iban_edit_text);
        bicEditText = (EditText) content.findViewById(R.id.bic_edit_text);
        ukBankAccountNumber = (EditText) content.findViewById(R.id.united_kingdom_bank_account_number_edit_text);
        ukBankAccountCode = (EditText) content.findViewById(R.id.united_kingdom_bank_account_code_edit_text);
        usaBankAccountNumber = (EditText) content.findViewById(R.id.usa_bank_account_number_edit_text);
        usaABA = (EditText) content.findViewById(R.id.usa_aba_edit_text);
        usaBankAccountType = (EditText) content.findViewById(R.id.usa_account_type_edit_text);
        canadaBankName = (EditText) content.findViewById(R.id.canada_bank_name_edit_text);
        canadaBankNumber = (EditText) content.findViewById(R.id.canada_bank_number_edit_text);
        canadaBranchCode = (EditText) content.findViewById(R.id.canada_branch_code);
        canadaBankAccountNumber = (EditText) content.findViewById(R.id.canada_bank_account_number_edit_text);
        otherCountry = (EditText) content.findViewById(R.id.other_country_edit_text);
        otherBIC = (EditText) content.findViewById(R.id.other_bic_edit_text);
        otherBankAccountNumber = (EditText) content.findViewById(R.id.other_bank_account_number_edit_text);

        ibanLinearLayout = (LinearLayout) content.findViewById(R.id.iban_linear_layout);
        ukLinearLayout = (LinearLayout) content.findViewById(R.id.united_kingdom_linear_layout);
        usaLinearLayout = (LinearLayout) content.findViewById(R.id.usa_linear_layout);
        canadaLinearLayout = (LinearLayout) content.findViewById(R.id.canada_linear_layout);
        otherLinearLayout = (LinearLayout) content.findViewById(R.id.other_linear_layout);

        ibanCheckbox = (CheckBox) content.findViewById(R.id.iban_checkbox);
        ukCheckbox = (CheckBox) content.findViewById(R.id.united_kingdom_checkbox);
        usaCheckbox = (CheckBox) content.findViewById(R.id.usa_checkbox);
        canadaCheckbox = (CheckBox) content.findViewById(R.id.canada_checkbox);
        otherCheckbox = (CheckBox) content.findViewById(R.id.other_checkbox);

        ibanCheckbox.setOnCheckedChangeListener(this);
        ukCheckbox.setOnCheckedChangeListener(this);
        usaCheckbox.setOnCheckedChangeListener(this);
        canadaCheckbox.setOnCheckedChangeListener(this);
        otherCheckbox.setOnCheckedChangeListener(this);

        builder.setPositiveButton("Terminer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addNewBankAccount();
                startProgressDialog();
            }
        });

        builder.create().show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.iban_checkbox:
                if (isChecked) {
                    ukCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);

                    ibanLinearLayout.setVisibility(View.VISIBLE);
                    ukLinearLayout.setVisibility(View.GONE);
                    usaLinearLayout.setVisibility(View.GONE);
                    canadaLinearLayout.setVisibility(View.GONE);
                    otherLinearLayout.setVisibility(View.GONE);

                    type = "iban";
                }
                break;
            case R.id.united_kingdom_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);

                    ibanLinearLayout.setVisibility(View.GONE);
                    ukLinearLayout.setVisibility(View.VISIBLE);
                    usaLinearLayout.setVisibility(View.GONE);
                    canadaLinearLayout.setVisibility(View.GONE);
                    otherLinearLayout.setVisibility(View.GONE);

                    type = "gb";
                }
                break;
            case R.id.usa_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    ukCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);

                    ibanLinearLayout.setVisibility(View.GONE);
                    ukLinearLayout.setVisibility(View.GONE);
                    usaLinearLayout.setVisibility(View.VISIBLE);
                    canadaLinearLayout.setVisibility(View.GONE);
                    otherLinearLayout.setVisibility(View.GONE);

                    type = "us";
                }
                break;
            case R.id.canada_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    ukCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);

                    ibanLinearLayout.setVisibility(View.GONE);
                    ukLinearLayout.setVisibility(View.GONE);
                    usaLinearLayout.setVisibility(View.GONE);
                    canadaLinearLayout.setVisibility(View.VISIBLE);
                    otherLinearLayout.setVisibility(View.GONE);

                    type = "ca";
                }
                break;
            case R.id.other_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    ukCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);

                    ibanLinearLayout.setVisibility(View.GONE);
                    ukLinearLayout.setVisibility(View.GONE);
                    usaLinearLayout.setVisibility(View.GONE);
                    canadaLinearLayout.setVisibility(View.GONE);
                    otherLinearLayout.setVisibility(View.VISIBLE);

                    type = "other";
                }
                break;
        }
    }

    public void addNewBankAccount() {
        String iban, bic, accountNumber, sortCode, aba, depositAccountType, bankName, institutionNumber, branchCode, country;
        UserBankAccount ibanAccount = new UserBankAccount();
        UserBankAccount gbAccount = new UserBankAccount();
        UserBankAccount usAccount = new UserBankAccount();
        UserBankAccount caAccount = new UserBankAccount();
        UserBankAccount otherAccount = new UserBankAccount();


        switch (type) {
            case "iban":
                iban = ibanEditText.getText().toString();
                bic = bicEditText.getText().toString();
                ibanAccount.setIban(iban);
                ibanAccount.setBic(bic);
                break;
            case "gb":
                accountNumber = ukBankAccountNumber.getText().toString();
                sortCode = ukBankAccountCode.getText().toString();
                gbAccount.setAccountNumber(accountNumber);
                gbAccount.setSortCode(sortCode);
                break;
            case "us":
                accountNumber = usaBankAccountNumber.getText().toString();
                aba = usaABA.getText().toString();
                depositAccountType = usaBankAccountType.getText().toString();
                usAccount.setAccountNumber(accountNumber);
                usAccount.setAba(aba);
                usAccount.setDepositAccountType(depositAccountType);
                break;
            case "ca":
                bankName = canadaBankName.getText().toString();
                institutionNumber = canadaBankNumber.getText().toString();
                branchCode = canadaBranchCode.getText().toString();
                accountNumber = canadaBankAccountNumber.getText().toString();
                caAccount.setBankName(bankName);
                caAccount.setInstitutionNumber(institutionNumber);
                caAccount.setBranchCode(branchCode);
                caAccount.setAccountNumber(accountNumber);
                break;
            case "other":
                country = otherCountry.getText().toString();
                bic = otherBIC.getText().toString();
                accountNumber = otherBankAccountNumber.getText().toString();
                otherAccount.setCountry(country);
                otherAccount.setAccountNumber(accountNumber);
                otherAccount.setBic(bic);
                break;
        }

        UserBankAccount typeAccount = new UserBankAccount();
        typeAccount.setType(type);

        Map<String, UserBankAccount> data = new HashMap<>();
        data.put("bank_account", typeAccount);
        data.put("iban_account", ibanAccount);
        data.put("gb_account", gbAccount);
        data.put("us_account", usAccount);
        data.put("ca_account", caAccount);
        data.put("other_account", otherAccount);


        Call<JsonResponse> call = service.addNewBankAccount(data, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    String message = response.body().getMessage();
                    getFragmentManager().popBackStack();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("FAILURE", t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
