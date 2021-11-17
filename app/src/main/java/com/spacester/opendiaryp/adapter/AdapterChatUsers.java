package com.spacester.opendiaryp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.shareChat.Chat;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterChatUsers extends RecyclerView.Adapter<AdapterChatUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    private String userId;

    public AdapterChatUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final String userName = userList.get(position).getName();
        String userUsernsme = userList.get(position).getUsername();
        holder.mName.setText(userName);
        holder.mUsername.setText(userUsernsme);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.avatar);
        }catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> imBLockedOrNot(hisUID));
        checkBlocked(hisUID, holder, position);
        holder.blockedIV.setVisibility(View.GONE);
        holder.blockedIV.setOnClickListener(v -> {
            if (userList.get(position).isBlocked()){
                unBlockUser(hisUID);
            }else {
                BlockUser(hisUID);
            }
        });
    }
    private void imBLockedOrNot (String hisUID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUID).child("BlockedUsers").orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                StyleableToast st = new StyleableToast(context, "You're blocked by this user", Toast.LENGTH_LONG);
                                st.setBackgroundColor(Color.parseColor("#001E55"));
                                st.setTextColor(Color.WHITE);
                                st.setIcon(R.drawable.ic_error);
                                st.setMaxAlpha();
                                st.show();
                                return;
                            }
                        }
                        Intent intent = new Intent(context, Chat.class);
                        intent.putExtra("hisUid", hisUID);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkBlocked(String hisUID, MyHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").orderByChild("id").equalTo(hisUID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                holder.blockedIV.setVisibility(View.VISIBLE);
                                userList.get(position).setBlocked(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void BlockUser(String hisUID) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id", hisUID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").child(hisUID).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(context, "Blocked", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_error);
                    st.setMaxAlpha();
                    st.show();
                });
    }

    private void unBlockUser(String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId).child("BlockedUsers").orderByChild("id").equalTo(hisUID).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            StyleableToast st = new StyleableToast(context, "Unblocked", Toast.LENGTH_LONG);
                                            st.setBackgroundColor(Color.parseColor("#001E55"));
                                            st.setTextColor(Color.WHITE);
                                            st.setIcon(R.drawable.ic_check_wt);
                                            st.setMaxAlpha();
                                            st.show();
                                        }).addOnFailureListener(e -> {
                                            StyleableToast st = new StyleableToast(context,  e.getMessage(), Toast.LENGTH_LONG);
                                            st.setBackgroundColor(Color.parseColor("#001E55"));
                                            st.setTextColor(Color.WHITE);
                                            st.setIcon(R.drawable.ic_error);
                                            st.setMaxAlpha();
                                            st.show();
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast st = new StyleableToast(context, error.getMessage(), Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_error);
                        st.setMaxAlpha();
                        st.show();
                    }
                });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView avatar;
        final ImageView blockedIV;
        final TextView mName;
        final TextView mUsername;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.circleImageView);
            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            blockedIV = itemView.findViewById(R.id.blockedIV);
        }

    }
}
