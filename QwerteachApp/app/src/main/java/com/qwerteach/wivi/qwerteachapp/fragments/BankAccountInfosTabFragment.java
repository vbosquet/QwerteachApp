package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.VirtualWalletActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccountAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCardAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 8/12/16.
 */

public class BankAccountInfosTabFragment extends Fragment  implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    View view;
    String type;
    ArrayList<UserCreditCard> userCreditCards;
    ArrayList<UserBankAccount> userBankAccounts;
    RecyclerView userCardsRecyclerView, userBankAccountRecyclerView;
    RecyclerView.Adapter userCardAdapter, userBankAccountAdapter;
    RecyclerView.LayoutManager userCardLayoutManager, userBankAccountLayoutManager;
    FloatingActionButton floatingActionButton;
    CheckBox ibanCheckbox, ukCheckbox, usaCheckbox, canadaCheckbox, otherCheckbox;
    LinearLayout ibanLinearLayout, ukLinearLayout, usaLinearLayout, canadaLinearLayout, otherLinearLayout;
    EditText ibanEditText, bicEditText, ukBankAccountNumber, ukBankAccountCode, usaBankAccountNumber,
            usaABA, usaBankAccountType, canadaBankName, canadaBankNumber, canadaBranchCode,
            canadaBankAccountNumber, otherCountry, otherBIC, otherBankAccountNumber;
    ProgressDialog progressDialog;
    boolean isTeacher;
    TextView bankAccountTextView;
    CoordinatorLayout bankAccountCoordinatorLayout;
    QwerteachService service;
    User user;

    public static BankAccountInfosTabFragment newInstance() {
        BankAccountInfosTabFragment bankAccountInfosTabFragment = new BankAccountInfosTabFragment();
        return  bankAccountInfosTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);
        isTeacher = preferences.getBoolean("isTeacher", false);

        userCreditCards = new ArrayList<>();
        userBankAccounts = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_bank_account_infos_tab, container, false);
        userCardsRecyclerView = (RecyclerView) view.findViewById(R.id.user_cards_recycler_view);
        userBankAccountRecyclerView = (RecyclerView) view.findViewById(R.id.user_bank_accounts_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        bankAccountTextView = (TextView) view.findViewById(R.id.bank_account_text_view);
        bankAccountCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.ban_account_coordinator_layout);
        floatingActionButton.setOnClickListener(this);
        userCreditCards = (ArrayList<UserCreditCard>) getArguments().getSerializable("userCreditCards");
        userBankAccounts = (ArrayList<UserBankAccount>) getArguments().getSerializable("userBankAccounts");
        setUserCreditCardsrecyclerView();

        if (isTeacher) {
            bankAccountTextView.setVisibility(View.VISIBLE);
            bankAccountCoordinatorLayout.setVisibility(View.VISIBLE);
            setUserBankAccountsRecyclerView();
        }
        return view;
    }

    public void setUserCreditCardsrecyclerView() {
        userCardAdapter = new UserCreditCardAdapter(userCreditCards);
        userCardsRecyclerView.setHasFixedSize(true);
        userCardLayoutManager = new LinearLayoutManager(getContext());
        userCardsRecyclerView.setLayoutManager(userCardLayoutManager);
        userCardsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userCardsRecyclerView.setAdapter(userCardAdapter);
    }

    public void setUserBankAccountsRecyclerView() {
        userBankAccountAdapter = new UserBankAccountAdapter(userBankAccounts, this);
        userBankAccountRecyclerView.setHasFixedSize(true);
        userBankAccountLayoutManager = new LinearLayoutManager(getContext());
        userBankAccountRecyclerView.setLayoutManager(userBankAccountLayoutManager);
        userBankAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userBankAccountRecyclerView.setAdapter(userBankAccountAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.floating_action_button:
                createNewBankAccountAlertDialog();
                break;
        }

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
                String success = response.body().getSuccess();
                String message = response.body().getMessage();
                displayConfirmationMessage(message, success);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

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

    public void didTouchDeleteBankAccountButton(String bankAccountId) {
        startProgressDialog();
        Call<JsonResponse> call = service.desactivateBankAccount(bankAccountId, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess();
                String message = response.body().getMessage();
                displayConfirmationMessage(message, success);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void displayConfirmationMessage(String message, String success) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        if (success.equals("true")) {
            Intent intent = new Intent(getContext(), VirtualWalletActivity.class);
            startActivity(intent);
        }
    }
}
