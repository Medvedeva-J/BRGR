package com.example.brgr.Models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class EventsList{
    private static Map<String, Event> list = new HashMap<>();

    public EventsList() {
    }

    public static int size() {
        return list.size();
    }

    public static ArrayList<Event> getElements() {
        ArrayList<Event> result = new ArrayList<>();
        for (Event event : list.values()) {
            result.add(event);
        }
        return result;
    }

    public static java.util.Set<String> getKeys() {
        return list.keySet();
    }

    public static Event getByKey(String key) {
        return list.get(key);
    }

    public static void clear() {
        list.clear();
    }

    public static void add(String key, Event value) {
        if (list.containsKey(key)) {
            changeValue(key, value);
        } else {
            list.put(key, value);
        }
        sort();
    }

    public static void changeValue(String key, Event value) {
        list.replace(key, value);
    }

    public static void removeByKey(String key) {
        list.remove(key);
    }

    public static void update() {
        DatabaseReference events = FirebaseDatabase.getInstance().getReference().child("Events");

        events.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    Event item = i.getValue(Event.class);
                    if (!list.containsKey(item.getName())) {
                        list.put(item.getName(), item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        sort();
    }

    public static void sort() {
        list = MapUtil.sortByValue(list);
    }

    public static class MapUtil {
        public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
            List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
            list.sort(Entry.comparingByValue());

            Map<K, V> result = new LinkedHashMap<>();
            for (Entry<K, V> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }

            return result;
        }
    }
}

