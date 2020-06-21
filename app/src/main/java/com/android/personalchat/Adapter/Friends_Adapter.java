package com.android.personalchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.ChatActivity;
import com.android.personalchat.Models.Friends_Model;
import com.android.personalchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friends_Adapter extends RecyclerView.Adapter<Friends_Adapter.ViewHolder> {
    private ArrayList<Friends_Model> arrayList;
    private DatabaseReference databaseReference;
    private Context context;
    private String n, i;

    public Friends_Adapter(ArrayList<Friends_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Friends_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Friends_Adapter.ViewHolder holder, int position) {
        final Friends_Model friends_model = arrayList.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(friends_model.getReceiver()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    n = dataSnapshot.child("name").getValue().toString();
                    i = dataSnapshot.child("thumb_image").getValue().toString();
                    String os = dataSnapshot.child("online_status").getValue().toString();

                    //----Name------//
                    holder.name.setText(n);

                    //----Thumb Image------//
                    Picasso.get().load(i).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_launcher_background).into(holder.circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(i).placeholder(R.drawable.ic_launcher_background).into(holder.circleImageView);
                        }
                    });

                    //----ONLINE STATUS------//
                    if (dataSnapshot.hasChild("online_status")) {
                        if (os.equals("online")) {
                            holder.online_status.setVisibility(View.VISIBLE);
                        } else {
                            holder.online_status.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //----On Click------//
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("hisid", friends_model.getReceiver())
                        .putExtra("hisname", n)
                        .putExtra("histhumb", i)

                );

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private TextView name;
        private ImageView online_status;
        private CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.friend_full_layout);
            name = itemView.findViewById(R.id.friend_name);
            circleImageView = itemView.findViewById(R.id.friend_image);
            online_status = itemView.findViewById(R.id.friend_online_status);
        }
    }
}
