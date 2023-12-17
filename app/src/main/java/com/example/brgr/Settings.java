package com.example.brgr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.brgr.Models.EventsList;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
    }

    public void LogOut(View view) {
        auth.signOut();
        finishAffinity();
        Intent intent = new Intent(this, MainActivity.class);
        EventsList.clear();
        startActivity(intent);
    }

    public void goToHome(View view) {
        finish();
    }
}