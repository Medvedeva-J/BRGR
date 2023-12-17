package com.example.brgr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.brgr.Models.EventsList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp, btnSignIn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventsList.update();

        btnSignUp = findViewById(R.id.signUp_mainActivity);
        btnSignIn = findViewById(R.id.signIn_mainActivity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            finish();
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        }
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void goToEnter(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}