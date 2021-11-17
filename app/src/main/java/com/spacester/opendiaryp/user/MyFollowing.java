package com.spacester.opendiaryp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterUsers;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFollowing extends AppCompatActivity {

    String title;
    List<String> idList;
    RecyclerView recyclerView;
    private String userId;
    AdapterUsers adapterUsers;
    TextView textView11;
    ProgressBar pg;
    List<ModelUser> userList;
    ImageView imageView3;
    SharedPref sharedPref;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_following);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.users);
        textView11 = findViewById(R.id.textView11);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        idList = new ArrayList<>();

        imageView3.setOnClickListener(v -> onBackPressed());


        switch (title){
            case "following":
                getFollowing();
                textView11.setText("Following");
                break;
            case "followers":
                getFollowers();
                textView11.setText("Followers");
                break;
        }


    }
    private void getFollowers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(userId).child("Followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
                pg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowing() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(userId).child("Following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
                pg.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);
                    for (String id : idList) {
                        assert modelUser != null;
                        if (modelUser.getId().equals(id)){
                            userList.add(modelUser);
                        }
                        adapterUsers = new AdapterUsers(MyFollowing.this, userList);
                        recyclerView.setAdapter(adapterUsers);
                        pg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}