package com.example.brgr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.brgr.Models.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.UUID;

public class PhotoAdding extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private ImageView mImageView;
    private Bitmap imageBitmap;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_adding);
        mImageView = findViewById(R.id.photo_preview);
        openFileChooser();
        findViewById(R.id.upload_another_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        findViewById(R.id.upload_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPhoto();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("Gallery");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Gallery");
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null) {
            mImageUri = data.getData();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels - 60;
            int height = mImageView.getMaxHeight();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);

                if (isHorizontal()) {
                    Picasso.with(getBaseContext()).load(mImageUri).resize(width, 0).into(mImageView);
                } else if (isVertical()){
                    Picasso.with(getBaseContext()).load(mImageUri).resize(0, height).into(mImageView);
                }
            } catch (Exception e) {
                Toast.makeText(PhotoAdding.this, "Ошибка выбора файла", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private boolean isVertical() {
        return imageBitmap.getHeight() > imageBitmap.getWidth();
    }

    private boolean isHorizontal() {
        return imageBitmap.getHeight() <= imageBitmap.getWidth();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadPhoto() {
        if (mImageUri != null) {
            String id = System.currentTimeMillis() + "_image";
            StorageReference fileReference = mStorageRef.child(id);
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PhotoAdding.this, "Фото загружено!", Toast.LENGTH_SHORT).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload();
                                    upload.setImageUrl(uri.toString());
                                    upload.setAuthor(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    mDatabaseRef.push().setValue(upload);
                                }
                            });
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PhotoAdding.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(PhotoAdding.this, "Выберите фото для загрузки", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToGallery() {
        finish();
    }
}