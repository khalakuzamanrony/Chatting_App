package com.android.personalchat.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.FullViewImageActivity;
import com.android.personalchat.GetTime;
import com.android.personalchat.Models.Blog2Model;
import com.android.personalchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Blog2Adapter extends RecyclerView.Adapter<Blog2Adapter.ViewHolder> {
    private Context context;
    private ArrayList<Blog2Model> arrayList;
    private FirebaseUser firebaseUser;
    private String myid;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    public static final int IMAGE = 0;
    public static final int NO_IMAGE = 1;

    public Blog2Adapter(Context context, ArrayList<Blog2Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Blog2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            myid = firebaseUser.getUid();
            databaseReference.keepSynced(true);
        }
        if (viewType == NO_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_interface_noimage, parent, false);
            return new Blog2Adapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_interface, parent, false);
            return new Blog2Adapter.ViewHolder(view);
        }
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
        }

        //------ON CLICK-------//
        if (!blog2Model.getImage().equals("noimage")) {
            holder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, FullViewImageActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .putExtra("imagelink", blog2Model.getImage()));


                }
            });
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

        //------LIKE-------//
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //------COMMENT-------//
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //------SHARE-------//
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + blog2Model.getPost_id(), Toast.LENGTH_SHORT).show();
            }
        });

        //-----MORE MENU  --------.//
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                if (blog2Model.getId().equals(myid)) {
                    popupMenu.getMenu().add(Menu.NONE, 0, 0, "Edit");
                    popupMenu.getMenu().add(Menu.NONE, 1, 0, "Delete");
                } else {
                    popupMenu.getMenu().add(Menu.NONE, 2, 0, "Save ");
                    popupMenu.getMenu().add(Menu.NONE, 3, 0, "Send Friend Request");
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == 0) {

                        } else if (id == 1) {

                            DeletePost(blog2Model.getPost_id(), blog2Model.getImage());
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }

        });
    }

    private void DeletePost(final String post_id, String image) {
        if (image.equals("noimage")) {

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Delete");
            progressDialog.setMessage("Deleteing...");
            progressDialog.show();
            Query query = FirebaseDatabase.getInstance().getReference("Blogs").orderByChild("post_id").equalTo(post_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        dataSnapshot1.getRef().removeValue();

                    }

                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Delete");
            progressDialog.setMessage("Deleteing...");
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            //Deleting image
            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Query query = FirebaseDatabase.getInstance().getReference("Blogs").orderByChild("post_id").equalTo(post_id);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    dataSnapshot1.getRef().removeValue();

                                }

                                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage, onlineDot;
        private TextView userName, postDate, postCaption, like_counter, comment_counter, share_counter;
        private ImageView postImage;
        private ImageButton likeButton, commentButton, shareButton, more;

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
            more = itemView.findViewById(R.id.blog_more);

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
