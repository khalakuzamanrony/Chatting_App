package com.android.personalchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.All_User_Adapter;
import com.android.personalchat.Models.AllUserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AllUsers extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<AllUserModel> arrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private All_User_Adapter all_user_adapter;
    private FirebaseUser firebaseUser;
    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myId = firebaseUser.getUid();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.alluserRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null) {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        } else {
            getAllUser();
            databaseReference.child(firebaseUser.getUid()).child("online_status").setValue("online");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseUser != null) {
            databaseReference.child(firebaseUser.getUid()).child("online_status").setValue("offline");
        }
    }

    @NotNull
    private ValueEventListener getAllUser() {
        return databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    AllUserModel allUserModel = dataSnapshot1.getValue(AllUserModel.class);
                    if (!allUserModel.getId().equals(myId)) {
                        arrayList.add(allUserModel);
                    }
                }
                all_user_adapter = new All_User_Adapter(arrayList, getApplicationContext());
                recyclerView.setAdapter(all_user_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}