package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCardAdapter;

import java.util.ArrayList;

/**
 * Created by wivi on 8/12/16.
 */

public class BankAccountInfosTabFragment extends Fragment  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    View view;
    String email, token;
    ArrayList<UserCreditCard> userCreditCards;
    RecyclerView userCardsRecyclerView;
    RecyclerView.Adapter userCardAdapter;
    RecyclerView.LayoutManager userCardLayoutManager;
    FloatingActionButton floatingActionButton;
    CheckBox ibanCheckbox, unitedKingdomCheckbox, usaCheckbox, canadaCheckbox, otherCheckbox;

    public static BankAccountInfosTabFragment newInstance() {
        BankAccountInfosTabFragment bankAccountInfosTabFragment = new BankAccountInfosTabFragment();
        return  bankAccountInfosTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        userCreditCards = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_bank_account_infos_tab, container, false);
        userCardsRecyclerView = (RecyclerView) view.findViewById(R.id.user_cards_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);
        userCreditCards = (ArrayList<UserCreditCard>) getArguments().getSerializable("userCreditCards");
        setUserCreditCardsListView();
        return view;
    }

    public void setUserCreditCardsListView() {
        userCardAdapter = new UserCreditCardAdapter(userCreditCards);
        userCardsRecyclerView.setHasFixedSize(true);
        userCardLayoutManager = new LinearLayoutManager(getContext());
        userCardsRecyclerView.setLayoutManager(userCardLayoutManager);
        userCardsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userCardsRecyclerView.setAdapter(userCardAdapter);
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

        ibanCheckbox = (CheckBox) content.findViewById(R.id.iban_checkbox);
        unitedKingdomCheckbox = (CheckBox) content.findViewById(R.id.united_kingdom_checkbox);
        usaCheckbox = (CheckBox) content.findViewById(R.id.usa_checkbox);
        canadaCheckbox = (CheckBox) content.findViewById(R.id.canada_checkbox);
        otherCheckbox = (CheckBox) content.findViewById(R.id.other_checkbox);

        ibanCheckbox.setOnCheckedChangeListener(this);
        unitedKingdomCheckbox.setOnCheckedChangeListener(this);
        usaCheckbox.setOnCheckedChangeListener(this);
        canadaCheckbox.setOnCheckedChangeListener(this);
        otherCheckbox.setOnCheckedChangeListener(this);

        builder.setPositiveButton("Terminer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.iban_checkbox:
                if (isChecked) {
                    unitedKingdomCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);
                }
                break;
            case R.id.united_kingdom_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);
                }
                break;
            case R.id.usa_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    unitedKingdomCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);
                }
                break;
            case R.id.canada_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    unitedKingdomCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    otherCheckbox.setChecked(false);
                }
                break;
            case R.id.other_checkbox:
                if (isChecked) {
                    ibanCheckbox.setChecked(false);
                    unitedKingdomCheckbox.setChecked(false);
                    usaCheckbox.setChecked(false);
                    canadaCheckbox.setChecked(false);
                }
                break;
        }
    }
}
