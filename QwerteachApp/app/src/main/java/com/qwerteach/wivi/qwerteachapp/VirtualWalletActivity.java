package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.CheckUserWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllWalletInfosAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetPreRegistrationCardDataAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetTotalWalletAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.BankAccountInfosTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.CreateVirtualWalletFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.PaymentHistoryTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.UserInfosTabFragment;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VirtualWalletActivity extends AppCompatActivity implements CheckUserWalletAsyncTask.ICheckUserWallet,
        GetAllWalletInfosAsyncTask.IGetAllWalletInfos,
        GetPreRegistrationCardDataAsyncTask.IGetPreRegistrationData,
        GetTotalWalletAsyncTask.IGetTotalWallet {

    String email, token, userId;
    static final int NUM_ITEMS = 3;

    String[] actionBarTabs = {"Informations bancaires", "Historique", "Coordonnées"};
    ViewPager viewPager;
    MyAdapter myAdapter;
    ActionBar actionBar;
    ArrayList<UserCreditCard> userCreditCards;
    CardRegistrationData cardRegistrationData;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userCreditCards = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        CheckUserWalletAsyncTask checkUserWalletAsyncTask = new CheckUserWalletAsyncTask(this);
        checkUserWalletAsyncTask.execute(email, token);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.virtual_wallet_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
            case R.id.reload_wallet_button:
                intent = new Intent(this, ReloadWalletActivity.class);
                intent.putExtra("easy_payment", userCreditCards);
                intent.putExtra("card_registration", cardRegistrationData);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void checkUserWallet(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");

            if (message.equals("no wallet")) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.fragment_container, CreateVirtualWalletFragment.newInstance(), "CREATE_NEW_WALLET");
                transaction.addToBackStack(null);
                transaction.commit();

                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment someFragment = getSupportFragmentManager().findFragmentByTag("CREATE_NEW_WALLET");

                        if (someFragment == null) {
                            Log.i("FRAGMENT", "removed");
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });

            } else {

                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                GetTotalWalletAsyncTask getTotalWalletAsyncTask = new GetTotalWalletAsyncTask(this);
                getTotalWalletAsyncTask.execute(email, token, userId);

                GetAllWalletInfosAsyncTask getAllWalletInfosAsyncTask = new GetAllWalletInfosAsyncTask(this);
                getAllWalletInfosAsyncTask.execute(email, token);

                GetPreRegistrationCardDataAsyncTask getPreRegistrationCardDataAsyncTask = new GetPreRegistrationCardDataAsyncTask(this);
                getPreRegistrationCardDataAsyncTask.execute(email, token);


                setViewPager();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setViewPager() {

        myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

        });

        viewPager.setOffscreenPageLimit(2);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        for (int i = 0; i < actionBarTabs.length; i++) {
            actionBar.addTab(actionBar.newTab().setText(actionBarTabs[i]).setTabListener(tabListener));
        }

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getPreRegistrationData(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject cardRegistrationJson = jsonObject.getJSONObject("card_registration");

            String accessKey = cardRegistrationJson.getString("access_key");
            String cardPreregistrationId = cardRegistrationJson.getString("id");
            String preRegistrationData = cardRegistrationJson.getString("preregistration_data");
            String cardRegistrationURL = cardRegistrationJson.getString("card_registration_url");
            cardRegistrationData = new CardRegistrationData(accessKey, preRegistrationData, cardRegistrationURL,cardPreregistrationId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayTotalWallet(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            int totalWallet = jsonObject.getInt("total_wallet");
            actionBar.setSubtitle("Solde : " + totalWallet/100 + "€");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return BankAccountInfosTabFragment.newInstance();
                case 1:
                    return PaymentHistoryTabFragment.newInstance();
                case 2:
                    return UserInfosTabFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}
