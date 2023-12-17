package com.example.brgr.Models;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class Upload {
    private String imageUrl;
    private String author;

    public Upload() {
    }

    public Upload(String url, String author) {
        imageUrl = url;
        author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        imageUrl = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String uid) {
        author = uid;
    }
}
