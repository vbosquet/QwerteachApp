package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 8/12/16.
 */

public class PaymentHistoryTabFragment extends Fragment implements View.OnClickListener {

    View view;
    ArrayList<Transaction> transactions;
    RecyclerView transactionsRecyclerView;
    RecyclerView.Adapter transactionAdapter;
    RecyclerView.LayoutManager transactionLayoutManager;
    FloatingActionButton floatingActionButton;
    ProgressDialog progressDialog;
    int page = 1, scrollPosition;
    QwerteachService service;
    User user;

    public static PaymentHistoryTabFragment newInstance() {
        PaymentHistoryTabFragment paymentHistoryTabFragment = new PaymentHistoryTabFragment();
        return  paymentHistoryTabFragment;
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
        view  = inflater.inflate(R.layout.fragment_payment_history_tab, container, false);
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactions_recycler_view);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);

        transactions = (ArrayList<Transaction>) getArguments().getSerializable("transactions");

        if (transactions != null && transactions.size() > 0) {
            startProgressDialog();
            for (int i = 0; i < transactions.size(); i++) {
                startFindUsersByMangoId(transactions.get(i).getAuthorId(), transactions.get(i).getCreditedUserId(),
                        transactions.get(i).getTransactionId());
            }

        }

        return view;
    }

    public void startGetAllWalletInfos() {
        startProgressDialog();
        Call<JsonResponse> call = service.getAllWallletInfos(page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                ArrayList<Transaction> transactionList = response.body().getTransactions();
                for (int i = 0; i < transactionList.size(); i++) {
                    transactions.add(transactionList.get(i));
                    startFindUsersByMangoId(transactions.get(i).getAuthorId(), transactions.get(i).getCreditedUserId(),
                            transactions.get(i).getTransactionId());
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void startFindUsersByMangoId(String authorId, String creditedUserId, final String transactionId) {
        Map<String, String> data = new HashMap<>();
        data.put("author_id", authorId);
        data.put("credited_user_id", creditedUserId);

        Call<JsonResponse> call = service.getTransactionInfos(data, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String authorName = response.body().getTransactionAuthorName();
                String creditedUserName = response.body().getTransactionCreditedUserName();

                for (int i = 0; i < transactions.size(); i++) {
                    String id = transactions.get(i).getTransactionId();

                    if (id.equals(transactionId)) {
                        transactions.get(i).setAuthorName(authorName);
                        transactions.get(i).setCreditedUserName(creditedUserName);
                    }

                    if (id.equals(transactions.get(transactions.size() - 1).getTransactionId())) {
                        progressDialog.dismiss();
                        setTransactionsListView();
                    }
                }

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

    public void setTransactionsListView() {
        transactionAdapter = new TransactionAdapter(transactions);
        transactionsRecyclerView.setHasFixedSize(true);
        transactionLayoutManager = new LinearLayoutManager(getContext());
        transactionsRecyclerView.setLayoutManager(transactionLayoutManager);
        transactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        transactionsRecyclerView.setAdapter(transactionAdapter);
        transactionsRecyclerView.scrollToPosition(scrollPosition);

    }

    @Override
    public void onClick(View view) {
        page += 1;
        scrollPosition = transactions.size() - 1;
        startGetAllWalletInfos();
    }
}
