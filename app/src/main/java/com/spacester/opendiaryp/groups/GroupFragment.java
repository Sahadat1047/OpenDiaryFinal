package com.spacester.opendiaryp.groups;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterChatListGroups;
import com.spacester.opendiaryp.adapter.AdapterGroups;
import com.spacester.opendiaryp.Adpref;
import com.spacester.opendiaryp.model.ModelChatListGroups;
import com.spacester.opendiaryp.model.ModelGroups;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.search.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupFragment extends Fragment {

    ImageView add,imageView3;
    RecyclerView posts_rv,groups_rv;
    TextView post,groups;
    RelativeLayout postly,groupsly;
    ProgressBar pg;
    //Groups
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroupsList;
    //Groups
    AdapterChatListGroups adapterChatListGroups;
    List<ModelChatListGroups> modelChatListGroupsList;
    private String userId;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        add = view.findViewById(R.id.imageView4);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        imageView3 = view.findViewById(R.id.imageView3);
        add.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateGroup.class);
            startActivity(intent);
        });

        MobileAds.initialize(getContext(), initializationStatus -> {
        });
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(Objects.requireNonNull(getContext()));
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }

        posts_rv = view.findViewById(R.id.posts_rv);
        groups_rv = view.findViewById(R.id.groups_rv);
        post = view.findViewById(R.id.post);
        groups = view.findViewById(R.id.groups);
        postly = view.findViewById(R.id.postly);
        imageView3.setOnClickListener(v -> {
            Intent intent9 = new Intent(getActivity(), Search.class);
            startActivity(intent9);
        });
        groupsly = view.findViewById(R.id.groupsly);
        pg = view.findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);



        groupsly.setOnClickListener(v -> {
            groups.setTextColor(Color.parseColor("#0047ab"));
            post.setTextColor(Color.parseColor("#161F3D"));
            posts_rv.setVisibility(View.GONE);
            groups_rv.setVisibility(View.VISIBLE);
        });

        postly.setOnClickListener(v -> {
            groups.setTextColor(Color.parseColor("#161F3D"));
            post.setTextColor(Color.parseColor("#0047ab"));
            posts_rv.setVisibility(View.VISIBLE);
            groups_rv.setVisibility(View.GONE);
        });


        //Groups
        groups_rv.setHasFixedSize(true);
        groups_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelGroupsList = new ArrayList<>();
        groups_rv.smoothScrollToPosition(0);
        getMyGroups();
        //Chat
        posts_rv.setHasFixedSize(true);
        posts_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelChatListGroupsList = new ArrayList<>();
        posts_rv.smoothScrollToPosition(0);
        getChatGroups();

        return view;
    }

    private void getChatGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelChatListGroupsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(userId).exists()){
                        ModelChatListGroups modelChatListGroups = ds.getValue(ModelChatListGroups.class);
                        modelChatListGroupsList.add(modelChatListGroups);
                    }
                    adapterChatListGroups = new AdapterChatListGroups(getActivity(), modelChatListGroupsList);
                    posts_rv.setAdapter(adapterChatListGroups);
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
                     ModelGroups modelGroups = ds.getValue(ModelGroups.class);
                     modelGroupsList.add(modelGroups);
                 }
                    adapterGroups = new AdapterGroups(getActivity(), modelGroupsList);
                    groups_rv.setAdapter(adapterGroups);
                    pg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}