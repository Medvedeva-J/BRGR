package com.example.brgr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brgr.Models.ImageAdapter;
import com.example.brgr.Models.Upload;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Gallery extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    public Context context = this;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecyclerView = findViewById(R.id.gallery_container);
        LinearLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Gallery");
        mStorageRef = FirebaseStorage.getInstance().getReference("Gallery");


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = new Upload(postSnapshot.child("imageUrl").getValue().toString(),
                            postSnapshot.child("author").getValue().toString());
                    mUploads.add(upload);
                }
                mAdapter = new ImageAdapter(Gallery.this, reverse(mUploads));
                mRecyclerView.setAdapter(mAdapter);
                if (mUploads.size() == 0) {
                    findViewById(R.id.gallery_container).setVisibility(View.INVISIBLE);
                    findViewById(R.id.no_photos).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.gallery_container).setVisibility(View.VISIBLE);
                    findViewById(R.id.no_photos).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Gallery.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter = new ImageAdapter(Gallery.this, reverse(mUploads));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void goToHomeScreen(View view) {
        finish();
    }

    public void goToPhotoUploading(View view) {
        Intent intent = new Intent(this, PhotoAdding.class);
        startActivity(intent);
    }

    public List reverse(List array) {
        Collections.reverse(array);
        return array;
    }
}