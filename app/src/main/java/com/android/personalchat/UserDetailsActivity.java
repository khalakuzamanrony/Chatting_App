package com.android.personalchat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    private ImageView profile_image;
    private TextView name, bio, total_f, mutual_f;
    private Button send, decline;
    private DatabaseReference databaseReference, req_dbRef, friends_dbRef, rootREF, databaseReference2;
    private FirebaseUser myid;
    private ProgressDialog progressDialog;
    private String hisid;
    private String current_req_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        hisid = getIntent().getStringExtra("id");
        transparentStatusBar();
        rootREF = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(hisid);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Users");
        req_dbRef = FirebaseDatabase.getInstance().getReference("Friend_Requests");
        friends_dbRef = FirebaseDatabase.getInstance().getReference("Friends");
        myid = FirebaseAuth.getInstance().getCurrentUser();
        init();
        putValue();
        current_req_status = "not_friend";

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setEnabled(false);
                //Toast.makeText(getApplicationContext(), "Request Send", Toast.LENGTH_SHORT).show();
                SendReq();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decline_Request();
                if (current_req_status.equals("req_send")) {
                    Map cancelRequestMap = new HashMap();
                    cancelRequestMap.put("Friend_Requests/" + myid.getUid() + "/" + hisid, null);
                    cancelRequestMap.put("Friend_Requests/" + hisid + "/" + myid.getUid(), null);
                    rootREF.updateChildren(cancelRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            send.setEnabled(true);
                            current_req_status = "not_friend";
                            send.setText("Send  Request");
                            decline.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Request Declined", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

    }

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    private void init() {
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        bio = findViewById(R.id.profile_bio);
        total_f = findViewById(R.id.profile_total_friends);
        mutual_f = findViewById(R.id.profile_mutual_friends);
        send = findViewById(R.id.send_req);
        decline = findViewById(R.id.accept_req);
        decline.setVisibility(View.INVISIBLE);

    }

    private void putValue() {
        progressDialog = new ProgressDialog(UserDetailsActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Registering....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String g_name = dataSnapshot.child("name").getValue().toString();
                String g_bio = dataSnapshot.child("status").getValue().toString();
                final String g_profile_image = dataSnapshot.child("profile_image").getValue().toString();

                name.setText(g_name);
                bio.setText(g_bio);
                if (!g_profile_image.equals("default")) {
                    Picasso.get().load(g_profile_image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_launcher_background).into(profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(g_profile_image).placeholder(R.drawable.ic_launcher_background).into(profile_image);
                        }
                    });

                }

                req_dbRef.child(myid.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(hisid)) {
                            String req_type = dataSnapshot.child(hisid).child("request_type").getValue().toString();
                            if (req_type.equals("sent")) {
                                current_req_status = "req_send";
                                send.setText("Cancel  Request");
                                decline.setVisibility(View.INVISIBLE);
                            } else if (req_type.equals("received")) {
                                current_req_status = "req_received";
                                send.setText("Accept  Request");
                                decline.setVisibility(View.VISIBLE);
                            }
                            progressDialog.dismiss();
                        } else {
                            friends_dbRef.child(myid.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(hisid)) {
                                        current_req_status = "friend";
                                        send.setText("UnFriend");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendReq() {
// ---- NOT FRIENDS--------
        if (current_req_status.equals("not_friend")) {
            Map requestMap = new HashMap();
            requestMap.put("Friend_Requests/" + myid.getUid() + "/" + hisid + "/" + "request_type", "sent");
            requestMap.put("Friend_Requests/" + hisid + "/" + myid.getUid() + "/" + "request_type", "received");
            rootREF.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    send.setEnabled(true);
                    current_req_status = "req_send";
                    send.setText("Cancel  Request");
                    Toast.makeText(getApplicationContext(), "Request Send", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // ----Cancel  REQUEST --------
        if (current_req_status.equals("req_send")) {
            Map cancelRequestMap = new HashMap();
            cancelRequestMap.put("Friend_Requests/" + myid.getUid() + "/" + hisid, null);
            cancelRequestMap.put("Friend_Requests/" + hisid + "/" + myid.getUid(), null);
            rootREF.updateChildren(cancelRequestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    send.setEnabled(true);
                    current_req_status = "not_friend";
                    send.setText("Send  Request");
                    decline.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Request Canceled", Toast.LENGTH_SHORT).show();
                }
            });


        }   // ---- ACCEPT REQUEST --------
        if (current_req_status.equals("req_received")) {
            final String date = DateFormat.getDateTimeInstance().format(new Date());
            Map acceptR = new HashMap();
            acceptR.put("Friends/" + myid.getUid() + "/" + hisid + "/" + "date", date);
            acceptR.put("Friends/" + myid.getUid() + "/" + hisid + "/" + "sender", myid.getUid());
            acceptR.put("Friends/" + myid.getUid() + "/" + hisid + "/" + "receiver", hisid);

            acceptR.put("Friends/" + hisid + "/" + myid.getUid() + "/" + "date", date);
            acceptR.put("Friends/" + hisid + "/" + myid.getUid() + "/" + "sender", hisid);
            acceptR.put("Friends/" + hisid + "/" + myid.getUid() + "/" + "receiver", myid.getUid());

            acceptR.put("Friend_Requests/" + myid.getUid() + "/" + hisid, null);
            acceptR.put("Friend_Requests/" + hisid + "/" + myid.getUid(), null);

            rootREF.updateChildren(acceptR, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        send.setEnabled(true);
                        current_req_status = "friend";
                        send.setText("UnFriend");
                        decline.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }  // ----UNFRIEND --------
        if (current_req_status.equals("friend")) {
            Map unFriend = new HashMap();
            unFriend.put("Friends/" + myid.getUid() + "/" + hisid, null);
            unFriend.put("Friends/" + hisid + "/" + myid.getUid(), null);
            rootREF.updateChildren(unFriend, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    send.setEnabled(true);
                    current_req_status = "not_friend";
                    send.setText("Send  Request");
                    decline.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Request Canceled", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference2.child(myid.getUid()).child("online_status").setValue("offline");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference2.child(myid.getUid()).child("online_status").setValue("online");
        }

    }
}