package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.qwerteach.wivi.qwerteachapp.fragments.BankAccountInfosTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.CreateVirtualWalletFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.PaymentHistoryTabFragment;
import com.qwerteach.wivi.qwerteachapp.fragments.UserInfosTabFragment;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.CardRegistrationData;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;
import com.qwerteach.wivi.qwerteachapp.models.UserWalletInfos;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VirtualWalletActivity extends AppCompatActivity {

    static final int NUM_ITEMS = 3;
    String[] actionBarTabs = {"Cartes bancaires", "Historique", "Coordonnées"};
    ViewPager viewPager;
    MyAdapter myAdapter;
    ActionBar actionBar;
    ArrayList<UserCreditCard> userCreditCards;
    ArrayList<Transaction> transactions;
    ArrayList<UserBankAccount> userBankAccounts;
    CardRegistrationData cardRegistrationData;
    Intent intent;
    String email, token, userId;
    boolean isTeacher;
    ProgressDialog progressDialog;
    UserWalletInfos userWalletInfos;
    double totalWallet;
    QwerteachService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");
        isTeacher = preferences.getBoolean("isTeacher", false);

        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);

        getTotalWallet();

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.virtual_wallet_menu, menu);

        if(isTeacher) {
            MenuItem menuItem = menu.findItem(R.id.unload_wallet_button);
            menuItem.setVisible(true);
        }

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
            case R.id.unload_wallet_button:
                intent = new Intent(this, UnloadWalletActivity.class);
                intent.putExtra("userBankAccounts", userBankAccounts);
                intent.putExtra("totalWallet", totalWallet);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getTotalWallet() {
        Call<JsonResponse> call = service.getTotalWallet(userId, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String message = response.body().getMessage();

                if (message != null && message.equals("no wallet")) {
                    displayCreateVirtualWalletFragment();

                } else {
                    totalWallet = response.body().getTotalWallet();
                    actionBar.setSubtitle("Solde de mon Portefeuille : " + totalWallet/100 + "€");
                    getAllWalletInfos();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void displayCreateVirtualWalletFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, CreateVirtualWalletFragment.newInstance(), "CREATE_NEW_WALLET");
        transaction.addToBackStack(null);
        transaction.commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Fragment someFragment = getSupportFragmentManager().findFragmentByTag("CREATE_NEW_WALLET");

                if (someFragment == null) {
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    public void getAllWalletInfos() {
        startProgressDialog();
        Call<JsonResponse> call = service.getAllWallletInfos(0, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                userWalletInfos = response.body().getUserWalletInfos();
                userBankAccounts = response.body().getBankAccounts();
                transactions = response.body().getTransactions();
                userCreditCards = response.body().getUserCreditCards();
                getPreRegistrationCardData();

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void getPreRegistrationCardData() {
        Call<JsonResponse> call = service.getPreRegistrationCardData(email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                progressDialog.dismiss();
                cardRegistrationData = response.body().getCardRegistrationData();
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                setViewPager();
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
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

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle cardsBundle = new Bundle();
                    cardsBundle.putSerializable("userCreditCards", userCreditCards);
                    cardsBundle.putSerializable("userBankAccounts", userBankAccounts);
                    BankAccountInfosTabFragment bankAccountInfosTabFragment = new BankAccountInfosTabFragment();
                    bankAccountInfosTabFragment.setArguments(cardsBundle);
                    return bankAccountInfosTabFragment;
                case 1:
                    Bundle transactionsBundle = new Bundle();
                    transactionsBundle.putSerializable("transactions", transactions);
                    PaymentHistoryTabFragment paymentHistoryTabFragment = new PaymentHistoryTabFragment();
                    paymentHistoryTabFragment.setArguments(transactionsBundle);
                    return paymentHistoryTabFragment;
                case 2:
                    Bundle userInfosBundle = new Bundle();
                    userInfosBundle.putSerializable("user", userWalletInfos);
                    UserInfosTabFragment userInfosTabFragment = new UserInfosTabFragment();
                    userInfosTabFragment.setArguments(userInfosBundle);
                    return userInfosTabFragment;
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
