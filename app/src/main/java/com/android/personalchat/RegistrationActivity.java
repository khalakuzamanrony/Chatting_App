package com.android.personalchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private EditText email, pass, username;
    private ImageButton go;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n_email = email.getText().toString().trim();
                String n_pass = pass.getText().toString().trim();
                String n_username = username.getText().toString().trim();
                if (TextUtils.isEmpty(n_email) && TextUtils.isEmpty(n_pass) && TextUtils.isEmpty(n_username)) {
                    Toast.makeText(getApplicationContext(), "Mustn't be Empty !", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Registering....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Register(n_email, n_pass, n_username);
                }
            }
        });
    }

    private void init() {
        email = findViewById(R.id.reg_email);
        pass = findViewById(R.id.reg_password);
        username = findViewById(R.id.reg_username);
        go = findViewById(R.id.reg_go);
    }

    private void Register(String n_email, String n_pass, final String n_username) {
        firebaseAuth.createUserWithEmailAndPassword(n_email, n_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    HashMap<String, String> userDetails = new HashMap<>();
                    userDetails.put("name", n_username);
                    userDetails.put("status", "Hi there,I am using Chat App");
                    userDetails.put("profile_image", "default");
                    userDetails.put("thumb_image", "default");
                    userDetails.put("id", userID);
                    userDetails.put("online_status", "offline");
                    databaseReference.child(userID).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}