package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllWalletInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 8/12/16.
 */

public class BankAccountInfosTabFragment extends Fragment  implements GetAllWalletInfosAsyncTask.IGetAllWalletInfos{

    View view;
    String email, token;
    ArrayList<UserCreditCard> userCreditCards;
    ListView userCreditCardsListView;

    public static BankAccountInfosTabFragment newInstance() {
        BankAccountInfosTabFragment bankAccountInfosTabFragment = new BankAccountInfosTabFragment();
        return  bankAccountInfosTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        userCreditCards = new ArrayList<>();

        startGetAllWalletInfosAsyncTask();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_bank_account_infos_tab, container, false);
        userCreditCardsListView = (ListView) view.findViewById(R.id.user_credit_cards_list_view);
        return view;
    }

    public void startGetAllWalletInfosAsyncTask() {
        GetAllWalletInfosAsyncTask getAllWalletInfosAsyncTask = new GetAllWalletInfosAsyncTask(this);
        getAllWalletInfosAsyncTask.execute(email, token);

    }

    @Override
    public void getAllWalletInfos(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("cards");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String cardId = jsonData.getString("id");
                String alias = jsonData.getString("alias");
                String expirationDate = jsonData.getString("expiration_date");
                String cardProvider = jsonData.getString("card_provider");
                String validity = jsonData.getString("validity");
                String currency = jsonData.getString("currency");

                UserCreditCard userCreditCard = new UserCreditCard(alias, cardId);
                userCreditCard.setExpirationDate(expirationDate);
                userCreditCard.setCardProvider(cardProvider);
                userCreditCard.setValidity(validity);
                userCreditCard.setCurrency(currency);

                userCreditCards.add(userCreditCard);

            }

            setUserCreditCardsListView();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setUserCreditCardsListView() {
        UserCreditCardAdapter userCreditCardAdapter = new UserCreditCardAdapter(getActivity(), userCreditCards);
        userCreditCardsListView.setAdapter(userCreditCardAdapter);
    }
}
