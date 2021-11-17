package com.spacester.opendiaryp.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterGroups;
import com.spacester.opendiaryp.adapter.AdapterPost;
import com.spacester.opendiaryp.adapter.AdapterUsers;
import com.spacester.opendiaryp.model.ModelGroups;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.model.ModelUser;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
public class Search extends AppCompatActivity {

    EditText editText;
    RecyclerView users_rv,posts_rv,groups_rv;
    TextView users,post,groups;
    RelativeLayout userly,postly,groupsly;
    ProgressBar pg;
    SharedPref sharedPref;
    ImageView imageView3;
    //User
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    //Post
    AdapterPost adapterPost;
    List<ModelPost> postList;
    //Groups
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroupsList;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.password);
        users_rv = findViewById(R.id.users_rv);
        posts_rv = findViewById(R.id.posts_rv);
        groups_rv = findViewById(R.id.groups_rv);
        users = findViewById(R.id.users);
        post = findViewById(R.id.post);
        groups = findViewById(R.id.groups);
        imageView3 = findViewById(R.id.imageView3);
        userly = findViewById(R.id.userly);
        postly = findViewById(R.id.postly);
        groupsly = findViewById(R.id.groupsly);
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        Intent intent = getIntent();

        if (intent.hasExtra("hashTag")){
            String tag = getIntent().getStringExtra("hashTag");
            editText.setText("#"+tag);
        }


        imageView3.setOnClickListener(v -> onBackPressed());

        users.setTextColor(Color.parseColor("#0047ab"));

        groupsly.setOnClickListener(v -> {
            users.setTextColor(Color.parseColor("#161F3D"));
            groups.setTextColor(Color.parseColor("#0047ab"));
            post.setTextColor(Color.parseColor("#161F3D"));
            users_rv.setVisibility(View.GONE);
            posts_rv.setVisibility(View.GONE);
            groups_rv.setVisibility(View.VISIBLE);
        });

        postly.setOnClickListener(v -> {
            users.setTextColor(Color.parseColor("#161F3D"));
            groups.setTextColor(Color.parseColor("#161F3D"));
            post.setTextColor(Color.parseColor("#0047ab"));
            users_rv.setVisibility(View.GONE);
            posts_rv.setVisibility(View.VISIBLE);
            groups_rv.setVisibility(View.GONE);
        });
        userly.setOnClickListener(v -> {
            users.setTextColor(Color.parseColor("#0047ab"));
            post.setTextColor(Color.parseColor("#161F3D"));
            groups.setTextColor(Color.parseColor("#161F3D"));
            users_rv.setVisibility(View.VISIBLE);
            posts_rv.setVisibility(View.GONE);
            groups_rv.setVisibility(View.GONE);
        });


        posts_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getAllPost();
                }
            }
        });



        users_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getAllUsers();
                }
            }
        });



        groups_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getAllGroups();
                }
            }
        });


        //User
        users_rv.setLayoutManager(new LinearLayoutManager(Search.this));
        userList = new ArrayList<>();
        getAllUsers();
        //Post
        posts_rv.setLayoutManager(new LinearLayoutManager(Search.this));
        postList = new ArrayList<>();
        getAllPost();
        //Groups
        groups_rv.setLayoutManager(new LinearLayoutManager(Search.this));
        modelGroupsList = new ArrayList<>();
        getAllGroups();

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
                    filterUser(s.toString());
                    filterPost(s.toString());
                    filterGroups(s.toString());
                }else {
                    getAllUsers();
                    getAllPost();
                    getAllGroups();
                }

            }
        });


    }

    private void filterGroups(String query) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                    if (Objects.requireNonNull(modelGroups).getgName().toLowerCase().contains(query.toLowerCase()) || modelGroups.getgUsername().contains(query.toLowerCase()) ){
                        modelGroupsList.add(modelGroups);
                    }
                    adapterGroups = new AdapterGroups(Search.this, modelGroupsList);
                    groups_rv.setAdapter(adapterGroups);
                    adapterGroups.notifyDataSetChanged();
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                    modelGroupsList.add(modelGroups);
                    adapterGroups = new AdapterGroups(Search.this, modelGroupsList);
                    groups_rv.setAdapter(adapterGroups);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterPost(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase()) || modelPost.getType().contains(query.toLowerCase()) || modelPost.getName().contains(query.toLowerCase())){
                        postList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(Search.this, postList);
                    posts_rv.setAdapter(adapterPost);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void filterUser(String query) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                            pg.setVisibility(View.GONE);
                        }
                    }
                    adapterUsers = new AdapterUsers(Search.this, userList);
                    adapterUsers.notifyDataSetChanged();
                    users_rv.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getAllPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterPost(Search.this, postList);
                    posts_rv.setAdapter(adapterPost);
                    pg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query q = databaseReference.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        userList.add(modelUser);
                        pg.setVisibility(View.GONE);
                    }
                    adapterUsers = new AdapterUsers(Search.this, userList);
                    users_rv.setAdapter(adapterUsers);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}