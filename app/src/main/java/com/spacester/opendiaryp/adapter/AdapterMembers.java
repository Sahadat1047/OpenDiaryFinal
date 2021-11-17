package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.user.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterMembers extends RecyclerView.Adapter<AdapterMembers.HolderParticipantsAdd>{
    private final Context context;
    private final List<ModelUser> userList;
    private final String groupId;
    private final String myGroupRole;
    public AdapterMembers(Context context, List<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);

        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {
        ModelUser modelUser = userList.get(position);
        String mName = modelUser.getName();
        String mUsername = modelUser.getUsername();
        String dp = modelUser.getPhoto();
        String uid = modelUser.getId();

        holder.name.setText(mName);
        try {
            Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.circleImageView);

        }catch (Exception e){
            Picasso.get().load(R.drawable.avatar).into(holder.circleImageView);
        }
        checkifAlreadyExists(modelUser, holder,mUsername);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", uid);
                context.startActivity(intent);
            }
        });

        holder.username.setText(mUsername);
    }


    private void checkifAlreadyExists(ModelUser modelUser, HolderParticipantsAdd holder, String mUsername) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            holder.username.setText(mUsername + " - " +hisRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class HolderParticipantsAdd extends RecyclerView.ViewHolder{

        private final CircleImageView circleImageView;
        private final TextView name;
        private final TextView username;

        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);


        }
    }
}
