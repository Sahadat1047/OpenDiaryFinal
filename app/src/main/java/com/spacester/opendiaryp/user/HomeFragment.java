package com.spacester.opendiaryp.user;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterPost;
import com.spacester.opendiaryp.adapter.AdapterStory;
import com.spacester.opendiaryp.authEmail.Finish;
import com.spacester.opendiaryp.authPhone.Final;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.model.ModelStory;
import com.spacester.opendiaryp.notifications.NotificationScreen;
import com.spacester.opendiaryp.post.Post;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.search.Search;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("NullableProblems")
public class HomeFragment extends Fragment {

    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<ModelPost>postList;
    AdapterPost adapterPost;
    ProgressBar pb;
    ImageView imageView4,imageView3;

    private AdapterStory story;
    private List<ModelStory> storyList;

    private String userId;
    List<String> followingList;
    List<String> followingSList;
    CircleImageView circular;
    ConstraintLayout post;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_home, container, false);

      recyclerView = view.findViewById(R.id.postView);

        RecyclerView storyView = view.findViewById(R.id.storyView);
        storyView.setHasFixedSize(true);
        circular= view.findViewById(R.id.circular);
        post= view.findViewById(R.id.post);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        storyView.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        story = new AdapterStory(getContext(), storyList);
        storyView.setAdapter(story);
        imageView3 = view.findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Search.class);
            startActivity(intent);
        });
      pb = view.findViewById(R.id.pb);
        mAuth = FirebaseAuth.getInstance();
        imageView4 = view.findViewById(R.id.imageView4);
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postList= new ArrayList<>();
        checkFollowing();
        checkSFollowing();

        post.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Post.class);
            startActivity(intent);
        });

        imageView4.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationScreen.class);
            startActivity(intent);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                mCurrenPage++;
                    checkFollowing();
                }
            }
        });

      DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
      mDatabase.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              String email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
              String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
              String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
              String photo = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();

              try {
                  Picasso.get().load(photo).placeholder(R.drawable.avatar).into(circular);
              }catch (Exception e){
                  Picasso.get().load(R.drawable.avatar).into(circular);
              }


              if (email.isEmpty()){
                  if (username.isEmpty() || name.isEmpty()){
                      Intent intent = new Intent(getActivity(), Final.class);
                      startActivity(intent);
                  }

              }else {
                  if (username.isEmpty()){
                      Intent intent = new Intent(getActivity(), Finish.class);
                      startActivity(intent);
                  }
              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
              Alerter.create(Objects.requireNonNull(getActivity()))
                      .setTitle("Error")
                      .setIcon(R.drawable.ic_error)
                      .setBackgroundColorRes(R.color.colorPrimary)
                      .setDuration(10000)
                      .enableSwipeToDismiss()
                      .setText(error.getMessage())
                      .show();

          }
      });

      return view;
    }



    private void checkSFollowing(){
        followingSList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingSList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingSList.add(snapshot.getKey());
                }
                readStory();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(userId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                loadPost();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    for (String id : followingList){
                        if (Objects.requireNonNull(modelPost).getId().equals(id)){
                            postList.add(modelPost);
                        }
                    }
                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new ModelStory("",0,0,"", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
                for (String id : followingSList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(modelStory);
                    }
                }
                story.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
