package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterParticipants extends RecyclerView.Adapter<AdapterParticipants.HolderParticipantsAdd>{
    private final Context context;
    private final List<ModelUser> userList;
    private final String groupId;
    private final String myGroupRole;
    public AdapterParticipants(Context context, List<ModelUser> userList, String groupId, String myGroupRole) {
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
        holder.itemView.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).child("Participants").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String hisPrevRole = ""+snapshot.child("role").getValue();
                                String[] options;
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Choose Option");
                                if (myGroupRole.equals("creator")){
                                    if (hisPrevRole.equals("admin")){
                                        options = new String[]{"Remove Admin", "Remove User"};
                                        builder.setItems(options, (dialog, which) -> {
                                            if (which == 0) {
                                                removeAdmin(modelUser);

                                            } else {
                                                removeParticiapnts(modelUser);
                                            }
                                        }).show();
                                    }
                                    else if (hisPrevRole.equals("participant"))
                                    {
                                        options = new String[]{"Make Admin", "Remove User"};
                                        builder.setItems(options, (dialog, which) -> {
                                            if (which == 0) {
                                                makeAdmin(modelUser);

                                            } else {
                                                removeParticiapnts(modelUser);
                                            }
                                        }).show();

                                    }
                                }
                                else if (myGroupRole.equals("admin")){
                                    switch (hisPrevRole) {
                                        case "creator":
                                            StyleableToast st = new StyleableToast(context, "Creator of the group", Toast.LENGTH_LONG);
                                            st.setBackgroundColor(Color.parseColor("#001E55"));
                                            st.setTextColor(Color.WHITE);
                                            st.setIcon(R.drawable.ic_check_wt);
                                            st.setMaxAlpha();
                                            st.show();
                                            break;
                                        case "admin":
                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        removeAdmin(modelUser);

                                                    } else {
                                                        removeParticiapnts(modelUser);
                                                    }
                                                }
                                            }).show();
                                            break;
                                        case "participant":
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        makeAdmin(modelUser);

                                                    } else {
                                                        removeParticiapnts(modelUser);
                                                    }
                                                }
                                            }).show();
                                            break;
                                    }
                                }
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Add Participant")
                                        .setMessage("Add this user in this group?")
                                        .setPositiveButton("Add", (dialog, which) -> addParticiapnts(modelUser)).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });
        holder.username.setText(mUsername);
    }

    private void addParticiapnts(ModelUser modelUser) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", modelUser.getId());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", ""+timestamp);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(context, "User added", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                });
    }

    private void makeAdmin(ModelUser modelUser) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(context, "New admin", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                });

    }

    private void removeParticiapnts(ModelUser modelUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(context, "User removed from the group", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                });
    }

    private void removeAdmin(ModelUser modelUser) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "participant");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(context, "Admin removed", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(context, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                });
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
