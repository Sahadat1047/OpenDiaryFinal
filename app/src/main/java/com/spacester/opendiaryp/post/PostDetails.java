package com.spacester.opendiaryp.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.adapter.AdapterComments;
import com.spacester.opendiaryp.groups.ShareGroupActivity;
import com.spacester.opendiaryp.model.ModelComments;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.search.Search;
import com.spacester.opendiaryp.shareChat.ShareActivity;
import com.spacester.opendiaryp.SharedPref;
import com.spacester.opendiaryp.user.MediaView;
import com.spacester.opendiaryp.user.UserProfile;
import com.spacester.opendiaryp.welcome.GetTimeAgo;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class PostDetails extends AppCompatActivity implements View.OnClickListener {
    SharedPref sharedPref;
    private static final int PICK_VIDEO_REQUEST = 1;
    ImageView back,more,meme,send,add,like_img,ic_eye,attach;
    RelativeLayout like,comment,share,view_ly,video_share;
    RecyclerView recyclerView;
    CircleImageView circleImageView3;
    TextView name,type,text,likeNo,commentNo,views;
    EditText textBox;
    ConstraintLayout constraintLayout9,viewlt;
    private String userId, myName, myDp;
    private DatabaseReference mDatabase;
    String hisId, hisName, postId, pLikes, hisDp, hisMeme, hisVine, hisTime, hisText, hisTypes;
    boolean mProcessComment = false;
    boolean mProcessCLike = false;
    List<ModelComments> commentsList;
    AdapterComments adapterComments;
    ProgressBar load;
    //Share
    ConstraintLayout chatshare,appshare,groupShare;
    BottomSheetDialog bottomDialog;
    //Share
    ConstraintLayout chatvid,appvid;
    BottomSheetDialog bottom;
    //Add
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    VideoView vine;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);



        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        textBox = findViewById(R.id.textBox);
        name = findViewById(R.id.name);
        ic_eye = findViewById(R.id.eye);
        attach = findViewById(R.id.imageView11);
        load= findViewById(R.id.load);
        views = findViewById(R.id.views);

        likeNo = findViewById(R.id.likeNo);
        like_img = findViewById(R.id.like_img);
        type = findViewById(R.id.username);
        view_ly = findViewById(R.id.view_ly);
        commentNo = findViewById(R.id.commentNo);


     vine = findViewById(R.id.videoView);

        text = findViewById(R.id.textView2);
        viewlt = findViewById(R.id.viewlt);
        constraintLayout9 = findViewById(R.id.constraintLayout9);
        circleImageView3 = findViewById(R.id.circleImageView3);
        recyclerView = findViewById(R.id.recyclerView);
        like = findViewById(R.id.relativeLayout);
        comment = findViewById(R.id.relativeLayout6);
        share = findViewById(R.id.meme_share);
        video_share = findViewById(R.id.vine_share);
        back = findViewById(R.id.imageView3);
        more = findViewById(R.id.more);
        meme = findViewById(R.id.imageView2);
        send = findViewById(R.id.imageView10);
        add = findViewById(R.id.imageView11);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setFocusable(false);


        HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.colorPrimary), hashTag -> {
            Intent intent1 = new Intent(PostDetails.this, Search.class);
            intent1.putExtra("hashTag", hashTag);
            startActivity(intent1);
        });

        mTextHashTagHelper.handle(text);

        attach.setOnClickListener(v -> bottomSheetDialog.show());

        share.setOnClickListener(v -> bottomDialog.show());

        more.setOnClickListener(v -> showMoreOptions());
        back.setOnClickListener(v -> onBackPressed());

        loadPostInfo();
        loadUserInfo();
        setLikes();
        setViews();
        loadComments();
        createBottomSheetDialog();
        createBottomDialog();
        BottomDialog();
        noLike();
        video_share.setOnClickListener(v -> bottom.show());
        
        send.setOnClickListener(v -> postComment());
        like.setOnClickListener(v -> likePost());

    }
    private void setViews() {
        DatabaseReference viewRef =FirebaseDatabase.getInstance().getReference().child("Views");
        viewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){

                    ic_eye.setImageResource(R.drawable.ic_eyed);

                }else {
                    ic_eye.setImageResource(R.drawable.ic_eye);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComments modelComments = ds.getValue(ModelComments.class);
                    commentsList.add(modelComments);
                    adapterComments = new AdapterComments(getApplicationContext(), commentsList);
                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    private void createBottomDialog(){
        if (bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.share_bottom_sheet, null);
            chatshare = view.findViewById(R.id.chatshare);
            appshare = view.findViewById(R.id.appshare);
            groupShare = view.findViewById(R.id.groupShare);
            chatshare.setOnClickListener(this);
            groupShare.setOnClickListener(this);
            appshare.setOnClickListener(this);
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);
        }
    }
    private void BottomDialog(){
        if (bottom == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.share_vid_bottom_sheet, null);
            chatvid = view.findViewById(R.id.chatvid);
            appvid = view.findViewById(R.id.appvid);
            chatvid.setOnClickListener(this);
            appvid.setOnClickListener(this);
            bottom = new BottomSheetDialog(this);
            bottom.setContentView(view);
        }
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this, more, Gravity.END);
        if (hisId.equals(userId)){
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE,2,0, "Liked By");
        if(!meme.equals("noImage") || !vine.equals("noVideo")){
            popupMenu.getMenu().add(Menu.NONE,3,0, "Fullscreen");
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id==0){
                beginDelete();
            }else if (id==1){
                Intent intent = new Intent(PostDetails.this, UpdatePost.class);
                intent.putExtra("key","editPost");
                intent.putExtra("editPostId", postId);
                startActivity(intent);
            } else if (id==2){
                Intent intent = new Intent(PostDetails.this, PostLikedBy.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
            else if (id==3){
                if(!hisVine.equals("noVideo")){
                    Intent intent = new Intent(getApplicationContext(), MediaView.class);
                    intent.putExtra("type","video");
                    intent.putExtra("uri",hisVine);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else
                if(!hisMeme.equals("noImage")){
                    Intent intent = new Intent(getApplicationContext(), MediaView.class);
                    intent.putExtra("type","image");
                    intent.putExtra("uri",hisMeme);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }

            return false;
        });
        popupMenu.show();
    }

    private void beginDelete() {
        if (vine.equals("noVideo") && meme.equals("noImage")){
            deleteWithoutBoth();
        }else if (vine.equals("noVideo")){
            deleteWithoutVine();
        }else if (meme.equals("noImage")){
            deleteWithoutMeme();
        }
    }

    private void deleteWithoutMeme() {
        StorageReference vidRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisVine);
        vidRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private void deleteWithoutVine() {


        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisMeme);
        picRef.delete().addOnSuccessListener(aVoid -> {

            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {

        });

    }

    private void deleteWithoutBoth() {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void noLike() {
        DatabaseReference likeRef =FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    likeNo.setText("Like");

                } else {
                   likeNo.setText(snapshot.getChildrenCount()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setLikes() {
        DatabaseReference likeRef =FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(userId)){

                like_img.setImageResource(R.drawable.ic_liked);

                }else {
             like_img.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {

        mProcessCLike = true;
        DatabaseReference likeRef =FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessCLike) {
                    if (dataSnapshot.child(postId).hasChild(userId)) {
                        likeRef.child(postId).child(userId).removeValue();
                        mProcessCLike = false;
                    } else {
                        likeRef.child(postId).child(userId).setValue("Liked");
                        mProcessCLike = false;
                        addToHisNotification(""+hisId,""+postId,"Liked your post");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void postComment() {
        String comment = textBox.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){

        }else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("comment", comment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("id", userId);
            hashMap.put("pLikes", "0");
            hashMap.put("type", "text");
            hashMap.put("pId", postId);
            hashMap.put("dp", myDp);
            hashMap.put("mane", myName);

            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        textBox.setText("");
                        updateCommentCount();
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

                });

    }

    private void updateCommentCount() {
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if (mProcessComment){
              String comments = ""+dataSnapshot.child("pComments").getValue();
             int newCommentCal = Integer.parseInt(comments)+1;
              ref.child("pComments").setValue(""+newCommentCal);
              mProcessComment = false;
              addToHisNotification(""+hisId,""+postId,"Commented on your post");
          }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadUserInfo() {
        Query query = FirebaseDatabase.getInstance().getReference("Users");
        query.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = ""+ds.child("name").getValue();
                    myDp = ""+ds.child("photo").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hisText = ""+ds.child("text").getValue();
                    hisMeme = ""+ds.child("meme").getValue();
                    hisVine = ""+ds.child("vine").getValue();
                    hisDp = ""+ds.child("dp").getValue();
                    hisTime = ""+ds.child("pTime").getValue();
                    hisTypes = ""+ds.child("type").getValue();
                    String hisViews = ""+ds.child("pViews").getValue();
                    hisName = ""+ds.child("name").getValue();
                    hisId = ""+ds.child("id").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String comment = ""+ds.child("pComments").getValue();

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(hisTime);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

                    views.setText(hisViews);
                    text.setText(hisText);
                    //DP
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.avatar).into(circleImageView3);
                    }catch (Exception ignored){

                    }

                    if (hisMeme.equals("noImage") &&  hisVine.equals("noVideo")){
                        load.setVisibility(View.GONE);
                    }

                    //hisTime
                    type.setText(hisTypes +  " - " +lastSeenTime );
                    name.setText(hisName);
                    if (comment.equals("0")){
                        commentNo.setText("Comment");

                    }else {
                        commentNo.setText(comment);
                    }

                    String ed_text = text.getText().toString().trim();
                    if(ed_text.length() > 0)
                    {
                        constraintLayout9.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        constraintLayout9.setVisibility(View.GONE);
                    }

                    name.setOnClickListener(v -> {
                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });
                    circleImageView3.setOnClickListener(v -> {
                        Intent intent = new Intent(PostDetails.this, UserProfile.class);
                        intent.putExtra("hisUid", hisId);
                        startActivity(intent);
                    });

                    //Post Image
                    if (hisMeme.equals("noImage")){
                        meme.setVisibility(View.GONE);
                    }else {
                        try {
                            Picasso.get().load(hisMeme).into(meme);
                        }catch (Exception ignored){

                        }
                    }


                    //Post Vine
                    if (hisVine.equals("noVideo")){
                        vine.setVisibility(View.GONE);
                        view_ly.setVisibility(View.GONE);
                  video_share.setVisibility(View.GONE);

                    }else {

                        Uri vineUri = Uri.parse(hisVine);
                     vine.setVideoURI(vineUri);

                     vine.start();

                        MediaController mediaController = new MediaController(PostDetails.this);
                        mediaController.setAnchorView(vine);
                        vine.setMediaController(mediaController);

                        vine.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setLooping(true);
                            }
                        });


                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void shareImageAndText(String text, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.spacester.myfriends.fileprovider", file);

        } catch (Exception e) {
            StyleableToast st = new StyleableToast(this, e.getMessage(), Toast.LENGTH_LONG);
            st.setBackgroundColor(Color.parseColor("#001E55"));
            st.setTextColor(Color.WHITE);
            st.setIcon(R.drawable.ic_check_wt);
            st.setMaxAlpha();
            st.show();
        }
        return uri;
    }

    private void shareTextOnly(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, text);
      startActivity(Intent.createChooser(intent, "Share Via"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.constraintLayout3:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }

                break;
            case R.id.delete:
                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        chooseVideo();
                    }
                }
                else {
                    chooseVideo();
                }
                break;

            case R.id.chatshare:

            case R.id.chatvid:
                Intent intent = new Intent(PostDetails.this, ShareActivity.class);
                intent.putExtra("postId", postId);
                startActivity(intent);
                break;
            case R.id.appshare:
                String shareText = text.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) meme.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(shareText);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(shareText, bitmap);
                }
                break;
            case R.id.appvid:

                String shareBody = hisText;
                String shareUrl = hisVine;
                Intent intent2 = new Intent(Intent.ACTION_SEND);
                intent2.setType("text/*");
                intent2.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                intent2.putExtra(Intent.EXTRA_TEXT,shareBody+" Link: "+shareUrl);
                startActivity(Intent.createChooser(intent2, "Share Via"));
                break;
            case R.id.groupShare:
                Intent intent4 = new Intent(PostDetails.this, ShareGroupActivity.class);
                intent4.putExtra("postId", postId);
                startActivity(intent4);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(PostDetails.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission Allowed")
                        .show();
            } else {
                Alerter.create(PostDetails.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission is required")
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
            bottomSheetDialog.cancel();
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            sendVideo(video_uri);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("id", userId);
                hashMap.put("pLikes", "0");
                hashMap.put("type", "image");
                hashMap.put("pId", postId);
                hashMap.put("dp", myDp);
                hashMap.put("mane", myName);

                ref1.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            textBox.setText("");
                            updateCommentCount();
                        }).addOnFailureListener(e -> {

                });


            }
        }).addOnFailureListener(e -> {

        });
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "ChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = uriTask.getResult().toString();
            if (uriTask.isSuccessful()){

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId", timeStamp);
                hashMap.put("comment", downloadUri);
                hashMap.put("timestamp", timeStamp);
                hashMap.put("id", userId);
                hashMap.put("pLikes", "0");
                hashMap.put("type", "video");
                hashMap.put("pId", postId);
                hashMap.put("dp", myDp);
                hashMap.put("mane", myName);

                ref1.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            textBox.setText("");
                            updateCommentCount();
                        }).addOnFailureListener(e -> {

                });


            }
        }).addOnFailureListener(e -> {

        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }


}