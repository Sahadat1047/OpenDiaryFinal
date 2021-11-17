package com.spacester.opendiaryp.shareChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.spacester.opendiaryp.adapter.AdapterChat;
import com.spacester.opendiaryp.MainActivity;
import com.spacester.opendiaryp.model.ModelChat;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.spacester.opendiaryp.notifications.Data;
import com.spacester.opendiaryp.notifications.Sender;
import com.spacester.opendiaryp.notifications.Token;
import com.spacester.opendiaryp.user.UserProfile;
import com.spacester.opendiaryp.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


@SuppressWarnings("ALL")
public class Chat extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_VIDEO_REQUEST = 1;
   RecyclerView recyclerView;
    EditText textBox;
    ImageView send,back,attach,more;
    TextView mName,username,blocked;
    CircleImageView circleImageView;
    RelativeLayout type;
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    ConstraintLayout block,info;
    BottomSheetDialog bottomDialog;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AdapterChat adapterChat;
    List<ModelChat> nChat;
    ConstraintLayout myblock,hisblock,constraintLayout5;
    SharedPref sharedPref;
    boolean isBlocked = false;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Uri image_uri, video_uri;
    ValueEventListener valueEventListener;

    String myUid;
    String hisUid;
    private RequestQueue requestQueue;
    private boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //ID
        send = findViewById(R.id.imageView10);
        textBox = findViewById(R.id.textBox);
        recyclerView = findViewById(R.id.chat);
        mName = findViewById(R.id.name);
        username = findViewById(R.id.username);
        attach = findViewById(R.id.imageView11);
        circleImageView = findViewById(R.id.circleImageView3);
        type = findViewById(R.id.relativeLayout15);
        back = findViewById(R.id.imageView9);
        constraintLayout5 = findViewById(R.id.constraintLayout5);
        myblock = findViewById(R.id.constraintLayout99);
        hisblock = findViewById(R.id.constraintLayout49);
        more = findViewById(R.id.more);
        createBottomSheetDialog();

        back.setOnClickListener(v -> {
            Intent intent = new Intent(Chat.this, MainActivity.class);
            startActivity(intent);

        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Intent
       hisUid = getIntent().getStringExtra("hisUid");

       more.setOnClickListener(v -> bottomDialog.show());

        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        myUid = currentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


        attach.setOnClickListener(v -> {
            bottomSheetDialog.show();
            //Check Permission

        });


        //UserDisplay
        Query query = databaseReference.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String name = ""+ ds.child("name").getValue();
                    String photo = ""+ ds.child("photo").getValue();
                    String status = ""+ ds.child("status").getValue();
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    if (typingStatus.equals(myUid)){
                      type.setVisibility(View.VISIBLE);
                    }else {

                        type.setVisibility(View.GONE);
                    }
                    if (status.equals("online")) {
                        username.setText(status);
                    }else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastTime = Long.parseLong(status);
                        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
                        username.setText("Active " + lastSeenTime);
                    }

                    mName.setText(name);
                    try {
                        Picasso.get().load(photo).placeholder(R.drawable.avatar).into(circleImageView);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.avatar).into(circleImageView);

                    }

                    mName.setOnClickListener(v -> {
                        Intent intent = new Intent(Chat.this, UserProfile.class);
                        intent.putExtra("hisUid", hisUid);
                        startActivity(intent);
                    });
                    circleImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(Chat.this, UserProfile.class);
                        intent.putExtra("hisUid", hisUid);
                        startActivity(intent);
                    });

                    readMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        //Send Msg
        send.setOnClickListener(v -> {
            notify = true;
            String message = textBox.getText().toString().trim();
            if (TextUtils.isEmpty(message)){

            }else {
                sendMessage(message);
            }
            textBox.setText("");
        });

        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        seenMessage();
        createBottomDialog();
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUid)
                .child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUid)
                .child(myUid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type!=null){
            if ("text/plain".equals(type)){
                sendText(intent);
            }
            else if (type.startsWith("image")){
                sendChatImage(intent);
            }else if (type.startsWith("video")){
                sendChatVideo(intent);
            }
        }
        checkBlocked();
        imBLockedOrNot();

    }

    private void imBLockedOrNot (){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("BlockedUsers").orderByChild("id").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                StyleableToast st = new StyleableToast(Chat.this, "You're blocked by this user", Toast.LENGTH_LONG);
                                st.setBackgroundColor(Color.parseColor("#001E55"));
                                st.setTextColor(Color.WHITE);
                                st.setIcon(R.drawable.ic_error);
                                st.setMaxAlpha();
                                st.show();
                                constraintLayout5.setVisibility(View.GONE);
                                hisblock.setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("id").equalTo(hisUid).
                addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                blocked.setText("Unblock");
                                myblock.setVisibility(View.VISIBLE);
                                constraintLayout5.setVisibility(View.GONE);
                              isBlocked = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void BlockUser() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id", hisUid);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").child(hisUid).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    StyleableToast st = new StyleableToast(Chat.this, "Blocked", Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_check_wt);
                    st.setMaxAlpha();
                    st.show();
                    constraintLayout5.setVisibility(View.GONE);
                    myblock.setVisibility(View.VISIBLE);
                }).addOnFailureListener(e -> {
                    StyleableToast st = new StyleableToast(Chat.this, e.getMessage(), Toast.LENGTH_LONG);
                    st.setBackgroundColor(Color.parseColor("#001E55"));
                    st.setTextColor(Color.WHITE);
                    st.setIcon(R.drawable.ic_error);
                    st.setMaxAlpha();
                    st.show();
                });
    }

    private void unBlockUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("id").equalTo(hisUid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            StyleableToast st = new StyleableToast(Chat.this, "Unblocked", Toast.LENGTH_LONG);
                                            st.setBackgroundColor(Color.parseColor("#001E55"));
                                            st.setTextColor(Color.WHITE);
                                            st.setIcon(R.drawable.ic_check_wt);
                                            st.setMaxAlpha();
                                            st.show();
                                            constraintLayout5.setVisibility(View.VISIBLE);
                                            myblock.setVisibility(View.GONE);
                                        }).addOnFailureListener(e -> {
                                            StyleableToast st = new StyleableToast(Chat.this,  e.getMessage(), Toast.LENGTH_LONG);
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
                        StyleableToast st = new StyleableToast(Chat.this, error.getMessage(), Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_error);
                        st.setMaxAlpha();
                        st.show();
                    }
                });
    }
    private void sendChatImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null){
            image_uri = imageUri;
           sendImage(image_uri);

        }
    }

    private void sendChatVideo(Intent intent) {
        Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null){
            video_uri = videoUri;
            sendVideo(video_uri);

        }
    }

    private void sendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText!=null){
            textBox.setText(sharedText);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(Chat.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission Allowed")
                        .show();
            } else {
                Alerter.create(Chat.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission is required")
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
            bottomSheetDialog.cancel();
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            sendVideo(video_uri);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendVideo(Uri video_uri) {
        Toast.makeText(Chat.this, "Sending....",
                Toast.LENGTH_LONG).show();
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
                hashMap.put("sender", myUid);
                hashMap.put("receiver", hisUid);
                hashMap.put("msg", downloadUri);
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("type", "video");
                databaseReference.child("Chats").push().setValue(hashMap);


                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisUid, user.getName(), "Sent a video");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast st = new StyleableToast(Objects.requireNonNull(Objects.requireNonNull(getApplicationContext())), error.getMessage(), Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_error);
                        st.setMaxAlpha();
                        st.show();
                    }
                });

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(hisUid)
                        .child(myUid);

                chatRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef2.child("id").setValue(myUid);
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

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

    private void sendImage(Uri image_uri) {
        Toast.makeText(Chat.this, "Sending....",
                Toast.LENGTH_LONG).show();
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
                hashMap.put("sender", myUid);
                hashMap.put("receiver", hisUid);
                hashMap.put("msg", downloadUri);
                hashMap.put("isSeen", false);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("type", "image");
                databaseReference.child("Chats").push().setValue(hashMap);

                DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser user = dataSnapshot.getValue(ModelUser.class);
                        if (notify){
                            sendNotification(hisUid, user.getName(), "Sent a Image");

                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast st = new StyleableToast(Objects.requireNonNull(Objects.requireNonNull(getApplicationContext())), error.getMessage(), Toast.LENGTH_LONG);
                        st.setBackgroundColor(Color.parseColor("#001E55"));
                        st.setTextColor(Color.WHITE);
                        st.setIcon(R.drawable.ic_error);
                        st.setMaxAlpha();
                        st.show();
                    }
                });

                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(hisUid)
                        .child(myUid);

                chatRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef2.child("id").setValue(myUid);
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

    private void seenMessage(){
        final String hisUid = getIntent().getStringExtra("hisUid");
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat modelChat = snapshot.getValue(ModelChat.class);
                    if (Objects.requireNonNull(modelChat).getReceiver().equals(myUid) && modelChat.getSender().equals(hisUid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void sendMessage(final String message) {
        final String hisUid = getIntent().getStringExtra("hisUid");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("msg", message);
        hashMap.put("isSeen", false);
        hashMap.put("type", "text");
        databaseReference1.child("Chats").push().setValue(hashMap);

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(hisUid, user.getName(), "Sent a message");

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                StyleableToast st = new StyleableToast(Objects.requireNonNull(Objects.requireNonNull(getApplicationContext())), databaseError.getMessage(), Toast.LENGTH_LONG);
                st.setBackgroundColor(Color.parseColor("#001E55"));
                st.setTextColor(Color.WHITE);
                st.setIcon(R.drawable.ic_error);
                st.setMaxAlpha();
                st.show();
            }
        });

    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name + " : " + message, "New Message", hisId, R.drawable.logo);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "AAAATP-4Amg:APA91bEzVSQK5oerZ_-N7ErVV4WhxFcmrLXeVvCJVuAuNVsm3rgNCwaLdfOVYsWvjL6xN8Jht58vLZ8cK0yTzTDhArfoQ-khvkP0vbhnpdsNYB2HpsuBHjIM3Z5kUg8MDdga7TV2JbrS");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void readMessage(){
        final String hisUid = getIntent().getStringExtra("hisUid");
        nChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (Objects.requireNonNull(chat).getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                    chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        nChat.add(chat);
                    }

                    adapterChat = new AdapterChat(Chat.this, nChat);
                    recyclerView.setAdapter(adapterChat);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOnlineStatus(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        databaseReference.updateChildren(hashMap);
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    private void createBottomDialog(){
        if ( bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.more_bottom_sheet, null);
            block = view.findViewById(R.id.chatshare);
            info = view.findViewById(R.id.appshare);
            blocked = view.findViewById(R.id.blocked);
            block.setOnClickListener(this);
            info.setOnClickListener(this);
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
       checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.constraintLayout3:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }

                break;
            case R.id.delete:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        chooseVideo();
                    }
                }
                else {
                    chooseVideo();
                }
                break;
            case R.id.chatshare:
                bottomDialog.cancel();
                if (isBlocked){
                    unBlockUser();
                }else {
                    BlockUser();

                }
                break;
            case R.id.appshare:
                bottomDialog.cancel();
                Intent intent = new Intent(Chat.this, UserProfile.class);
                intent.putExtra("hisUid", hisUid);
                startActivity(intent);
                break;
        }
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
}
