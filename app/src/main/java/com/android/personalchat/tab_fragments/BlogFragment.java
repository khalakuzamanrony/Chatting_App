package com.android.personalchat.tab_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.BlogAdapter;
import com.android.personalchat.Blog.New_BlogActivity;
import com.android.personalchat.Models.BlogModel;
import com.android.personalchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BlogFragment extends Fragment {
    View view;
    private FirebaseUser firebaseUser;
    private DatabaseReference root;
    private FloatingActionButton fab;
    private String myid;
    private RecyclerView recyclerView;
    private ArrayList<BlogModel> arrayList=new ArrayList<>();
    private BlogAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_blog, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            myid = firebaseUser.getUid();
            root = FirebaseDatabase.getInstance().getReference();
        }
        init();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), New_BlogActivity.class));
            }
        });
        return view;
    }

    private void init() {
        fab=view.findViewById(R.id.blog_fab);
        recyclerView=view.findViewById(R.id.blogRV);
    }
}