package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by wivi on 17/01/17.
 */

public class UserBankAccountAdapter extends RecyclerView.Adapter<UserBankAccountAdapter.ViewHolder> {

    private ArrayList<UserBankAccount> userBankAccounts;
    private Context context;

    public UserBankAccountAdapter(ArrayList<UserBankAccount> userBankAccounts, Context context) {
        this.userBankAccounts = userBankAccounts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_bank_account_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserBankAccount userBankAccount = userBankAccounts.get(position);

        holder.cardType.setText(userBankAccount.getType());
        holder.cardNumber.setText(userBankAccount.getIban());
        Picasso.with(context).load(R.drawable.bank_account_icon).resize(50, 50).centerCrop().into(holder.bankAccountIcon);

    }

    @Override
    public int getItemCount() {
        return userBankAccounts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardType, cardNumber;
        ImageView bankAccountIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            cardType = (TextView) itemView.findViewById(R.id.card_type);
            cardNumber = (TextView) itemView.findViewById(R.id.card_number);
            bankAccountIcon = (ImageView) itemView.findViewById(R.id.bank_account_icon);
        }
    }
}
