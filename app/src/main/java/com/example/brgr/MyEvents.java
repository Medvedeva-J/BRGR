package com.example.brgr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brgr.Models.Event;
import com.example.brgr.Models.EventAdapter;
import com.example.brgr.Models.EventsList;
import com.example.brgr.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyEvents extends AppCompatActivity {

    public ArrayList<Event> eventsArray = new ArrayList<Event>();
    public ListView eventListElement;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        textView = findViewById(R.id.no_events);
        EventsList.update();
        checkEventsCount();

        DatabaseReference events = FirebaseDatabase.getInstance().getReference().child("Events");
        eventListElement = findViewById(R.id.events_container);
        events.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EventsList.update();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                EventsList.update();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                EventsList.update();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        EventAdapter adapter;
        adapter = new EventAdapter(this, R.layout.event_item, EventsList.getElements());
        eventListElement.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        EventAdapter adapter;
        adapter = new EventAdapter(this, R.layout.event_item, EventsList.getElements());
        eventListElement.setAdapter(adapter);
        checkEventsCount();
    }

    public static void checkEventsCount() {
        if (EventsList.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public void goToHomeScreen(View view) {
        finish();
    }

    public void goToEventCreation(View view) {
        Intent intent = new Intent(this, EventCreation.class);
        startActivity(intent);
    }
}