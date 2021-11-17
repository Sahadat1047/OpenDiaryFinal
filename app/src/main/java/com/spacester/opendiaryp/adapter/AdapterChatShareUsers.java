package com.spacester.opendiaryp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.notifications.Data;
import com.spacester.opendiaryp.notifications.Sender;
import com.spacester.opendiaryp.notifications.Token;
import com.spacester.opendiaryp.shareChat.Chat;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AdapterChatShareUsers extends RecyclerView.Adapter<AdapterChatShareUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    private String userId;
    private final RequestQueue requestQueue;
    private boolean notify = false;
    Uri video_uri, image_uri;
     String hisUID;
    String action;
    String type;
    Intent intent;
    public AdapterChatShareUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_display, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
         hisUID = userList.get(position).getId();
        String userImage = userList.get(position).getPhoto();
        final String userName = userList.get(position).getName();
        String userUsernsme = userList.get(position).getUsername();
        holder.mName.setText(userName);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        holder.mUsername.setText(userUsernsme);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.avatar);
        }catch (Exception ignored){

        }

         intent = ((Activity) context).getIntent();
         action = intent.getAction();
         type = intent.getType();


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
                        if (Intent.ACTION_SEND.equals(action) && type!=null){
                            if ("text/plain".equals(type)){
                                sendText(intent);
                            }
                            else if (type.startsWith("image")){
                                sendImage(intent);
                            }else if (type.startsWith("video")){
                                sendVideo(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkBlocked(String hisUID, AdapterChatShareUsers.MyHolder holder, int position) {
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

    private void sendVideo(Intent intent) {
        Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null){
            video_uri = videoUri;
            notify = true;
            String timeStamp = ""+System.currentTimeMillis();
            String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
            ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                if (uriTask.isSuccessful()){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", userId);
                    hashMap.put("receiver", hisUID);
                    hashMap.put("msg", downloadUri);
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "video");
                    databaseReference.child("Chats").push().setValue(hashMap);
                    Intent intent1 = new Intent(context, Chat.class);
                    intent1.putExtra("hisUid", hisUID);
                    context.startActivity(intent1);
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(hisUID, user.getName(), "Sent you a video");

                            }
                            notify = false;

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(hisUID)
                            .child(userId);

                    chatRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef2.child("id").setValue(userId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }).addOnFailureListener(e -> {

            });
        }
    }

    private void sendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null){
            image_uri = imageUri;

            notify = true;
            String timeStamp = ""+System.currentTimeMillis();
            String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
            ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                if (uriTask.isSuccessful()){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", userId);
                    hashMap.put("receiver", hisUID);
                    hashMap.put("msg", downloadUri);
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", timeStamp);
                    hashMap.put("type", "image");
                    databaseReference.child("Chats").push().setValue(hashMap);
                    Intent intent2 = new Intent(context, Chat.class);
                    intent2.putExtra("hisUid", hisUID);
                    context.startActivity(intent2);
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify){
                                sendNotification(hisUID, user.getName(), "Sent you a photo");

                            }
                            notify = false;

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                            .child(hisUID)
                            .child(userId);

                    chatRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                chatRef2.child("id").setValue(userId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }).addOnFailureListener(e -> {

            });
        }

    }

    private void sendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText!=null){
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", userId);
            hashMap.put("receiver", hisUID);
            hashMap.put("msg", sharedText);
            hashMap.put("isSeen", false);
            hashMap.put("type", "text");
            databaseReference1.child("Chats").push().setValue(hashMap);
            Intent intent3 = new Intent(context, Chat.class);
            intent3.putExtra("hisUid", hisUID);
            context.startActivity(intent3);
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if (notify){
                        sendNotification(hisUID, Objects.requireNonNull(user).getName(), sharedText);

                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void sendNotification(String hisUid, String name, String message) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userId, name+": "+message, "New Message", hisUid, R.drawable.logo);
                    Sender sender = new Sender(data, Objects.requireNonNull(token).getToken());
                    try {
                        JSONObject senderJsonOnj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonOnj,
                                response -> Log.d("JSON_RESPONSE", "onResponse:" +response.toString() ), error -> Log.d("JSON_RESPONSE", "onResponse:" +error.toString() )){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAADu5rTxA:APA91bEvZnB9PdnPsCSZGXOakuCmEoyrhraMXdOTrXbxsolCRdVwRqe_XLf8cFZnngoEtn0xDWqbVs1gv2KUFtJ02VBwatkKSpLY1cev-uj_jEWJcydOrIvYi-Ph4NBot_FG4fNt5G8f");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
