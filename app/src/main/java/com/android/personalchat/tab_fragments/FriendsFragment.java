package com.android.personalchat.tab_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.Friends_Adapter;
import com.android.personalchat.Models.Friends_Model;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView friendsCount;
    private Friends_Adapter friends_adapter;
    private ArrayList<Friends_Model> arrayList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private String myid;
    private DatabaseReference databaseReference, root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.friends_RV);
        friendsCount = view.findViewById(R.id.total_friend_count);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        if (firebaseUser != null) {
            myid = firebaseUser.getUid();
            root = FirebaseDatabase.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference("Friends");
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        Friends_Model friends_model = dataSnapshot1.getValue(Friends_Model.class);
                        if (friends_model.getSender().equals(myid)) {
                            arrayList.add(friends_model);

                        }

                    }

                    friendsCount.setText("Total Friends " + arrayList.size());
                    friends_adapter = new Friends_Adapter(arrayList, getContext());
                    recyclerView.setAdapter(friends_adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}