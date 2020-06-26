package com.android.personalchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.GetTime;
import com.android.personalchat.Models.BlogModel;
import com.android.personalchat.R;
import com.android.personalchat.UserDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BlogModel> arrayList;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    public static final int IMAGE = 0;
    public static final int NO_IMAGE = 1;

    public BlogAdapter(Context context, ArrayList<BlogModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            firebaseFirestore = FirebaseFirestore.getInstance();
            databaseReference.keepSynced(true);

        }
        if (viewType == NO_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_interface_noimage, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_interface, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final BlogModel blogModel = arrayList.get(position);
        //------POST-------//
        holder.postCaption.setText(blogModel.getText());

        //------TIME-------//
        String time = GetTime.getTimeAgo(Long.parseLong(blogModel.getTime()), context);
        holder.postDate.setText(time);

        //------POST IMAGE-------//
        if (!blogModel.getImage().equals("noimage")) {
            Picasso.get().load(blogModel.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_launcher_background).into(holder.postImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(blogModel.getImage()).placeholder(R.drawable.ic_launcher_background).into(holder.postImage);
                }
            });
        } else {
//            holder.postImage.setVisibility(View.GONE);
            //if you dont want to use viewType you just can use this logic...
        }


        //------USER IMAGE-------//
        databaseReference.child(blogModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        //------SHOW LIKES-------//
        firebaseFirestore.collection("Blogs/" + blogModel.Postid + "/Likes").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists())
                {
                    holder.likeButton.setImageResource(R.drawable.ic_like_fill);
                }else {
                    holder.likeButton.setImageResource(R.drawable.ic_like);
                }
            }
        });
        //------SHOW LIKE COUNT-------//
        firebaseFirestore.collection("Blogs/" + blogModel.Postid + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty())
                {
                    holder.like_counter.setText(queryDocumentSnapshots.size()+" Likes");
                }
                else {
                    holder.like_counter.setText(0+" Likes");
                }
            }
        });
        //------Like HANDLE-------//
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Blogs").document(blogModel.Postid).collection("Likes").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            firebaseFirestore.collection("Blogs/" + blogModel.Postid + "/Likes").document(firebaseUser.getUid()).delete();
                        } else {
                            HashMap<String, Object> likeMap = new HashMap<>();
                            likeMap.put("time", String.valueOf(System.currentTimeMillis()));
                            firebaseFirestore.collection("Blogs/" + blogModel.Postid + "/Likes").document(firebaseUser.getUid()).set(likeMap);
                        }
                    }
                });
            }
        });



        //------ON CLICK NAME-------//
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserDetailsActivity.class)
                        .putExtra("id", blogModel.getId())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage, onlineDot;
        private TextView userName, postDate, postCaption, like_counter;
        private ImageView postImage;
        private ImageButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.blog_user_image);
            onlineDot = itemView.findViewById(R.id.blog_user_online_status);
            userName = itemView.findViewById(R.id.blog_user_name);
            postDate = itemView.findViewById(R.id.blog_post_time);
            postCaption = itemView.findViewById(R.id.blog_post_caption);
            postImage = itemView.findViewById(R.id.blog_post_image);
            like_counter = itemView.findViewById(R.id.like_counter);
            likeButton = itemView.findViewById(R.id.like_button);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (firebaseUser != null) {
            if (arrayList.get(position).getImage().equals("noimage")) {
                return NO_IMAGE;
            } else {
                return IMAGE;
            }
        }
        return 0;
    }
}
