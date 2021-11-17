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

import com.spacester.opendiaryp.groups.GroupProfile;
import com.spacester.opendiaryp.model.ModelGroups;
import com.spacester.opendiaryp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGroups extends RecyclerView.Adapter<AdapterGroups.MyHolder> {

    final Context context;
    final List<ModelGroups> modelGroups;

    public AdapterGroups(Context context, List<ModelGroups> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_display, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
         String GroupId = modelGroups.get(position).getGroupId();
        String GroupName = modelGroups.get(position).getgName();
         String GroupUsername = modelGroups.get(position).getgUsername();
        String GroupIcon = modelGroups.get(position).getgIcon();

        holder.mName.setText(GroupName);
        holder.mUsername.setText(GroupUsername);
        try {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.group).into(holder.avatar);
        }catch (Exception e){
            Picasso.get().load(R.drawable.group).into(holder.avatar);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupProfile.class);
            intent.putExtra("groupId", GroupId);
            context.startActivity(intent);
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
