package com.example.adchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText logMail, logPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance(); // Initialize auth object

        // Check if the user is already logged in
        if (auth.getCurrentUser() != null) {
            // User is already logged in, redirect to home activity
            startActivity(new Intent(login.this, home.class));
            finish(); // Finish the current activity so the user can't go back to login
        }

        // Initialize views
        Button createAccount = findViewById(R.id.createAccount);
        Button loginButton = findViewById(R.id.loginButton);
        logMail = findViewById(R.id.rgName);
        logPassword = findViewById(R.id.rgPass2);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailOrUsername = logMail.getText().toString();
                String pass = logPassword.getText().toString();

                if (TextUtils.isEmpty(mailOrUsername)) {
                    showToast("Enter the email or username");
                } else if (TextUtils.isEmpty(pass)) {
                    showToast("Enter your password");
                } else if (pass.length() < 6) {
                    logPassword.setError("Your password is too small");
                } else {
                    // Determine if the input is an email or username
                    AuthCredential credential;
                    if (Patterns.EMAIL_ADDRESS.matcher(mailOrUsername).matches()) {
                        // Input is an email
                        credential = EmailAuthProvider.getCredential(mailOrUsername, pass);
                    } else {
                        // Input is a username
                        String emailForUsername = getDummyEmailForUsername(mailOrUsername);
                        if (emailForUsername == null) {
                            showToast("Invalid username");
                            return;
                        }
                        credential = EmailAuthProvider.getCredential(emailForUsername, pass);
                    }

                    // Sign in with the provided credential
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Login successful, redirect to home activity
                                startActivity(new Intent(login.this, home.class));
                                finish();
                            } else {
                                handleAuthException(task.getException());
                            }
                        }
                    });
                }
            }
        });

        // Button intent for login page to signup page
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, signup.class));
                finish();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    // Dummy function to get email for username (customize this based on your logic)
    private String getDummyEmailForUsername(String username) {
        // Replace this with your logic to fetch email for the given username
        // For example, you might have a separate database or field for usernames
        // Here, we are using a dummy implementation
        if ("exampleUsername".equals(username)) {
            return "example@example.com";
        } else {
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleAuthException(Exception exception) {
        if (exception != null) {
            showToast("Authentication failed: " + Objects.requireNonNull(exception.getMessage()));
        }
    }
}
