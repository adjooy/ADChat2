package com.example.adchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is already authenticated
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // User is already authenticated, redirect to home activity
                    Intent homeIntent = new Intent(MainActivity.this, home.class);
                    startActivity(homeIntent);
                } else {
                    // User is not authenticated, redirect to login activity
                    Intent loginIntent = new Intent(MainActivity.this, login.class);
                    startActivity(loginIntent);
                }

                finish();
            }
        }, 2000);
    }
}
