package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void didTouchTextView(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void didTouchEmailSignUpButton(View view) {
        Intent intent = new Intent(this, EmailSignUpActivity.class);
        startActivity(intent);

    }

    public void didTouchGoogleSignUpButton(View view) {
    }

    public void didTouchFacebookSignUpButton(View view) {
    }
}
