package com.android.personalchat.tab_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.RequestAdapter;
import com.android.personalchat.Models.RequestModel;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestFragment extends Fragment {
    private RecyclerView request_RV;
    private TextView req_count;


    private ArrayList<RequestModel> arrayList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private String myid;
    private DatabaseReference databaseReference, root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        request_RV = view.findViewById(R.id.request_RV);
        req_count = view.findViewById(R.id.total_request_count);

        request_RV.setHasFixedSize(true);

        if (firebaseUser != null) {
            root = FirebaseDatabase.getInstance().getReference("Friend_Requests");
            root.keepSynced(true);
            myid = firebaseUser.getUid();
        }


        return view;
    }

    public void GetAllReq() {
        root.child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        RequestModel requestModel = dataSnapshot1.getValue(RequestModel.class);
                        if (requestModel.getRequest_type().equals("received")) {
                            arrayList.add(requestModel);
                        }


                    }
                    RequestAdapter requestAdapter = new RequestAdapter(arrayList, getContext());
                    request_RV.setAdapter(requestAdapter);
                    req_count.setText("Total Requests " + arrayList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GetAllReq();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllReq();
    }
}