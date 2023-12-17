package com.example.brgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email, password;
    MaterialButton btnSignIn;
    FrameLayout root;
    int stroke;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        password = findViewById(R.id.passwordField_signIn);
        email = findViewById(R.id.emailField_signIn);
        btnSignIn = findViewById(R.id.signIn_signIn);
        root = findViewById(R.id.root_signIn);

        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        stroke = btnSignIn.getCurrentTextColor();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Snackbar.make(root, "OK!", Snackbar.LENGTH_SHORT).show();
                                finishAffinity();
                                startActivity(new Intent(SignIn.this, HomeScreen.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    };

    public void goToMainActivity(View view) {
        finish();
    }

    public void checkForm() {
        if (password.getText().toString().trim().length() != 0 &
                email.getText().toString().trim().length() != 0) {
            btnSignIn.setEnabled(true);
            btnSignIn.setBackgroundColor(Color.parseColor("#F6D111"));
            btnSignIn.setTextColor(Color.parseColor("#FF000000"));
            btnSignIn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF000000")));
        } else {
            btnSignIn.setEnabled(false);
            btnSignIn.setBackgroundColor(root.getSolidColor());
            btnSignIn.setTextColor(stroke);
            btnSignIn.setStrokeColor(ColorStateList.valueOf(stroke));
        }
    }
};

