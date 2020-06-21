package com.android.personalchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Models.MessageModel;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MessageModel> arrayList;
    private FirebaseAuth firebaseAuth;
    private String myid;

    public MessageAdapter(Context context, ArrayList<MessageModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel messageModel = arrayList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        myid = firebaseAuth.getCurrentUser().getUid();
        if (messageModel.getFrom().equals(myid)) {
            holder.myMsg.setText(messageModel.getMessage());
        } else {

        }
        holder.myMsg.setText(messageModel.getMessage());
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView myMsg, seenStatus, time;
        private CircleImageView profile_thumb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMsg = itemView.findViewById(R.id.c_message);
            seenStatus = itemView.findViewById(R.id.c_seen);
            time = itemView.findViewById(R.id.c_time);
            profile_thumb = itemView.findViewById(R.id.c_image);
        }
    }

}
