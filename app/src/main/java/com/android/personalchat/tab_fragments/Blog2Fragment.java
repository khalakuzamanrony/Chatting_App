package com.android.personalchat.tab_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.personalchat.Adapter.Blog2Adapter;
import com.android.personalchat.Models.Blog2Model;
import com.android.personalchat.Blog.Blog2Activity;
import com.android.personalchat.R;
import com.android.personalchat.StartActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Blog2Fragment extends Fragment {
    View view;
    private FirebaseUser firebaseUser;
    private DatabaseReference root;
    private FloatingActionButton fab;
    private String myid;
    private RecyclerView recyclerView;
    private ArrayList<Blog2Model> arrayList=new ArrayList<>();
    private Blog2Adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_blog2, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            myid = firebaseUser.getUid();
            root = FirebaseDatabase.getInstance().getReference();
        }
        init();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Blog2Activity.class));
            }
        });

        return view;
    }

    private void init() {
        fab = view.findViewById(R.id.blog2_fab);
        recyclerView = view.findViewById(R.id.blog2RV);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseUser!=null)
        {
            GetAllPost();
        }
        else {
            startActivity(new Intent(getContext(), StartActivity.class));
        }
    }

    private void GetAllPost() {
        Query query=root.child("Blogs").orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        Blog2Model blog2Model=dataSnapshot1.getValue(Blog2Model.class);
                        arrayList.add(blog2Model);
                    }
                    adapter=new Blog2Adapter(getContext(),arrayList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}