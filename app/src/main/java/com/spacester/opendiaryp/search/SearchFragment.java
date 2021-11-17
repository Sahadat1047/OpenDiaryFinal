package com.spacester.opendiaryp.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterPost;
import com.spacester.opendiaryp.model.ModelPost;
import com.spacester.opendiaryp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
public class SearchFragment extends Fragment implements View.OnClickListener {

    ImageView search,imageView3;
    RecyclerView posts_rv, likes_rv, views_rv, comment_rv, meme_rv, vines_rv;
    ProgressBar pg;
    BottomSheetDialog bottomSheetDialog;
    ConstraintLayout chatshare,appshare,editgroup,leave,memes,vines;
    //Post
    AdapterPost adapterPost;
    List<ModelPost> postList;

    private static final int TOTAL_ITEMS_TO_LOAD = 7;
    private int mCurrenPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        search = view.findViewById(R.id.imageView4);
        imageView3= view.findViewById(R.id.imageView3);

        posts_rv = view.findViewById(R.id.posts_rv);
        likes_rv = view.findViewById(R.id.likes_rv);
        views_rv = view.findViewById(R.id.views_rv);
        comment_rv = view.findViewById(R.id.comment_rv);
        meme_rv = view.findViewById(R.id.meme_rv);
        vines_rv = view.findViewById(R.id.vines_rv);

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
        likes_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getLikePost();
                }
            }
        });
        views_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getViewsPost();
                }
            }
        });
        comment_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getCommentPost();
                }
            }
        });
        meme_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getMemePost();
                }
            }
        });
        vines_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    mCurrenPage++;
                    getVinesPost();
                }
            }
        });

        pg = view.findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Search.class);
            startActivity(intent);
        });
        imageView3.setOnClickListener(v -> bottomSheetDialog.show());


        //Post
        postList = new ArrayList<>();
        getAllPost();

        //Likes
        postList = new ArrayList<>();



        //Comment
        postList = new ArrayList<>();


        //views
        postList = new ArrayList<>();

        //Meme
        postList = new ArrayList<>();


        //Vine
        postList = new ArrayList<>();

        createBottomSheetDialog();
        return view;
    }
    private void createBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.more_filter_sheet, null);
            chatshare = view.findViewById(R.id.chatshare);
            appshare = view.findViewById(R.id.appshare);
            editgroup = view.findViewById(R.id.editgroup);
            leave = view.findViewById(R.id.leave);
            memes = view.findViewById(R.id.memes);
            vines = view.findViewById(R.id.vines);

            chatshare.setOnClickListener(this);
            appshare.setOnClickListener(this);
            editgroup.setOnClickListener(this);
            leave.setOnClickListener(this);
            memes.setOnClickListener(this);
            vines.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()));
            bottomSheetDialog.setContentView(view);
        }
    }

    private void getAllPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPost = new AdapterPost(getActivity(), postList);
                posts_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getLikePost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.orderByChild("pLikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);

                }
                adapterPost = new AdapterPost(getActivity(), postList);
                likes_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getViewsPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.orderByChild("pViews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPost = new AdapterPost(getActivity(), postList);
                views_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getCommentPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.orderByChild("pComments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPost = new AdapterPost(getActivity(), postList);
                comment_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getMemePost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.orderByChild("type").equalTo("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPost = new AdapterPost(getActivity(), postList);
                meme_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getVinesPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query q = ref.limitToLast(mCurrenPage * TOTAL_ITEMS_TO_LOAD);
        q.orderByChild("type").equalTo("Video").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPost = new AdapterPost(getActivity(), postList);
                vines_rv.setAdapter(adapterPost);
                pg.setVisibility(View.GONE);
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.chatshare:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.VISIBLE);
                likes_rv.setVisibility(View.GONE);
                comment_rv.setVisibility(View.GONE);
                views_rv.setVisibility(View.GONE);
                meme_rv.setVisibility(View.GONE);
                vines_rv.setVisibility(View.GONE);
                getAllPost();
                break;
            case R.id.appshare:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.GONE);
                likes_rv.setVisibility(View.VISIBLE);
                comment_rv.setVisibility(View.GONE);
                views_rv.setVisibility(View.GONE);
                meme_rv.setVisibility(View.GONE);
                vines_rv.setVisibility(View.GONE);
                getLikePost();
                break;
            case R.id.editgroup:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.GONE);
                likes_rv.setVisibility(View.GONE);
                comment_rv.setVisibility(View.VISIBLE);
                views_rv.setVisibility(View.GONE);
                meme_rv.setVisibility(View.GONE);
                vines_rv.setVisibility(View.GONE);
                getCommentPost();
                break;
            case R.id.leave:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.GONE);
                likes_rv.setVisibility(View.GONE);
                comment_rv.setVisibility(View.GONE);
                views_rv.setVisibility(View.VISIBLE);
                meme_rv.setVisibility(View.GONE);
                vines_rv.setVisibility(View.GONE);
                getViewsPost();

                break;
            case R.id.memes:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.GONE);
                likes_rv.setVisibility(View.GONE);
                comment_rv.setVisibility(View.GONE);
                views_rv.setVisibility(View.GONE);
                meme_rv.setVisibility(View.VISIBLE);
                vines_rv.setVisibility(View.GONE);
                getMemePost();
                break;
            case R.id.vines:
                bottomSheetDialog.cancel();
                posts_rv.setVisibility(View.GONE);
                likes_rv.setVisibility(View.GONE);
                comment_rv.setVisibility(View.GONE);
                views_rv.setVisibility(View.GONE);
                meme_rv.setVisibility(View.GONE);
                vines_rv.setVisibility(View.VISIBLE);
                getVinesPost();
                break;
        }

    }


}
