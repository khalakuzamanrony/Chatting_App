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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText email, pass;
    private ImageButton go;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(LoginActivity.this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        init();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p_email = email.getText().toString().trim();
                String p_pass = pass.getText().toString().trim();
                if (TextUtils.isEmpty(p_email) && TextUtils.isEmpty(p_pass)) {
                    Toast.makeText(getApplicationContext(), "Mustn't be Empty !", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setMessage("Loging....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Login(p_email, p_pass);
                }
            }
        });
    }

    private void Login(String p_email, String p_pass) {
        firebaseAuth.signInWithEmailAndPassword(p_email, p_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    private void init() {
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        go = findViewById(R.id.login_go);

    }
}