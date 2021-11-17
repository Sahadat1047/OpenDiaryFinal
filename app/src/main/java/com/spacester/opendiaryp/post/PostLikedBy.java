package com.spacester.opendiaryp.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

public class PostLikedBy extends AppCompatActivity {
    SharedPref sharedPref;
    String postId;
    private RecyclerView recyclerView;
    private List<ModelUser> userList;
    ProgressBar pg;
    private AdapterUsers adapterUsers;
    ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_by);
        Intent intent = getIntent();
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        imageView3.setOnClickListener(v -> onBackPressed());
        postId = intent.getStringExtra("postId");
        recyclerView = findViewById(R.id.users);
       userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        reference.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
userList.clear();
for (DataSnapshot ds: snapshot.getChildren()){
    String hisUid = ""+ ds.getRef().getKey();
    getUsers(hisUid);
}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers(String hisUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelUser modelUser = ds.getValue(ModelUser.class);
                            userList.add(modelUser);
                        }
                        adapterUsers = new AdapterUsers(PostLikedBy.this, userList);
                        recyclerView.setAdapter(adapterUsers);
                        pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}