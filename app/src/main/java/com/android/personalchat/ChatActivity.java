package com.android.personalchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.personalchat.Adapter.MessageAdapter;
import com.android.personalchat.Models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView name, online_status;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView profile_image;
    private RecyclerView msgRV;
    private ImageButton add_item, msg_send;
    private EditText msg_text;
    private String hisid, hisname, his_thumb, online, myid,thumb;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference rootRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<MessageModel> arrayList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private int currentPage = 1;
    private String LastKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        hisid = getIntent().getStringExtra("hisid");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myid = firebaseUser.getUid();
        toolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        init();
        name.setText(hisname);
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("Chats").child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(hisid)) {
                    Map chatMap = new HashMap();
                    chatMap.put("seen", "false");
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatData = new HashMap();
                    chatData.put("Chats/" + myid + "/" + hisid, chatMap);
                    chatData.put("Chats/" + hisid + "/" + myid, chatMap);

                    rootRef.updateChildren(chatData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        msg_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
                msg_text.setText("");
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserDetailsActivity.class)
                .putExtra("id",hisid)
                );
            }
        });
        LoadMessage();
        //LoadMessage2();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadMoreMessage();
            }
        });
    }

    private void LoadMessage2() {
        rootRef.child("Messages").child(myid).child(hisid);
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                arrayList.clear();
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                arrayList.add(messageModel);

                messageAdapter = new MessageAdapter(getApplicationContext(), arrayList, thumb);

                msgRV.setAdapter(messageAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void LoadMoreMessage() {
        DatabaseReference messageref = rootRef.child("Messages").child(myid).child(hisid);
        Query messageQ = messageref.orderByKey().endAt(LastKey).limitToLast(10);
        messageQ.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                arrayList.add(messageModel);
                messageAdapter.notifyDataSetChanged();
                msgRV.scrollToPosition(arrayList.size() - 1);
                messageAdapter = new MessageAdapter(getApplicationContext(), arrayList, his_thumb);
                msgRV.setAdapter(messageAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadMessage() {
        arrayList.clear();
        DatabaseReference messageref = rootRef.child("Messages").child(myid).child(hisid);
        Query messageQ = messageref.limitToLast(currentPage * 10);
        messageQ.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.hasChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    arrayList.add(messageModel);
                    // messageAdapter.notifyDataSetChanged();
                    msgRV.scrollToPosition(arrayList.size() - 1);

                    messageAdapter = new MessageAdapter(getApplicationContext(), arrayList, thumb);

                    msgRV.setAdapter(messageAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage() {
        String message = msg_text.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            msg_send.setEnabled(false);
        } else {
            String myRef = "Messages/" + myid + "/" + hisid;
            String hisRef = "Messages/" + hisid + "/" + myid;

            DatabaseReference msgPush = rootRef.child("Messages").child(myid).child(hisid).push();
            String pushKey = msgPush.getKey();

            Map messagemap = new HashMap();
            messagemap.put("message", message);
            messagemap.put("seen", "false");
            messagemap.put("type", "text");
            messagemap.put("time", ServerValue.TIMESTAMP);
            messagemap.put("from", myid);

            Map messageUserMap = new HashMap();
            messageUserMap.put(myRef + "/" + pushKey, messagemap);
            messageUserMap.put(hisRef + "/" + pushKey, messagemap);

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {

                    }
                }
            });
        }

    }

    private void init() {
        name = findViewById(R.id.msg_profile_name);
        online_status = findViewById(R.id.msg_online_status);
        profile_image = findViewById(R.id.msg_profile_image);
        add_item = findViewById(R.id.msg_add_item);
        msg_send = findViewById(R.id.msg_send);
        msg_text = findViewById(R.id.msg_text);
        swipeRefreshLayout = findViewById(R.id.msg_swipe);

        msgRV = findViewById(R.id.msg_RV);
        msgRV.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        msgRV.setLayoutManager(linearLayoutManager);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(hisid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thumb = dataSnapshot.child("thumb_image").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ShowData();
    }

    private void ShowData() {
        if (firebaseUser != null) {
            rootRef.child("Users").child(hisid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hisname = dataSnapshot.child("name").getValue().toString();
                    his_thumb = dataSnapshot.child("thumb_image").getValue().toString();
                    //itemPos = dataSnapshot.child("profile_image").getValue().toString();
                    online = dataSnapshot.child("online_status").getValue().toString();
                    name.setText(hisname);
                    Picasso.get().load(his_thumb).placeholder(R.drawable.ic_launcher_background).into(profile_image);
                    if (online.equals("online")) {
                        online_status.setText("Online");
                    }
                    else {
                        online_status.setText("Offline");
                    }
                        /*else {

                        long time = Long.parseLong(online);
                        String lastSeen = GetTime.getTimeAgo(time, getApplicationContext());
                        online_status.setText(lastSeen);
                    }*/
                    

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

}