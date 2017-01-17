package com.qwerteach.wivi.qwerteachapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.BankAccountInfosTabFragment;

import java.util.ArrayList;

/**
 * Created by wivi on 17/01/17.
 */

public class UserBankAccountAdapter extends RecyclerView.Adapter<UserBankAccountAdapter.ViewHolder> {

    private ArrayList<UserBankAccount> userBankAccounts;
    private BankAccountInfosTabFragment fragment;

    public UserBankAccountAdapter(ArrayList<UserBankAccount> userBankAccounts, BankAccountInfosTabFragment fragment) {
        this.userBankAccounts = userBankAccounts;
        this.fragment = fragment;
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

        holder.ownerName.setText(userBankAccount.getOwnerName());

        if (userBankAccount.getType().equals("IBAN")) {
            holder.iban.setText(userBankAccount.getIban());
            holder.iban.setVisibility(View.VISIBLE);
        } else {
            holder.bankAccountNumber.setText(userBankAccount.getAccountNumber());
            holder.bankAccountNumber.setVisibility(View.VISIBLE);
        }

        holder.deleteBankAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchDeleteBankAccountButton(userBankAccount.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userBankAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ownerName, iban, bankAccountNumber, deleteBankAccount;

        public ViewHolder(View itemView) {
            super(itemView);

            ownerName = (TextView) itemView.findViewById(R.id.owner_name);
            iban = (TextView) itemView.findViewById(R.id.iban);
            bankAccountNumber = (TextView) itemView.findViewById(R.id.account_number);
            deleteBankAccount = (TextView) itemView.findViewById(R.id.delete_bank_account);
        }
    }
}
