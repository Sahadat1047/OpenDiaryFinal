package com.spacester.opendiaryp.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.adapter.AdapterPost;
import com.spacester.opendiaryp.menu.Menu;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.post.Post;
import com.spacester.opendiaryp.settings.EditProfile;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("NullableProblems")
public class ProfileFragment extends Fragment {
    private String userId;

    //xml
    ImageView imageView3,imageView4;
    CircleImageView circularImageView, circular;
    RelativeLayout followingly,followersly;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    //Theme
    RelativeLayout containers,edittext_bg;
    ConstraintLayout header,constraintLayout,post;
    ProgressBar pb;
    RelativeLayout bio_layout, web_layout,location_layout;
    TextView  bio, link, location,post_meme;
    TextView mUsername, mName,noFollowers,noFollowing,noPost;
    ImageView imageView12,bio_img,web_img,location_img;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //firebase
        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //view
        imageView12 = view.findViewById(R.id.imageView12);
        bio_img = view.findViewById(R.id.bio_img);
        web_img = view.findViewById(R.id.web_img);
        location_img = view.findViewById(R.id.location_img);
        post_meme = view.findViewById(R.id.post_meme);
        mUsername = view.findViewById(R.id.textView10);
        mName = view.findViewById(R.id.textView9);
        imageView3 = view.findViewById(R.id.imageView3);
        noFollowers = view.findViewById(R.id.noFollowers);
        noFollowing = view.findViewById(R.id.noFollowing);
        noPost = view.findViewById(R.id.noPost);
        imageView4 = view.findViewById(R.id.imageView4);
        followersly = view.findViewById(R.id.followersly);
        followingly = view.findViewById(R.id.followingly);
        circularImageView = view.findViewById(R.id.circularImageView);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        containers = view.findViewById(R.id.container);
        edittext_bg = view.findViewById(R.id.edittext_bg);
        circular = view.findViewById(R.id.circular);
        header = view.findViewById(R.id.header);
        //view
        bio = view.findViewById(R.id.bio);
        link = view.findViewById(R.id.link);
        location = view.findViewById(R.id.location);
        bio_layout = view.findViewById(R.id.bio_layout);
        web_layout = view.findViewById(R.id.web_layout);
        location_layout = view.findViewById(R.id.location_layout);
        post = view.findViewById(R.id.post);
        recyclerView = view.findViewById(R.id.postView);


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


        post.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Post.class);
            startActivity(intent);
        });

        imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Menu.class);
            startActivity(intent);
        });

        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });




        //display
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                mUsername.setText(username);
                String photo = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try {
                    Picasso.get().load(photo).into(circularImageView);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(circularImageView);
                }
                try {
                    Picasso.get().load(photo).into(circular);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(circular);
                }

                String dbBio = Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString();
                bio.setText(dbBio);
                String dbLink = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();
                link.setText(dbLink);
                String dbLocation = Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString();
                location.setText(dbLocation);


                String ed_text = bio.getText().toString().trim();
                if(ed_text.length() > 0)
                {
                    bio_layout.setVisibility(View.VISIBLE);

                }
                else
                {
                    bio_layout.setVisibility(View.GONE);
                }
                String ed_link = link.getText().toString().trim();

                if(ed_link.length() > 0)
                {
                    web_layout.setVisibility(View.VISIBLE);

                }
                else
                {
                    web_layout.setVisibility(View.GONE);
                }

                String ed_location = location.getText().toString().trim();

                if(ed_location.length() > 0)
                {
                    location_layout.setVisibility(View.VISIBLE);

                }
                else
                {
                    location_layout.setVisibility(View.GONE);
                }

                pb.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                post.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Alerter.create(Objects.requireNonNull(getActivity()))
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
        postList= new ArrayList<>();
        loadPost();

        followingly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyFollowing.class);
            intent.putExtra("title", "following");
            startActivity(intent);
        });

        followersly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyFollowing.class);
            intent.putExtra("title", "followers");
            startActivity(intent);
        });

        getFollowers();
        getFollowing();
        getPost();

        return view;
    }

    private void getPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    if (Objects.requireNonNull(post).getId().equals(userId)){
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

    private void getFollowing() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userId).child("Following");
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

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userId).child("Followers");
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


    private void loadPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("id").equalTo(userId).limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
