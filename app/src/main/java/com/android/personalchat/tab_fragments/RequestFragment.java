package com.android.personalchat.tab_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Adapter.Friends_Adapter;
import com.android.personalchat.Models.Friends_Model;
import com.android.personalchat.Models.RequestModel;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

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
        View view= inflater.inflate(R.layout.fragment_request, container, false);


        return view;
    }
}