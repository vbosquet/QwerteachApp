package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 9/12/16.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;

    public TransactionAdapter(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.transactionDate.setText("Date du paiement : " + transaction.getDate());
        holder.transactionType.setText("Type de paiement : " + transaction.getType());
        holder.transactionAuthor.setText("Donneur d'ordre : " + transaction.getAuthorName());
        holder.creditedUserName.setText("Bénéficiaire : " + transaction.getCreditedUserName());
        holder.creditedAmount.setText("Montant crédité : " + transaction.getCreditedFund().getAmount()/100 + " " + transaction.getCreditedFund().getCurrency());
        holder.debitedAmount.setText("Montant débité : " + transaction.getDebitedFund().getAmount()/100 + " " + transaction.getDebitedFund().getCurrency());
        holder.fees.setText("Frais : " + transaction.getFee().getAmount() + " " + transaction.getFee().getCurrency());

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView transactionDate, transactionType, transactionAuthor,
                creditedUserName, creditedAmount, debitedAmount, fees;

        public ViewHolder(View itemView) {
            super(itemView);

            transactionDate = (TextView) itemView.findViewById(R.id.transaction_date_text_view);
            transactionType = (TextView) itemView.findViewById(R.id.transaction_type_tewt_view);
            transactionAuthor = (TextView) itemView.findViewById(R.id.transaction_author_text_view);
            creditedUserName = (TextView) itemView.findViewById(R.id.credited_user_text_view);
            creditedAmount = (TextView) itemView.findViewById(R.id.credited_amount_text_view);
            debitedAmount = (TextView) itemView.findViewById(R.id.debited_amount_text_view);
            fees = (TextView) itemView.findViewById(R.id.fees_text_view);
        }
    }
}
