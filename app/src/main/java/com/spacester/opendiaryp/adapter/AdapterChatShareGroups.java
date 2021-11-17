package com.spacester.opendiaryp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spacester.opendiaryp.groups.GroupChat;
import com.spacester.opendiaryp.groups.ShareGroupActivity;
import com.spacester.opendiaryp.model.ModelChatListGroups;
import com.spacester.opendiaryp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterChatShareGroups extends RecyclerView.Adapter<AdapterChatShareGroups.MyHolder> {

    final Context context;
    final List<ModelChatListGroups> modelGroups;
    private String userId;

    public AdapterChatShareGroups(Context context, List<ModelChatListGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groupchatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
         String GroupId = modelGroups.get(position).getGroupId();
        String GroupName = modelGroups.get(position).getgName();
         String GroupUsername = modelGroups.get(position).getgUsername();
        String GroupIcon = modelGroups.get(position).getgIcon();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        holder.mName.setText(GroupName);
        holder.mUsername.setText(GroupUsername);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }
        holder.itemView.setOnClickListener(v -> {
            String timeStamp = ""+System.currentTimeMillis();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", userId);
            hashMap.put("msg", ShareGroupActivity.getPostId());
            hashMap.put("type", "post");
            hashMap.put("timestamp", timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(GroupId).child("Message").child(timeStamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(context, GroupChat.class);
                        intent.putExtra("groupId", GroupId);
                        context.startActivity(intent);
                    }).addOnFailureListener(e -> {

            });

        });

    }

    @Override
    public int getItemCount() {
        return modelGroups.size();
    }



    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);

        }
    }
}
