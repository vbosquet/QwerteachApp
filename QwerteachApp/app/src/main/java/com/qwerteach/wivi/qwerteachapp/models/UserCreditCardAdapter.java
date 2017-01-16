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

public class UserCreditCardAdapter extends RecyclerView.Adapter<UserCreditCardAdapter.ViewHolder> {

    private ArrayList<UserCreditCard> userCreditCards;

    public UserCreditCardAdapter(ArrayList<UserCreditCard> userCreditCards) {
        this.userCreditCards = userCreditCards;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_credit_card_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserCreditCard userCreditCard = userCreditCards.get(position);

        holder.cardProvider.setText(userCreditCard.getCardProvider() + " (" + userCreditCard.getCurrency() + ")");
        holder.cardAlias.setText(userCreditCard.getAlias());
        holder.expirationDate.setText("Expiration : " + userCreditCard.getExpirationDate());
        holder.cardValidity.setText(userCreditCard.getValidity());

    }

    @Override
    public int getItemCount() {
        return userCreditCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardProvider, cardAlias, expirationDate, cardValidity;

        public ViewHolder(View itemView) {
            super(itemView);

            cardProvider = (TextView) itemView.findViewById(R.id.card_provider_text_view);
            cardAlias = (TextView) itemView.findViewById(R.id.card_alias_text_view);
            expirationDate = (TextView) itemView.findViewById(R.id.expiration_date_text_view);
            cardValidity = (TextView) itemView.findViewById(R.id.card_validity_text_view);
        }
    }
}
