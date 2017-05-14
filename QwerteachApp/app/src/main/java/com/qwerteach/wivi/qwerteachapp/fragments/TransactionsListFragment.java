package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.TransactionAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

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

    public static TransactionsListFragment newInstance() {
        TransactionsListFragment transactionsListFragment = new TransactionsListFragment();
        return transactionsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        transactions = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_transactions_list, container, false);
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactions_recycler_view);
        transactions = (ArrayList<Transaction>) getArguments().getSerializable("transactions");
        if (transactions != null && transactions.size() > 0) {
            setTransactionsListView();
        }

        return view;
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
                ArrayList<Transaction> transactionList = response.body().getTransactions();
                List<String> transactionInfos = response.body().getTransactionInfos();

                if (transactionList.size() > 0) {
                    for (int i = 0; i < transactionList.size(); i++) {
                        transactionList.get(i).setTitle(transactionInfos.get(i));
                        transactions.add(transactionList.get(i));
                    }

                    loading = true;
                    progressDialog.dismiss();
                    setTransactionsListView();

                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }
}
