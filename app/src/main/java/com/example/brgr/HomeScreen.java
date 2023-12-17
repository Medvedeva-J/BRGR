package com.example.brgr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.brgr.Models.EventsList;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        TextView headerName = findViewById(R.id.user_name);

        auth = FirebaseAuth.getInstance();
        headerName.setText(auth.getCurrentUser().getDisplayName());
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }


    public void goToEvents(View view) {
        Intent intent = new Intent(this, MyEvents.class);
        startActivity(intent);
    }

    public void goToGallery(View view) {
        Intent intent = new Intent(this, Gallery.class);
        startActivity(intent);
    }

}