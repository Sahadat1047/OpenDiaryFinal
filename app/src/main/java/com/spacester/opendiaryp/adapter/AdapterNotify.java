package com.spacester.opendiaryp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.spacester.opendiaryp.model.ModelNotification;
import com.spacester.opendiaryp.post.PostDetails;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.user.UserProfile;
import com.spacester.opendiaryp.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AdapterNotify extends RecyclerView.Adapter<AdapterNotify.Holder>  {

    private final Context context;
    private final ArrayList<ModelNotification> notifications;
    private String userId;

    public AdapterNotify(Context context, ArrayList<ModelNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);

        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ModelNotification modelNotification = notifications.get(position);
        String mName = modelNotification.getsName();
        String notification = modelNotification.getNotification();
        String image = modelNotification.getsImage();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String postId = modelNotification.getpId();

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(timestamp);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("id").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String mName = ""+ds.child("name").getValue();
                            String image = ""+ds.child("photo").getValue();

                            modelNotification.setsName(mName);
                            modelNotification.setsImage(image);
                            holder.name.setText(mName);
                            try {
                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.circleImageView);
                            }catch (Exception e){
                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.circleImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.username.setText(notification +" - "+ lastSeenTime);
        holder.itemView.setOnClickListener(v -> {
            if (!postId.isEmpty()){
                Intent intent = new Intent(context, PostDetails.class);
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("hisUid", senderUid);
                context.startActivity(intent);
            }
        });

     holder.itemView.setOnLongClickListener(v -> {
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle("Delete");
         builder.setMessage("Are you sure to delete this notification?");
         builder.setPositiveButton("Delete", (dialog, which) -> {
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
             ref.child(userId).child("Notifications").child(timestamp).removeValue().addOnSuccessListener(aVoid -> {
                 StyleableToast st = new StyleableToast(context, "Deleted", Toast.LENGTH_LONG);
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
         }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
         builder.create().show();
         return false;
     });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        final CircleImageView circleImageView;
        final TextView username;
        final TextView name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
        }
    }
}
