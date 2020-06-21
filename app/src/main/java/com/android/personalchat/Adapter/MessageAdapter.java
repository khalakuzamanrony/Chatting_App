package com.android.personalchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.GetTime;
import com.android.personalchat.Models.MessageModel;
import com.android.personalchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MessageModel> arrayList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String myid;
    private String hisImage;
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private boolean x = false;

    public MessageAdapter(Context context, ArrayList<MessageModel> arrayList, String hisImage) {
        this.context = context;
        this.arrayList = arrayList;
        this.hisImage = hisImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_item, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left_item, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MessageModel messageModel = arrayList.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        myid = firebaseAuth.getCurrentUser().getUid();

        holder.myMsg.setText(messageModel.getMessage());

        if (!messageModel.getFrom().equals(myid)) {
            Picasso.get().load(hisImage).placeholder(R.drawable.ic_launcher_background).into(holder.profile_thumb);
        }
        final String t = GetTime.getTimeAgo(messageModel.getTime(), context);
        holder.time.setText(t);


        holder.all_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x == true) {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    x = false;
                } else {
                    holder.time.setVisibility(View.GONE);
                    holder.linearLayout.setVisibility(View.GONE);
                    x = true;
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView myMsg, seenStatus, time;
        private CircleImageView profile_thumb;
        private RelativeLayout all_rl;
        private LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myMsg = itemView.findViewById(R.id.c_message);
            seenStatus = itemView.findViewById(R.id.c_seen);
            time = itemView.findViewById(R.id.c_time);
            profile_thumb = itemView.findViewById(R.id.c_image);
            all_rl = itemView.findViewById(R.id.all_rl);
            linearLayout = itemView.findViewById(R.id.info);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (arrayList.get(position).getFrom().equals(firebaseUser.getUid())) {
                return MSG_RIGHT;
            } else {
                return MSG_LEFT;
            }
        }
        return 0;
    }
}
