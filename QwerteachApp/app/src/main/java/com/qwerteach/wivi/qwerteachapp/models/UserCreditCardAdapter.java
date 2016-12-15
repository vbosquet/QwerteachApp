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

public class UserCreditCardAdapter extends ArrayAdapter<UserCreditCard> {

    public UserCreditCardAdapter(Context context, ArrayList<UserCreditCard> userCreditCards) {
        super(context, 0, userCreditCards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserCreditCard userCreditCard = getItem(position);
        UserCreditCardAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new UserCreditCardAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_credit_card_list_view, parent, false);
            viewHolder.cardProvider = (TextView) convertView.findViewById(R.id.card_provider_text_view);
            viewHolder.cardAlias = (TextView) convertView.findViewById(R.id.card_alias_text_view);
            viewHolder.expirationDate = (TextView) convertView.findViewById(R.id.expiration_date_text_view);
            viewHolder.cardValidity = (TextView) convertView.findViewById(R.id.card_validity_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UserCreditCardAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.cardProvider.setText(userCreditCard.getCardProvider() + " (" + userCreditCard.getCurrency() + ")");
        viewHolder.cardAlias.setText(userCreditCard.getAlias());
        viewHolder.expirationDate.setText("Expiration : " + userCreditCard.getExpirationDate());
        viewHolder.cardValidity.setText(userCreditCard.getValidity());

        return convertView;
    }

    public static class ViewHolder {
        TextView cardProvider;
        TextView cardAlias;
        TextView expirationDate;
        TextView cardValidity;
    }
}
