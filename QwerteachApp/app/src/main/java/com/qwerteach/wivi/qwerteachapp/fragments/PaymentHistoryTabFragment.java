package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.FindUsersByMangoIdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllWalletInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wivi on 8/12/16.
 */

public class PaymentHistoryTabFragment extends Fragment implements GetAllWalletInfosAsyncTask.IGetAllWalletInfos,
        FindUsersByMangoIdAsyncTask.IFindUsersByMangoId{

    View view;
    String email, token;
    ArrayList<Transaction> transactions;
    ListView transactionsListView;

    public static PaymentHistoryTabFragment newInstance() {
        PaymentHistoryTabFragment paymentHistoryTabFragment = new PaymentHistoryTabFragment();
        return  paymentHistoryTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        transactions = new ArrayList<>();

        startGetAllWalletInfosAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_payment_history_tab, container, false);
        transactionsListView = (ListView) view.findViewById(R.id.transactions_list_view);
        return view;
    }

    @Override
    public void getAllWalletInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("transactions");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                JSONObject debitedFunds = jsonData.getJSONObject("debited_funds");
                int debitedAmount = debitedFunds.getInt("amount") / 100;
                String debitedCurrency = debitedFunds.getString("currency");
                JSONObject creditedFunds = jsonData.getJSONObject("credited_funds");
                int creditedAmount = creditedFunds.getInt("amount") / 100;
                String creditedCurrency = creditedFunds.getString("currency");
                JSONObject fees = jsonData.getJSONObject("fees");
                int feesAmount = fees.getInt("amount") / 100;
                String feesCurrency = fees.getString("currency");
                int transactionId = jsonData.getInt("id");
                int authorId = jsonData.getInt("author_id");
                int creditedUserId = jsonData.getInt("credited_user_id");
                String type = jsonData.getString("type");
                String date = jsonData.getString("creation_date");
                String transactionDate = getDate(date);

                Transaction transaction = new Transaction(transactionId, transactionDate, type, authorId,
                        creditedUserId, creditedAmount + " " + creditedCurrency,
                        debitedAmount + " " + debitedCurrency, feesAmount + " " + feesCurrency);
                transactions.add(transaction);
            }

            for (int i = 0; i < transactions.size(); i++) {
                startFindUsersByMangoIdAsyncTask(transactions.get(i).getAuthorId(), transactions.get(i).getCreditedUserId(),
                        transactions.get(i).getTransactionId());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startGetAllWalletInfosAsyncTask() {
        GetAllWalletInfosAsyncTask getAllWalletInfosAsyncTask = new GetAllWalletInfosAsyncTask(this);
        getAllWalletInfosAsyncTask.execute(email, token);

    }

    public void startFindUsersByMangoIdAsyncTask(int authorId, int creditedUserId, int transactionId) {
        FindUsersByMangoIdAsyncTask findUsersByMangoIdAsyncTask = new FindUsersByMangoIdAsyncTask(this);
        findUsersByMangoIdAsyncTask.execute(email, token, authorId, creditedUserId, transactionId);
    }

    private String getDate(String timeStamp) {
        long newTimeStamp = Long.parseLong(timeStamp) * 1000L;
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date netDate = (new Date(newTimeStamp));
        return sdf.format(netDate);
    }

    @Override
    public void displayUsersInfos(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String authorName = jsonObject.getString("author");
            String creditedUserName = jsonObject.getString("credited_user");
            int transactionId = jsonObject.getInt("transaction");

            for (int i = 0; i < transactions.size(); i++) {
                int id = transactions.get(i).getTransactionId();

                if (id == transactionId) {
                    transactions.get(i).setAuthorName(authorName);
                    transactions.get(i).setCreditedUserName(creditedUserName);
                }

                if (id == transactions.get(transactions.size() - 1).getTransactionId()) {
                    setTransactionsListView();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setTransactionsListView() {
        TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), transactions);
        transactionsListView.setAdapter(transactionAdapter);

    }
}
