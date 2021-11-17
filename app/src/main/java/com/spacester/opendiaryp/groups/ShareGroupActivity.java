package com.spacester.opendiaryp.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterChatShareGroups;
import com.spacester.opendiaryp.model.ModelChatListGroups;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShareGroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    //Groups
    AdapterChatShareGroups adapterChatShareGroups;
    List<ModelChatListGroups> modelGroupsList;
    EditText editText;
    ImageView imageView3;
    private String userId;
    ProgressBar pg;
    private static String postId;

    public static String getPostId() {
        return postId;
    }

    SharedPref sharedPref;
    public ShareGroupActivity(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerView = findViewById(R.id.users);
        imageView3 = findViewById(R.id.imageView3);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);

        imageView3.setOnClickListener(v -> onBackPressed());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShareGroupActivity.this));
        editText = findViewById(R.id.password);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    pg.setVisibility(View.VISIBLE);
                    filter(s.toString());
                }else {
                    getMyGroups();
                }

            }
        });
        modelGroupsList = new ArrayList<>();
        getMyGroups();

    }

    private void filter(String query) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelGroups = ds.getValue(ModelChatListGroups.class);
                        if (Objects.requireNonNull(modelGroups).getgName().toLowerCase().contains(query.toLowerCase()) ||
                                modelGroups.getgUsername().toLowerCase().contains(query.toLowerCase())){
                            modelGroupsList.add(modelGroups);
                            pg.setVisibility(View.GONE);
                        }
                    }
                    adapterChatShareGroups = new AdapterChatShareGroups(ShareGroupActivity.this, modelGroupsList);
                    recyclerView.setAdapter(adapterChatShareGroups);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMyGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelGroups = ds.getValue(ModelChatListGroups.class);
                        modelGroupsList.add(modelGroups);
                    }
                    adapterChatShareGroups = new AdapterChatShareGroups(ShareGroupActivity.this, modelGroupsList);
                    recyclerView.setAdapter(adapterChatShareGroups);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
