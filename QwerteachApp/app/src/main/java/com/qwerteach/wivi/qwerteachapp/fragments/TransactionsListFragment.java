package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 8/12/16.
 */

public class TransactionsListFragment extends Fragment {

    View view;
    ArrayList<Transaction> transactions;
    RecyclerView transactionsRecyclerView;
    RecyclerView.Adapter transactionAdapter;
    RecyclerView.LayoutManager transactionLayoutManager;
    ProgressDialog progressDialog;
    int scrollPosition, page = 1;
    QwerteachService service;
    User user;
    boolean loading = true;
    Intent intent;
    ArrayList<UserBankAccount> userBankAccounts;

    public static TransactionsListFragment newInstance() {
        TransactionsListFragment transactionsListFragment = new TransactionsListFragment();
        return transactionsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        transactions = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
        transactions = (ArrayList<Transaction>) getArguments().getSerializable("transactions");
        userBankAccounts = (ArrayList<UserBankAccount>) getArguments().get("userBankAccounts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_transactions_list, container, false);
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactions_recycler_view);
        if (transactions != null && transactions.size() > 0) {
            setTransactionsListView();
        }

        return view;
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

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void setTransactionsListView() {
        transactionAdapter = new TransactionAdapter(transactions, getContext());
        transactionsRecyclerView.setHasFixedSize(true);
        transactionLayoutManager = new LinearLayoutManager(getContext());
        transactionsRecyclerView.setLayoutManager(transactionLayoutManager);
        transactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        transactionsRecyclerView.setAdapter(transactionAdapter);
        transactionsRecyclerView.scrollToPosition(scrollPosition);
        transactionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int total = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (loading) {
                    if (total > 0) {
                        if ((total - 1) == lastVisibleItem) {
                            loading = false;
                            page += 1;
                            scrollPosition = transactions.size() - 1;
                            getMoreTransactions();
                        }
                    }
                }
            }
        });

    }

    public void getMoreTransactions() {
        startProgressDialog();
        Call<JsonResponse> call = service.getMoreTransactions(page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    ArrayList<Transaction> transactionList = response.body().getTransactions();
                    List<String> transactionInfos = response.body().getTransactionInfos();

                    if (transactionList.size() > 0) {
                        for (int i = 0; i < transactionList.size(); i++) {
                            transactionList.get(i).setTitle(transactionInfos.get(i));
                            transactions.add(transactionList.get(i));
                        }
                        setTransactionsListView();
                        loading = true;
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
}
