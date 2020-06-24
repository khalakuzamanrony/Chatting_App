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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_PasswordActivity extends AppCompatActivity {
    private EditText getEmail;
    private ImageButton reset_go;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset__password);
        firebaseAuth = FirebaseAuth.getInstance();
        getEmail = findViewById(R.id.reset_pass);
        reset_go = findViewById(R.id.reset_go);
        reset_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressDialog = new ProgressDialog(Reset_PasswordActivity.this);
                progressDialog.setTitle("Reset Password");
                progressDialog.setMessage("Resetting.....");
                progressDialog.setCanceledOnTouchOutside(false);
                String getText = getEmail.getText().toString().trim();
                if (TextUtils.isEmpty(getText)) {
                    Snackbar.make(v, "Please fill all Fields! ", Snackbar.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    firebaseAuth.sendPasswordResetEmail(getText).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Please Check your Mailbox, A reset Link Has been sent! ", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}