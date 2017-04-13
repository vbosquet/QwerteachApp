package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    boolean isLogin;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isLogin = preferences.getBoolean("isLogin", false);

        if (isLogin) {
            intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    public void didTouchTextView(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void didTouchEmailSignUpButton(View view) {
        Intent intent = new Intent(this, EmailSignUpActivity.class);
        startActivity(intent);

    }
}
