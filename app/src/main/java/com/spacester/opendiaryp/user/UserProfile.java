package com.spacester.opendiaryp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.adapter.AdapterPost;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.notifications.Data;
import com.spacester.opendiaryp.notifications.Sender;
import com.spacester.opendiaryp.notifications.Token;
import com.spacester.opendiaryp.shareChat.Chat;
import com.spacester.opendiaryp.SharedPref;
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
public class UserProfile extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    TextView mUsername, mName,noFollowers,noFollowing,noPost;
    CircleImageView circularImageView;
    TextView  bio, link, location;
    RelativeLayout bio_layout, web_layout,location_layout,followingly,followersly;
    ProgressBar pb;
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    Button message;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;
    private String userId;
    Button follow,following;
    ImageView imageView3;
     String hisUid;
    SharedPref sharedPref;
    @SuppressWarnings("unused")
    private RequestQueue requestQueue;
    private boolean notify = false;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        recyclerView = findViewById(R.id.postView);
        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //Intent
         hisUid = getIntent().getStringExtra("hisUid");
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = firebaseDatabase.getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        //view
        mUsername = findViewById(R.id.textView10);
        message = findViewById(R.id.message);
        imageView3 = findViewById(R.id.imageView3);
        mName = findViewById(R.id.textView9);
        followersly = findViewById(R.id.followersly);
        followingly = findViewById(R.id.followingly);
        noPost = findViewById(R.id.noPost);
        noFollowing = findViewById(R.id.noFollowing);
        circularImageView = findViewById(R.id.circularImageView);
        noFollowers = findViewById(R.id.noFollowers);
        pb = findViewById(R.id.pb);
        following = findViewById(R.id.following);
        pb.setVisibility(View.VISIBLE);
        constraintLayout = findViewById(R.id.constraintLayout);
        follow = findViewById(R.id.follow);
        //view
        bio = findViewById(R.id.bio);
        link = findViewById(R.id.link);
        location = findViewById(R.id.location);
        bio_layout = findViewById(R.id.bio_layout);
        web_layout = findViewById(R.id.web_layout);
        location_layout = findViewById(R.id.location_layout);
        NestedScrollView cv = findViewById(R.id.cv);

        message.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfile.this, Chat.class);
            intent.putExtra("hisUid", hisUid);
            startActivity(intent);

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    loadPost();
                }
            }
        });

        imageView3.setOnClickListener(v -> onBackPressed());

        Query query = mDatabase.orderByChild("id").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    String name = ""+ ds.child("name").getValue();
                    String username = ""+ ds.child("username").getValue();
                    String photo = ""+ ds.child("photo").getValue();

                    String bi = ""+ ds.child("bio").getValue();
                    String lik = ""+ ds.child("link").getValue();
                    String loc = ""+ ds.child("location").getValue();

                    mName.setText(name);
                    mUsername.setText(username);
                    bio.setText(bi);
                    link.setText(lik);
                    location.setText(loc);

                    followingly.setOnClickListener(v -> {
                        Intent intent = new Intent(UserProfile.this, FollowersList.class);
                        intent.putExtra("id", hisUid);
                        intent.putExtra("title", "following");
                        startActivity(intent);
                    });

                    followersly.setOnClickListener(v -> {
                        Intent intent = new Intent(UserProfile.this, FollowersList.class);
                        intent.putExtra("id", hisUid);
                        intent.putExtra("title", "followers");
                        startActivity(intent);
                    });

                    try {
                        Picasso.get().load(photo).into(circularImageView);
                    }
                    catch (Exception e ){
                        Picasso.get().load(R.drawable.avatar).into(circularImageView);
                    }

                    String ed_text = bio.getText().toString().trim();
                    if (ed_text.length() > 0) {
                        bio_layout.setVisibility(View.VISIBLE);

                    } else {
                        bio_layout.setVisibility(View.GONE);
                    }
                    String ed_link = link.getText().toString().trim();

                    if (ed_link.length() > 0) {
                        web_layout.setVisibility(View.VISIBLE);

                    } else {
                        web_layout.setVisibility(View.GONE);
                    }

                    String ed_location = location.getText().toString().trim();

                    if (ed_location.length() > 0) {
                        location_layout.setVisibility(View.VISIBLE);

                    } else {
                        location_layout.setVisibility(View.GONE);
                    }

                    pb.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Alerter.create(UserProfile.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText(databaseError.getMessage())
                        .show();
                pb.setVisibility(View.GONE);
            }
        });


        follow.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("Following").child(hisUid).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid)
                        .child("Followers").child(firebaseUser.getUid()).setValue(true);
                follow.setVisibility(View.GONE);
                following.setVisibility(View.VISIBLE);
            addToHisNotification(""+hisUid);
            notify = true;

            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify){
                        sendNotification(hisUid, Objects.requireNonNull(user).getName(), "Started following you");

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
        });
        following.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("Following").child(hisUid).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid)
                    .child("Followers").child(firebaseUser.getUid()).removeValue();
            following.setVisibility(View.GONE);
            follow.setVisibility(View.VISIBLE);
        });


        isFollowing();
        getFollowers();
        getFollowing();
        getPost();

        postList= new ArrayList<>();
        loadPost();
    }
    @Override
    protected void onStart() {
        super.onStart();
        isFollowing();
        if (hisUid.equals(userId)){
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.container,profileFragment).commit();
        }
    }
    private void addToHisNotification(String hisUid){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", "Started following you");
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

                });

    }
    private void isFollowing() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(hisUid).exists()){
                 follow.setVisibility(View.GONE);
                    following.setVisibility(View.VISIBLE);
                }else {
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(hisUid).limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterPost(UserProfile.this, postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @SuppressWarnings("SameParameterValue")
    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(userId, name + " : " + message, "New Message", hisId, R.drawable.logo);
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
                                headers.put("Authorization", "key=AAAADu5rTxA:APA91bEvZnB9PdnPsCSZGXOakuCmEoyrhraMXdOTrXbxsolCRdVwRqe_XLf8cFZnngoEtn0xDWqbVs1gv2KUFtJ02VBwatkKSpLY1cev-uj_jEWJcydOrIvYi-Ph4NBot_FG4fNt5G8f");
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

    private void  getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisUid).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noFollowers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void  getFollowing(){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(hisUid).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noFollowing.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    if (Objects.requireNonNull(post).getId().equals(hisUid)){
                        i++;
                    }
                }
                noPost.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

