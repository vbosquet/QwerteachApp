package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
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

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Transaction transaction = getItem(position);
        TransactionAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new TransactionAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.transaction_list_view, parent, false);
            viewHolder.transactionDate = (TextView) convertView.findViewById(R.id.transaction_date_text_view);
            viewHolder.transactionType = (TextView) convertView.findViewById(R.id.transaction_type_tewt_view);
            viewHolder.transactionAuthor = (TextView) convertView.findViewById(R.id.transaction_author_text_view);
            viewHolder.creditedUserName = (TextView) convertView.findViewById(R.id.credited_user_text_view);
            viewHolder.creditedAmount = (TextView) convertView.findViewById(R.id.credited_amount_text_view);
            viewHolder.debitedAmount = (TextView) convertView.findViewById(R.id.debited_amount_text_view);
            viewHolder.fees = (TextView) convertView.findViewById(R.id.fees_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TransactionAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.transactionDate.setText("Date du paiement : " + transaction.getDate());
        viewHolder.transactionType.setText("Type de paiement : " + transaction.getType());
        viewHolder.transactionAuthor.setText("Donneur d'ordre : " + transaction.getAuthorName());
        viewHolder.creditedUserName.setText("Bénéficiaire : " + transaction.getCreditedUserName());
        viewHolder.creditedAmount.setText("Montant crédité : " + transaction.getCreditedAmount());
        viewHolder.debitedAmount.setText("Montant débité : " + transaction.getDebitedAmount());
        viewHolder.fees.setText("Frais : " + transaction.getFees());

        return convertView;
    }

    public static class ViewHolder {
        TextView transactionDate;
        TextView transactionType;
        TextView transactionAuthor;
        TextView creditedUserName;
        TextView creditedAmount;
        TextView debitedAmount;
        TextView fees;
    }
}
