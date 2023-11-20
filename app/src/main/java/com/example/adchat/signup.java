package com.example.adchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup extends AppCompatActivity {

    EditText rg_name, rg_userName, rg_email, rg_password, rg_rePassword;
    Button rg_Button;
    CircleImageView rg_profile_photo;
    FirebaseAuth auth;
    Uri imageURI;
    FirebaseDatabase database;
    String imageURI2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        rg_Button = findViewById(R.id.loginButton);
        rg_name = findViewById(R.id.rgName);
        rg_userName = findViewById(R.id.rgUserName);
        rg_email = findViewById(R.id.rgEmail);
        rg_password = findViewById(R.id.rgPass);
        rg_rePassword = findViewById(R.id.rgPass2);
        rg_profile_photo = findViewById(R.id.rgProfilePic);

        rg_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), 10);
            }
        });

        rg_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name2 = rg_name.getText().toString();
                String userName = rg_userName.getText().toString().toLowerCase();
                String email2 = rg_email.getText().toString();
                String pass2 = rg_password.getText().toString();
                String rePass = rg_rePassword.getText().toString();
                String status = "Hi, Whatsapp";

                if (TextUtils.isEmpty(name2) || TextUtils.isEmpty(email2) || TextUtils.isEmpty(pass2) || TextUtils.isEmpty(rePass) || TextUtils.isEmpty(userName)) {
                    Toast.makeText(signup.this, "Please Provide Valid Information", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                    rg_email.setError("Enter a valid email address");
                } else if (pass2.length() < 7) {
                    rg_password.setError("Your password must be longer than 7 characters");
                } else if (!pass2.equals(rePass)) {
                    rg_rePassword.setError("Your Password does not match");
                    rg_password.setError("Your Password does not match");
                } else {
                    // Check if the email is already registered
                    DatabaseReference emailReference = database.getReference().child("User");
                    emailReference.orderByChild("email").equalTo(email2).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot emailSnapshot) {
                            if (emailSnapshot.exists()) {
                                // Email already exists
                                Toast.makeText(signup.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is unique, proceed with username check
                                DatabaseReference usernameReference = database.getReference().child("User");
                                usernameReference.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot usernameSnapshot) {
                                        if (usernameSnapshot.exists()) {
                                            // Username already exists
                                            Toast.makeText(signup.this, "Username is already registered", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Username is unique, proceed with user creation
                                            auth.createUserWithEmailAndPassword(email2, pass2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Get the unique ID generated by Firebase for the new user
                                                        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                                                        // Save the user data to the database
                                                        DatabaseReference userReference = database.getReference().child("User").child(userId);
                                                        Users user = new Users(userId, name2, email2, pass2, imageURI2, status);
                                                        userReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Intent intent = new Intent(signup.this, home.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(signup.this, "Error in creating account: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(signup.this, "Error in creating account: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(signup.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(signup.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                imageURI = data.getData();
                rg_profile_photo.setImageURI(imageURI);
                imageURI2 = imageURI.toString(); // Convert URI to String and store it
            }
        }
    }
}
