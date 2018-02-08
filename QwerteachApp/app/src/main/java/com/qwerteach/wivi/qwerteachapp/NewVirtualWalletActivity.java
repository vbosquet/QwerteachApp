package com.qwerteach.wivi.qwerteachapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.CheckInternetAsyncTask;
import com.qwerteach.wivi.qwerteachapp.fragments.CreateVirtualWalletFragment;

public class NewVirtualWalletActivity extends AppCompatActivity implements CheckInternetAsyncTask.ICheckInternet {

    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_virtual_wallet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        status = getIntent().getIntExtra("status", 0);

        CheckInternetAsyncTask asyncTask = new CheckInternetAsyncTask(this, this);
        asyncTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayCreateVirtualWalletFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        CreateVirtualWalletFragment fragment = new CreateVirtualWalletFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment, "CREATE_NEW_WALLET");
        transaction.commitNow();
    }

    @Override
    public void getResult(Boolean result) {
        if(result) {
            displayCreateVirtualWalletFragment();
        } else {
            Toast.makeText(this, R.string.socket_failure, Toast.LENGTH_SHORT).show();
        }

    }
}
