package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by wivi on 9/12/16.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionAdapter(ArrayList<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        long millisecond = Long.parseLong(transaction.getCreationDate());
        String status = transaction.getStatus();


        holder.creationDate.setText("Le " + getDate(millisecond) + " à " + getHour(millisecond));
        if (transaction.getTitle().equals("")) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(transaction.getTitle());
        }
        holder.amount.setText(transaction.getCreditedFund().getAmount()/100 + "€");

        if (Objects.equals(status, "SUCCEEDED")) {
            holder.status.setText(R.string.succeeded_transaction_status);
        } else if (Objects.equals(status, "FAILED")) {
            holder.status.setText(R.string.failed_transaction_status);
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        } else if (Objects.equals(status, "CREATED")) {
            holder.status.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, creationDate, amount, status;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.transaction_title);
            creationDate = (TextView) itemView.findViewById(R.id.transaction_creation_date);
            amount = (TextView) itemView.findViewById(R.id.transaction_amount);
            status = (TextView) itemView.findViewById(R.id.transaction_status);
        }
    }

    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch(Exception ex){
            return "xx";
        }
    }

    private String getHour(long timeStamp) {
        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch(Exception ex){
            return "xx";
        }

    }
}
