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
import com.qwerteach.wivi.qwerteachapp.models.Transaction;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserCreditCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VirtualWalletActivity extends AppCompatActivity implements CheckUserWalletAsyncTask.ICheckUserWallet,
        GetAllWalletInfosAsyncTask.IGetAllWalletInfos,
        GetPreRegistrationCardDataAsyncTask.IGetPreRegistrationData,
        GetTotalWalletAsyncTask.IGetTotalWallet {

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
    User user;
    double totalWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_wallet);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userCreditCards = new ArrayList<>();
        transactions = new ArrayList<>();
        userBankAccounts = new ArrayList<>();
        user = new User();
        progressDialog = new ProgressDialog(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");
        isTeacher = preferences.getBoolean("isTeacher", false);

        CheckUserWalletAsyncTask checkUserWalletAsyncTask = new CheckUserWalletAsyncTask(this);
        checkUserWalletAsyncTask.execute(email, token);

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
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });

            } else {

                GetTotalWalletAsyncTask getTotalWalletAsyncTask = new GetTotalWalletAsyncTask(this);
                getTotalWalletAsyncTask.execute(email, token, userId);

                GetAllWalletInfosAsyncTask getAllWalletInfosAsyncTask = new GetAllWalletInfosAsyncTask(this);
                getAllWalletInfosAsyncTask.execute(email, token, 0);
                startProgressDialog();

                GetPreRegistrationCardDataAsyncTask getPreRegistrationCardDataAsyncTask = new GetPreRegistrationCardDataAsyncTask(this);
                getPreRegistrationCardDataAsyncTask.execute(email, token);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getAllWalletInfos(String string) {

        Log.i("WALLET_INFOS", string);

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray cardsJsonArray = jsonObject.getJSONArray("cards");
            JSONArray transactionsJsonArray = jsonObject.getJSONArray("transactions");
            JSONArray bankAccountsJsonArray = jsonObject.getJSONArray("bank_accounts");
            JSONObject accountJson = jsonObject.getJSONObject("account");

            String firstName = accountJson.getString("first_name");
            String lastName = accountJson.getString("last_name");
            String address = accountJson.getString("address_line1");
            String streetNumber = accountJson.getString("address_line2");
            String postalCode = accountJson.getString("postal_code");
            String city = accountJson.getString("city");
            String region = accountJson.getString("region");
            String countryCode = accountJson.getString("country");
            String nationalityCode = accountJson.getString("nationality");
            String residencePlaceCode = accountJson.getString("country_of_residence");

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAddress(address);
            user.setStreetNumber(streetNumber);
            user.setPostalCode(postalCode);
            user.setCity(city);
            user.setRegion(region);
            user.setCountryCode(countryCode);
            user.setNationalityCode(nationalityCode);
            user.setResidencePlaceCode(residencePlaceCode);

            if (bankAccountsJsonArray.length() > 0) {
                for (int i = 0; i < bankAccountsJsonArray.length(); i++) {
                    JSONObject jsonData = bankAccountsJsonArray.getJSONObject(i);
                    String userId = jsonData.getString("user_id");
                    String ownerName = jsonData.getString("owner_name");
                    String type = jsonData.getString("type");
                    String bankAccountId = jsonData.getString("id");

                    UserBankAccount userBankAccount = new UserBankAccount(userId, ownerName, bankAccountId, type);
                    String accountNumber = "", bic = "";

                    if (type.equals("IBAN")) {
                        String iban = jsonData.getString("iban");
                        bic = jsonData.getString("bic");
                        userBankAccount.setIban(iban);
                        userBankAccount.setBic(bic);
                    } else if (type.equals("GB")) {
                        accountNumber = jsonData.getString("account_number");
                        String sortCode = jsonData.getString("sort_code");
                        userBankAccount.setAccountNumber(accountNumber);
                        userBankAccount.setSortCode(sortCode);
                    } else if (type.equals("US")) {
                        accountNumber = jsonData.getString("account_number");
                        String aba = jsonData.getString("aba");
                        String depositAccountType = jsonData.getString("deposit_account_type");
                        userBankAccount.setAccountNumber(accountNumber);
                        userBankAccount.setAba(aba);
                        userBankAccount.setDepositAccountType(depositAccountType);
                    } else if (type.equals("CA")) {
                        accountNumber = jsonData.getString("account_number");
                        String bankName = jsonData.getString("bank_name");
                        String institutionNumber = jsonData.getString("institution_number");
                        String branchCode = jsonData.getString("branch_code");
                        userBankAccount.setAccountNumber(accountNumber);
                        userBankAccount.setBankName(bankName);
                        userBankAccount.setInstitutionNumber(institutionNumber);
                        userBankAccount.setBranchCode(branchCode);
                    } else {
                        accountNumber = jsonData.getString("account_number");
                        String country = jsonData.getString("country");
                        bic = jsonData.getString("bic");
                        userBankAccount.setAccountNumber(accountNumber);
                        userBankAccount.setBic(bic);
                        userBankAccount.setCountry(country);
                    }

                    userBankAccounts.add(userBankAccount);
                }

            }

            for (int i = 0; i < cardsJsonArray.length(); i++) {
                JSONObject jsonData = cardsJsonArray.getJSONObject(i);
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

            for (int i = 0; i < transactionsJsonArray.length(); i++) {
                JSONObject jsonData = transactionsJsonArray.getJSONObject(i);
                JSONObject debitedFunds = jsonData.getJSONObject("debited_funds");
                int debitedAmount = debitedFunds.getInt("amount") / 100;
                String debitedCurrency = debitedFunds.getString("currency");
                JSONObject creditedFunds = jsonData.getJSONObject("credited_funds");
                int creditedAmount = creditedFunds.getInt("amount") / 100;
                String creditedCurrency = creditedFunds.getString("currency");
                JSONObject fees = jsonData.getJSONObject("fees");
                int feesAmount = fees.getInt("amount") / 100;
                String feesCurrency = fees.getString("currency");
                String transactionId = jsonData.getString("id");
                String authorId = jsonData.getString("author_id");
                String creditedUserId = jsonData.getString("credited_user_id");
                String type = jsonData.getString("type");
                String date = jsonData.getString("creation_date");
                String transactionDate = getDate(date);

                Transaction transaction = new Transaction(transactionId, transactionDate, type, authorId,
                        creditedUserId, creditedAmount + " " + creditedCurrency,
                        debitedAmount + " " + debitedCurrency, feesAmount + " " + feesCurrency);
                transactions.add(transaction);
            }

            progressDialog.dismiss();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            setViewPager();

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
            totalWallet = jsonObject.getDouble("total_wallet");
            actionBar.setSubtitle("Solde : " + totalWallet/100 + " €");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDate(String timeStamp) {
        long newTimeStamp = Long.parseLong(timeStamp) * 1000L;
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date netDate = (new Date(newTimeStamp));
        return sdf.format(netDate);
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
                    userInfosBundle.putSerializable("user", user);
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
