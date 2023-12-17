package com.example.brgr.Models;

import java.util.ArrayList;
import java.util.Date;

public class Event implements Comparable<Event>{
    private String name, comment, author;
    private ArrayList<String> participants = new ArrayList<>();
    private Date date;
    public boolean isCheked = false;

    public Event() {}

    public Event(String name, Date date, String author, String comment) {
        this.name = name;
        this.date = date;
        this.author = author;
        this.comment = comment;
        this.participants = new ArrayList<>();
    }

    public Event(String name, Date date, String author) {
        this.name = name;
        this.date = date;
        this.author = author;
        this.comment = null;
        this.participants = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }
    public void addParticipant(String userUid) {
        this.participants.add(userUid);
    }

    public void removeParticipant(String userUid) {
        this.participants.remove(userUid);
    }

    @Override
    public int compareTo(Event o) {
        if (this.isCheked && o.isCheked || !this.isCheked && !o.isCheked) {
            if (this.getDate().before(o.getDate()))
                return -1;
            else if (this.getDate().after(o.getDate()))
                return 1;
            return 0;
        } else if (this.isCheked && !o.isCheked){
            return -1;
        } else return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return (((Event) obj).getDate().equals(this.getDate()));
    }
}
