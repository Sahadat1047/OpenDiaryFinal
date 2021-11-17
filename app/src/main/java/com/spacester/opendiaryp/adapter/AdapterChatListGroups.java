package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.groups.GroupChat;
import com.spacester.opendiaryp.model.ModelChatListGroups;
import com.spacester.opendiaryp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChatListGroups extends RecyclerView.Adapter<AdapterChatListGroups.MyHolder> {

    final Context context;
    final List<ModelChatListGroups> modelGroups;

    public AdapterChatListGroups(Context context, List<ModelChatListGroups> modelGroups) {
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

        ModelChatListGroups modelChatListGroups = modelGroups.get(position);

        loadLastMsg(holder, modelChatListGroups );

        holder.mName.setText(GroupName);
        holder.mUsername.setText(GroupUsername);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupChat.class);
            intent.putExtra("groupId", GroupId);
            context.startActivity(intent);
        });

    }


    private void loadLastMsg(MyHolder holder, ModelChatListGroups modelChatListGroups) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(modelChatListGroups.getGroupId()).child("Message").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String message = ""+ds.child("msg").getValue();
                            String sender = ""+ds.child("sender").getValue();
                            String type = ""+ds.child("type").getValue();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.orderByChild("id").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+ds.child("name").getValue();
                                                switch (type) {
                                                    case "text":
                                                        holder.mUsername.setText(name + ": " + message);
                                                        break;
                                                    case "image":
                                                        holder.mUsername.setText(name + ": " + "Sent a photo");

                                                        break;
                                                    case "video":
                                                        holder.mUsername.setText(name + ": " + "Sent a Video");
                                                        break;
                                                    case "post":
                                                        holder.mUsername.setText(name + ": " + "Sent a post");
                                                        break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
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
