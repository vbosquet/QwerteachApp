package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ReloadWalletActivity;
import com.qwerteach.wivi.qwerteachapp.UnloadWalletActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.TransactionAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccountAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCardAdapter;
import com.qwerteach.wivi.qwerteachapp.models.UserWalletInfos;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 29/04/17.
 */

public class IndexVirtualWalletFragment extends Fragment implements View.OnClickListener {

    View view;
    ArrayList<UserCreditCard> userCreditCards;
    ArrayList<Transaction> transactions;
    ArrayList<UserBankAccount> userBankAccounts;
    List<String> transactionInfos;
    UserWalletInfos userWalletInfos;
    QwerteachService service;
    User user;
    TextView cardBankAccountsTitle, cardBankAccountsButton, cardCoordonneesButton, transactionsButton, userName, userAddresLine1, userAddressLine2;
    RecyclerView creditCardsRecyclerView, bankAccountsRecyclerView, transactionsRecyclerView;
    RecyclerView.Adapter creditCardAdapter, bankAccountAdapter, transactionAdapter;
    RecyclerView.LayoutManager creditCardLayoutManager, bankAccountLayoutManager, transactionLayoutManager;
    ProgressDialog progressDialog;
    Intent intent;

    public static IndexVirtualWalletFragment newInstance() {
        IndexVirtualWalletFragment indexVirtualWalletFragment = new IndexVirtualWalletFragment();
        return indexVirtualWalletFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index_virtual_wallet, container, false);
        cardBankAccountsTitle = (TextView) view.findViewById(R.id.card_bank_accounts_title);
        cardBankAccountsButton = (TextView) view.findViewById(R.id.card_bank_accounts_button);
        cardCoordonneesButton = (TextView) view.findViewById(R.id.card_coordonnees_button);
        transactionsButton = (TextView) view.findViewById(R.id.transactions_button);
        userName = (TextView) view.findViewById(R.id.user_name);
        userAddresLine1 = (TextView) view.findViewById(R.id.user_address_line_1);
        userAddressLine2 = (TextView) view.findViewById(R.id.user_address_line_2);
        creditCardsRecyclerView = (RecyclerView) view.findViewById(R.id.credit_cards_list);
        bankAccountsRecyclerView = (RecyclerView) view.findViewById(R.id.bank_accounts_list);
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactions_list);

        cardBankAccountsButton.setOnClickListener(this);
        cardCoordonneesButton.setOnClickListener(this);
        transactionsButton.setOnClickListener(this);
        getAllWalletInfos();

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
                startActivity(intent);
                return true;
            case R.id.unload_wallet_button:
                intent = new Intent(getContext(), UnloadWalletActivity.class);
                intent.putExtra("userBankAccounts", userBankAccounts);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getAllWalletInfos() {
        startProgressDialog();
        Call<JsonResponse> call = service.getAllWallletInfos(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                userWalletInfos = response.body().getUserWalletInfos();
                userBankAccounts = response.body().getBankAccounts();
                transactions = response.body().getTransactions();
                userCreditCards = response.body().getUserCreditCards();
                transactionInfos = response.body().getTransactionInfos();

                for (int i = 0; i < transactionInfos.size(); i++) {
                    transactions.get(i).setTitle(transactionInfos.get(i));
                }

                if (user.getPostulanceAccepted()) {
                    displayTeacherAccountsAndCards();
                } else {
                    displayStudentAccountsAndCards();
                }

                displayCardCoordonnees();
                displayTransactionsList();

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                if(t instanceof SocketTimeoutException){;
                    Toast.makeText(getContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void displayTeacherAccountsAndCards() {
        cardBankAccountsTitle.setText(R.string.teacher_accounts_and_cards_text_view);

        if (userCreditCards.size() > 0) {
            displayCreditCardsList();
        }

        if (userBankAccounts.size() > 0) {
            bankAccountsRecyclerView.setVisibility(View.VISIBLE);
            displayBankAccountsList();
        }

    }

    public void displayStudentAccountsAndCards() {
        cardBankAccountsTitle.setText(R.string.student_accounts_and_cards_text_view);
        if (userCreditCards.size() > 0) {
            displayCreditCardsList();
        }

    }

    public void displayCardCoordonnees() {
        userName.setText(userWalletInfos.getFirstName() + " " + userWalletInfos.getLastName());
        userAddresLine1.setText(userWalletInfos.getAddress() + " " + userWalletInfos.getStreetNumber());
        userAddressLine2.setText(userWalletInfos.getPostalCode() + " " + userWalletInfos.getCity());
    }

    public void displayTransactionsList() {
        if (transactions.size() > 0) {
            transactionAdapter = new TransactionAdapter(transactions, getContext());
            transactionsRecyclerView.setHasFixedSize(true);
            transactionLayoutManager = new LinearLayoutManager(getContext());
            transactionsRecyclerView.setLayoutManager(transactionLayoutManager);
            transactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            transactionsRecyclerView.setAdapter(transactionAdapter);

        }
    }

    public void displayCreditCardsList() {
        creditCardAdapter = new UserCreditCardAdapter(userCreditCards, getContext());
        creditCardsRecyclerView.setHasFixedSize(true);
        creditCardLayoutManager = new LinearLayoutManager(getContext());
        creditCardsRecyclerView.setLayoutManager(creditCardLayoutManager);
        creditCardsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        creditCardsRecyclerView.setAdapter(creditCardAdapter);
    }

    public void displayBankAccountsList() {
        bankAccountAdapter = new UserBankAccountAdapter(userBankAccounts, getContext());
        bankAccountsRecyclerView.setHasFixedSize(true);
        bankAccountLayoutManager = new LinearLayoutManager(getContext());
        bankAccountsRecyclerView.setLayoutManager(bankAccountLayoutManager);
        bankAccountsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bankAccountsRecyclerView.setAdapter(bankAccountAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_bank_accounts_button:
                displayEditVirtualWalletFragment();
                break;
            case R.id.card_coordonnees_button:
                displayEditVirtualWalletFragment();
                break;
            case R.id.transactions_button:
                displayTransactionsListFragment();
                break;
        }
    }

    public void displayEditVirtualWalletFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("creditCards", userCreditCards);
        bundle.putSerializable("bankAccounts", userBankAccounts);
        bundle.putSerializable("accountInfos", userWalletInfos);

        Fragment newFragment = EditVirtualWalletFragment.newInstance();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, "EDIT_WALLET");
        transaction.addToBackStack("EDIT_WALLET");
        transaction.commit();
    }

    public void displayTransactionsListFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("transactions", transactions);

        Fragment newFragment = TransactionsListFragment.newInstance();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, "TRANSACTIONS_LIST");
        transaction.addToBackStack("TRANSACTIONS_LIST");
        transaction.commit();
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
}
