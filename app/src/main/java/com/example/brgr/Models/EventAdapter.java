package com.example.brgr.Models;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.codersroute.flexiblewidgets.FlexibleSwitch;
import com.example.brgr.MyEvents;
import com.example.brgr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends ArrayAdapter<Event> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Event> eventsList;
    public Event event;
    public DatabaseReference eventsDB;
    public Context context_public;
    EventAdapter adapter = this;

    public EventAdapter(Context context, int resource, ArrayList<Event> events) {
        super(context, resource, events);
        this.eventsList = events;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        context_public = context;
        eventsDB = FirebaseDatabase.getInstance().getReference("Events");
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Event event = eventsList.get(position);

        Date date = event.getDate();
        viewHolder.eventName.setText(event.getName());
        String s = new SimpleDateFormat("dd.MM.yyy  HH:mm").format(date);
        viewHolder.toggle.setChecked(event.getParticipants()
                .contains(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        viewHolder.eventDate.setText(s);

        viewHolder.itemElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventCard();
            }

            @SuppressLint("Range")
            private void showEventCard() {
                AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Popup).create();
                LayoutInflater inf = LayoutInflater.from(getContext());
                View eventCard = inf.inflate(R.layout.event_card, null);

                TextView cardName = eventCard.findViewById(R.id.event_card_name);
                TextView cardDate = eventCard.findViewById(R.id.event_card_date);
                TextView cardComment = eventCard.findViewById(R.id.event_card_comment);
                Button closeCard = eventCard.findViewById(R.id.event_card_close);
                Button more = eventCard.findViewById(R.id.event_card_more);

                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopUp(more, dialog);
                    }
                });

                closeCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                cardName.setText(event.getName());
                cardDate.setText(new SimpleDateFormat("dd.MM.yyy  HH:mm").format(date));
                cardComment.setText(event.getComment());

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setView(eventCard);
                dialog.show();
            }

            private void showPopUp(View anchor, DialogInterface currentDialog) {
                PopupMenu popup = new PopupMenu(getContext(), anchor);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                FirebaseDatabase.getInstance().getReference("Users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (String participant : event.getParticipants()) {
                                    popup.getMenu().findItem(R.id.show_participants).getSubMenu()
                                            .add(snapshot.child(participant).getValue(User.class).getName());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                popup.show();
                popup.getMenu().findItem(R.id.show_participants).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        if (event.getParticipants().size() == 0) {
                            Toast.makeText(getContext(),"Участников пока нет :(",
                                    Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });


                popup.getMenu().findItem(R.id.delete_event).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        eventsDB.child(event.getName()).removeValue();
                        EventsList.removeByKey(event.getName());
                        adapter.remove(event);
                        adapter.notifyDataSetChanged();
                        currentDialog.dismiss();
                        MyEvents.checkEventsCount();
                        return false;
                    }
                });

                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(event.getAuthor())) {
                    popup.getMenu().findItem(R.id.delete_event).setVisible(false);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        final FlexibleSwitch toggle;
        final TextView eventName, eventDate;
        final androidx.constraintlayout.widget.ConstraintLayout itemElement;

        ViewHolder(View view){
            eventName = view.findViewById(R.id.event_item_name);
            eventDate = view.findViewById(R.id.event_item_date);
            toggle = view.findViewById(R.id.toggle);
            itemElement = view.findViewById(R.id.item_element);
            toggle.addOnStatusChangedListener(new FlexibleSwitch.OnStatusChangedListener() {
                @Override
                public void onStatusChanged(boolean b) {
                    event = EventsList.getByKey(eventName.getText().toString());
                    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (EventsList.getKeys().contains(event.getName())) {
                        if (b) {
                            if (!event.getParticipants().contains(user)) {
                                event.addParticipant(user);
                                event.isCheked = true;
                            }
                        } else {
                            event.removeParticipant(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            event.isCheked = false;
                        }
                        EventsList.changeValue(event.getName(), event);
                        eventsDB.child(eventName.getText().toString()).setValue(event);
                    }
                }
            });
        }
    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}