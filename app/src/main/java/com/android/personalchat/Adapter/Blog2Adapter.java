package com.android.personalchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.GetTime;
import com.android.personalchat.Models.Blog2Model;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Blog2Adapter extends RecyclerView.Adapter<Blog2Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Blog2Model> arrayList;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    public Blog2Adapter(Context context, ArrayList<Blog2Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Blog2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_interface, parent, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.keepSynced(true);
        }
        return new Blog2Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Blog2Adapter.ViewHolder holder, int position) {
        final Blog2Model blog2Model = arrayList.get(position);
        //------POST-------//
        holder.postCaption.setText(blog2Model.getText());

        //------TIME-------//
        String time = GetTime.getTimeAgo(Long.parseLong(blog2Model.getTime()), context);
        holder.postDate.setText(time);

        //------POST IMAGE-------//
        if (!blog2Model.getImage().equals("noimage")) {
            Picasso.get().load(blog2Model.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_launcher_background).into(holder.postImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(blog2Model.getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.postImage);
                }
            });
        } else {
            holder.postImage.setVisibility(View.GONE);
            //if you dont want to use viewType you just can use this logic...
        }


        //------USER IMAGE-------//
        databaseReference.child(blog2Model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String online = dataSnapshot.child("online_status").getValue().toString();
                    final String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                    //------USER NAME-------//
                    holder.userName.setText(name);

                    //----- IMAGE-------//
                    Picasso.get().load(thumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_launcher_background).into(holder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(thumb).placeholder(R.drawable.ic_launcher_background).into(holder.userImage);
                        }
                    });

                    //------ONLINE DOT-------//
                    if (online.equals("online")) {
                        holder.onlineDot.setVisibility(View.VISIBLE);
                    } else {
                        holder.onlineDot.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage, onlineDot;
        private TextView userName, postDate, postCaption, like_counter, comment_counter, share_counter;
        private ImageView postImage;
        private ImageButton likeButton, commentButton, shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.blog_user_image);
            onlineDot = itemView.findViewById(R.id.blog_user_online_status);
            userName = itemView.findViewById(R.id.blog_user_name);
            postDate = itemView.findViewById(R.id.blog_post_time);
            postCaption = itemView.findViewById(R.id.blog_post_caption);
            postImage = itemView.findViewById(R.id.blog_post_image);

            like_counter = itemView.findViewById(R.id.like_counter);
            comment_counter = itemView.findViewById(R.id.comment_counter);
            share_counter = itemView.findViewById(R.id.share_counter);

            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            shareButton = itemView.findViewById(R.id.share_button);

        }
    }
}
