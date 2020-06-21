package com.android.personalchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.personalchat.Models.AllUserModel;
import com.android.personalchat.R;
import com.android.personalchat.UserDetailsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class All_User_Adapter extends RecyclerView.Adapter<All_User_Adapter.ViewHolder> {
    private ArrayList<AllUserModel> arrayList;
    private Context context;

    public All_User_Adapter(ArrayList<AllUserModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public All_User_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final All_User_Adapter.ViewHolder holder, int position) {
        final AllUserModel allUserModel = arrayList.get(position);

        holder.textView.setText(allUserModel.getName());

        Picasso.get().load(allUserModel.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_launcher_background).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(allUserModel.getThumb_image()).placeholder(R.drawable.ic_launcher_background).into(holder.imageView);
            }
        });
        holder.full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserDetailsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("id", allUserModel.getId()));

            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView textView;
        private RelativeLayout full_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.demo_image);
            textView = itemView.findViewById(R.id.demo_name);
            full_layout = itemView.findViewById(R.id.full_layout);
        }
    }
}
