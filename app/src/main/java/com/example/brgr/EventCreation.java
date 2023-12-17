package com.example.brgr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.brgr.Models.Event;
import com.example.brgr.Models.EventsList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EventCreation extends AppCompatActivity {
    EditText date, time, name, comment;
    FirebaseDatabase db;
    DatabaseReference events;
    Date eventDate;
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);


        db = FirebaseDatabase.getInstance();
        events = db.getReference("Events");

        root = findViewById(R.id.root_event_creation);

        date = findViewById(R.id.date_field);
        time = findViewById(R.id.time_field);
        name = findViewById(R.id.event_name_field);
        comment = findViewById(R.id.comment_field);

        Slot[] dateSlots = new UnderscoreDigitSlotsParser().parseSlots("__.__.____");
        FormatWatcher dateFW = new MaskFormatWatcher( // форматировать текст будет вот он
                MaskImpl.createTerminated(dateSlots)
        );
        dateFW.installOn(date);

        Slot[] timeSlots = new UnderscoreDigitSlotsParser().parseSlots("__:__");
        FormatWatcher timeFW = new MaskFormatWatcher( // форматировать текст будет вот он
                MaskImpl.createTerminated(timeSlots)
        );
        timeFW.installOn(time);
    }

    public void checkEventData(View view) throws ParseException {
        if (TextUtils.isEmpty(name.getText().toString())) {
            Snackbar.make(view, "Введите название события", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(date.getText().toString()) || !DateIsCorrect()) {
            Snackbar.make(view, "Введите корректную дату", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(time.getText().toString()) || !TimeIsCorrect()) {
            Snackbar.make(view, "Введите корректное время", Snackbar.LENGTH_SHORT).show();
        } else {
            eventDate = new Date(String.valueOf(new SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .parse(date.getText().toString() + " " + time.getText().toString())));
            if (eventDate.before(Calendar.getInstance().getTime())) {
                Snackbar.make(view, "Указанная дата уже прошла", Snackbar.LENGTH_SHORT).show();
            } else {
                events.child(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Snackbar.make(view, "Такое имя уже существует, выберите другое", Snackbar.LENGTH_SHORT).show();
                        } else {
                            RegisterEvent();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    public void RegisterEvent() {
        Event event = new Event(name.getText().toString(), eventDate, FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!TextUtils.isEmpty(comment.getText().toString())) {
            event.setComment(comment.getText().toString());
        }
        events.child(name.getText().toString()).setValue(event);
        EventsList.add(event.getName(), event);
        for (Event i: EventsList.getElements()) {
            System.out.println(i.getName());
        }
        goToEvents(root);
    }

    public boolean TimeIsCorrect() {
        int[] timeArray = Arrays.stream(time.getText().toString().split(":")).mapToInt(Integer::parseInt).toArray();
        return (timeArray.length == 2 & timeArray[0] >= 0 & timeArray[0] <= 23 & timeArray[1] >= 0 & timeArray[1] <= 59);
    }

    public boolean DateIsCorrect() {
        int[] dateArray = Arrays.stream(date.getText().toString().split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (dateArray.length < 3)
            return false;
        int d = dateArray[0];
        int m = dateArray[1];
        int y = dateArray[2];
        if (m < 1 || m > 12)
            return false;
        if (d < 1 || d > 31)
            return false;
        if (m == 2)
        {
            if (isLeap(y))
                return (d <= 29);
            else
                return (d <= 28);
        }
        if (m == 4 || m == 6 ||
                m == 9 || m == 11)
            return (d <= 30);
        return true;
    }

    public boolean isLeap(int year)
    {
        return (((year % 4 == 0) &&
                (year % 100 != 0)) ||
                (year % 400 == 0));
    }

    public void goToEvents(View view) {
        finish();
    }

}
