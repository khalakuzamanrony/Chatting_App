package com.android.personalchat.tab_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.BlogAdapter;
import com.android.personalchat.Blog.New_BlogActivity;
import com.android.personalchat.Models.BlogModel;
import com.android.personalchat.R;
import com.android.personalchat.StartActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BlogFragment extends Fragment {
    View view;
    private FirebaseUser firebaseUser;
    private DatabaseReference root;
    private FloatingActionButton fab;
    private String myid;
    private RecyclerView recyclerView;
    private ArrayList<BlogModel> arrayList = new ArrayList<>();
    private BlogAdapter adapter;
    private FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blog, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            myid = firebaseUser.getUid();
            root = FirebaseDatabase.getInstance().getReference();
            firebaseFirestore = FirebaseFirestore.getInstance();
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
        fab = view.findViewById(R.id.blog_fab);
        recyclerView = view.findViewById(R.id.blogRV);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            GetAllData();
        } else {
            startActivity(new Intent(getContext(), StartActivity.class));

        }

    }

    private void GetAllData() {
        Query sorting = firebaseFirestore.collection("Blogs").orderBy("time", Query.Direction.DESCENDING);
        sorting.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        BlogModel blogModel = documentChange.getDocument().toObject(BlogModel.class);
                        arrayList.add(blogModel);
                    }
                }
                adapter = new BlogAdapter(getContext(), arrayList);
                recyclerView.setAdapter(adapter);


            }
        });
    }
}