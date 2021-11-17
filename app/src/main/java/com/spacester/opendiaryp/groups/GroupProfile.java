package com.spacester.opendiaryp.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterMembers;
import com.spacester.opendiaryp.groupSettings.EditGroup;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class GroupProfile extends AppCompatActivity {

    //Firebase
    FirebaseAuth mAuth;
    
    TextView mUsername, mName,noMemeber,creator;
    CircleImageView circularImageView;
    TextView  bio, link;
    RelativeLayout bio_layout, web_layout,relativeLayout18;
    ProgressBar pb;
    ConstraintLayout main;
    RecyclerView recyclerView;
    String GroupId, myGroupRole;
    String userId;
    ImageView imageView3,imageView4;
    SharedPref sharedPref;
    private ArrayList<ModelUser> userArrayList;
    private AdapterMembers adapterParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //view
        mUsername = findViewById(R.id.textView10);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        mName = findViewById(R.id.textView9);
        noMemeber = findViewById(R.id.noMemeber);
        imageView4 = findViewById(R.id.imageView4);
        circularImageView = findViewById(R.id.circularImageView);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.postView);
        relativeLayout18 = findViewById(R.id.relativeLayout18);
        main = findViewById(R.id.main);

        //view
        bio = findViewById(R.id.bio);
        link = findViewById(R.id.link);
        creator = findViewById(R.id.creator);
        bio_layout = findViewById(R.id.bio_layout);
        web_layout = findViewById(R.id.web_layout);

        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");
        mAuth = FirebaseAuth.getInstance();
        loadGroupInfo();
        loadMyInfo();
        getMembers();

        imageView4.setOnClickListener(v -> {
            Intent intent9 = new Intent(GroupProfile.this, EditGroup.class);
            intent9.putExtra("groupId", GroupId);
            startActivity(intent9);
        });
    }

    private void getMembers() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(GroupId).child("Participants");
        reference1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noMemeber.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            switch (myGroupRole) {
                                case "Participant":
                                    imageView4.setVisibility(View.INVISIBLE);

                                    break;
                                case "admin":
                                case "creator":
                                    imageView4.setVisibility(View.VISIBLE);

                                    break;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String groupId = ""+ds.child("groupId").getValue();
                    String gName = ""+ds.child("gName").getValue();
                    String gUsername = ""+ds.child("gUsername").getValue();
                    String gBio = ""+ds.child("gBio").getValue();
                    String gIcon = ""+ds.child("gIcon").getValue();
                    String gLink = ""+ds.child("gLink").getValue();
                    String createdBy = ""+ds.child("createdBy").getValue();
                    loadCreatorInfo(createdBy);

                    mName.setText(gName);
                    mUsername.setText(gUsername);
                    bio.setText(gBio);
                    link.setText(gLink);
                    try {
                        Picasso.get().load(gIcon).placeholder(R.drawable.group).into(circularImageView);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.group).into(circularImageView);
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

                    loadMembers();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadMembers() {
        userArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
               for (DataSnapshot ds: snapshot.getChildren()){
                   String id = ""+ds.child("id").getValue();
                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                   ref.orderByChild("id").equalTo(id).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for (DataSnapshot ds : snapshot.getChildren()) {
                               ModelUser modelUser = ds.getValue(ModelUser.class);
                               userArrayList.add(modelUser);
                           }
                           adapterParticipants = new AdapterMembers(GroupProfile.this, userArrayList, GroupId, myGroupRole);
                           recyclerView.setAdapter(adapterParticipants);
                           pb.setVisibility(View.GONE);
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCreatorInfo(String createdBy) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(createdBy).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    creator.setText("Created by "+name );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}