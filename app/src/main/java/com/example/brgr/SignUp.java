package com.example.brgr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brgr.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    EditText email, name, password;
    MaterialButton btnSignUp;
    int stroke;
    FrameLayout root;
    public String currentUserName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        root = findViewById(R.id.root_signUp);

        email = findViewById(R.id.emailField_signUp);
        password = findViewById(R.id.passwordField_signUp);
        name = findViewById(R.id.nameField_signUp);

        btnSignUp = findViewById(R.id.signUp_signUp);

        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        stroke = btnSignUp.getSolidColor();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String nameText = name.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailText)) {
                    Snackbar.make(root, "Введите email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordText)) {
                    Snackbar.make(root, "Введите пароль", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nameText)) {
                    Snackbar.make(root, "Введите имя", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //рега
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(emailText);
                                user.setName(nameText);
                                user.setPassword(passwordText);
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user);
                                FirebaseUser currentUser = auth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameText)
                                        .build();


                                currentUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated: " + currentUser.getDisplayName());
                                                    currentUser.reload();
                                                    finishAffinity();
                                                    startActivity(new Intent(SignUp.this, HomeScreen.class));
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void goToMainActivity(View view) {
        finish();
    }

    public void checkForm() {
        if (password.getText().toString().trim().length() != 0 &
                name.getText().toString().trim().length() != 0 &
                email.getText().toString().trim().length() != 0) {
            btnSignUp.setEnabled(true);
            btnSignUp.setBackgroundColor(Color.parseColor("#F6D111"));
            btnSignUp.setTextColor(Color.parseColor("#FF000000"));
            btnSignUp.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F6D111")));
        } else {
            btnSignUp.setEnabled(false);
            btnSignUp.setBackgroundColor(root.getSolidColor());
            btnSignUp.setTextColor(stroke);
            btnSignUp.setStrokeColor(ColorStateList.valueOf(stroke));
        }
    }
}