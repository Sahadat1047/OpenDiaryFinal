package com.spacester.opendiaryp.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class UpdatePost extends AppCompatActivity {
    SharedPref sharedPref;
    private static final int PICK_VIDEO_REQUEST = 1;

    ImageView meme,cancel;
    VideoView vines;
    ConstraintLayout add_meme,add_vines,update_remove,remove;
    Button update_it, update_vine;
    EditText text;
    TextView mName,type;
    CircleImageView circleImageView3;
    ProgressBar pd;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String name, dp, id;
    String editText, editMeme, editVine;


    String editPostId;

    private Uri image_uri, video_uri;
    MediaController mediaController;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        meme = findViewById(R.id.meme);
        add_meme = findViewById(R.id.constraintLayout3);
        update_remove = findViewById(R.id.update_remove);
        remove = findViewById(R.id.remove);
        text = findViewById(R.id.post_text);
        update_it = findViewById(R.id.post_it);
        update_vine = findViewById(R.id.post_vine);
        type = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        circleImageView3 = findViewById(R.id.circleImageView3);
        pd = findViewById(R.id.pb);
        cancel = findViewById(R.id.imageView);
        cancel.setOnClickListener(v -> onBackPressed());
        add_vines = findViewById(R.id.vines_lt);
        vines = findViewById(R.id.vines);
        mediaController = new MediaController(this);
        vines.setMediaController(mediaController);
        mediaController.setAnchorView(vines);
        MediaController ctrl = new MediaController(UpdatePost.this);
        ctrl.setVisibility(View.GONE);
        vines.setMediaController(ctrl);
        vines.start();
        vines.setOnPreparedListener(mp -> mp.setLooping(true));

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try {
                    Picasso.get().load(dp).into(circleImageView3);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(circleImageView3);
                }
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intent = getIntent();
        String isUpdateKey = ""+intent.getStringExtra("key");
        editPostId = ""+intent.getStringExtra("editPostId");

        if (isUpdateKey.equals("editPost")){
            loadPostData(editPostId);

        }

        remove.setOnClickListener(v -> {

            meme.setImageURI(null);
            image_uri = null;
            update_it.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            update_vine.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
            type.setText("Text");
        });
        update_vine.setOnClickListener(v -> {
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter caption")
                        .show();

            }else   if (!editVine.equals("noVideo")){
                updateWithVine(mText, String.valueOf(video_uri));
                pd.setVisibility(View.VISIBLE);
            }else if (vines.getDrawableState() != null){
                updateNowVine(mText, String.valueOf(video_uri));
                pd.setVisibility(View.VISIBLE);
            }

        });
        add_vines.setOnClickListener(v -> {
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

        });
        add_meme.setOnClickListener(v -> {

            type.setText("Image");
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
        });
        update_it.setOnClickListener(v -> {
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter caption")
                        .show();
                return;
            }
            if (!editMeme.equals("noImage")){
                updateWithMemeData(mText, String.valueOf(image_uri));
                pd.setVisibility(View.VISIBLE);
            }else if (meme.getDrawable() != null){
                updateNowMemeData(mText, String.valueOf(image_uri));
            }
            else {
                pd.setVisibility(View.VISIBLE);
                updateData(mText);
                pd.setVisibility(View.VISIBLE);
            }

        });

        update_remove.setOnClickListener(v -> {
            if (!editMeme.equals("noImage")){
                deleteWithoutVine(editPostId, editMeme);
                pd.setVisibility(View.VISIBLE);
            }else if (!editVine.equals("noVideo")){
                deleteWithoutMeme(editPostId, editVine);
                pd.setVisibility(View.VISIBLE);
            }
        });

    }

    private void deleteWithoutMeme(String editPostId, String editVine) {

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(editVine);
        picRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(editPostId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String child = ds.getKey();
                        dataSnapshot.getRef().child(Objects.requireNonNull(child)).child("vine").setValue("noVideo");
                        vines.setVisibility(View.GONE);
                        pd.setVisibility(View.GONE);
                        update_remove.setVisibility(View.GONE);
                        update_it.setVisibility(View.VISIBLE);
                        update_vine.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }).addOnFailureListener(e -> {

        });
    }

    private void deleteWithoutVine(String editPostId, String editMeme) {

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(editMeme);
        picRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(editPostId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String child = ds.getKey();
                        dataSnapshot.getRef().child(Objects.requireNonNull(child)).child("meme").setValue("noImage");
                        meme.setImageURI(null);
                        image_uri = null;
                        meme.setVisibility(View.GONE);
                        pd.setVisibility(View.GONE);
                        update_remove.setVisibility(View.GONE);
                        update_it.setVisibility(View.VISIBLE);
                        update_vine.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    pd.setVisibility(View.GONE);
                    Alerter.create(UpdatePost.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText(databaseError.getMessage())
                            .show();
                }
            });

        }).addOnFailureListener(e -> {
            pd.setVisibility(View.GONE);
            Alerter.create(UpdatePost.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });

    }

    private void updateNowVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Video");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap)
                        .addOnSuccessListener(aVoid -> {

                            Alerter.create(UpdatePost.this)
                                    .setTitle("Successful")
                                    .setIcon(R.drawable.ic_check_wt)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .setText("Post Updated")
                                    .show();

                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(getApplicationContext(), Post.class);
                                startActivity(intent);
                                finish();


                            }, 2000);

                            text.setText("");
                            vines.setVideoURI(null);
                            video_uri = null;
                            vines.setVisibility(View.GONE);
                            update_vine.setVisibility(View.GONE);
                            update_it.setVisibility(View.VISIBLE);
                            type.setText("Text");
                            pd.setVisibility(View.GONE);
                            update_remove.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> Alerter.create(UpdatePost.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show());
            }
        }).addOnFailureListener(e -> {
            pd.setVisibility(View.GONE);
            Alerter.create(UpdatePost.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });

    }
    private void updateWithVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Video");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    Alerter.create(UpdatePost.this)
                            .setTitle("Successful")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText("Post Updated")
                            .show();

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(getApplicationContext(), Post.class);
                        startActivity(intent);
                        finish();


                    }, 2000);
                    text.setText("");
                    update_remove.setVisibility(View.GONE);
                    vines.setVideoURI(null);
                    video_uri = null;
                    vines.setVisibility(View.GONE);
                    update_vine.setVisibility(View.GONE);
                    update_it.setVisibility(View.VISIBLE);
                    type.setText("Text");
                    pd.setVisibility(View.GONE);
                    update_remove.setVisibility(View.GONE);

                }).addOnFailureListener(e -> {
                    pd.setVisibility(View.GONE);
                    Alerter.create(UpdatePost.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText(e.getMessage())
                            .show();
                });
            }
        }).addOnFailureListener(e -> {
            pd.setVisibility(View.GONE);
            Alerter.create(UpdatePost.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });




    }

    private void updateNowMemeData(String mText, String uri) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        if (!uri.equals("noImage")) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("name", name);
                            hashMap.put("dp", dp);
                            hashMap.put("text", mText);
                            hashMap.put("type", "Image");
                            hashMap.put("meme", downloadUri);
                            hashMap.put("vine", "noVideo");
                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                            dRef.child(editPostId).updateChildren(hashMap)
                                    .addOnSuccessListener(aVoid -> {
                                        text.setText("");
                                        meme.setImageURI(null);
                                        image_uri = null;
                                        type.setText("Text");
                                        pd.setVisibility(View.GONE);
                                        update_it.setVisibility(View.VISIBLE);
                                        update_vine.setVisibility(View.GONE);
                                        update_remove.setVisibility(View.GONE);

                                        new Handler().postDelayed(() -> {
                                            Intent intent = new Intent(getApplicationContext(), Post.class);
                                            startActivity(intent);
                                            finish();


                                        }, 2000);

                                        Alerter.create(UpdatePost.this)
                                                .setTitle("Successful")
                                                .setIcon(R.drawable.ic_check_wt)
                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                .setDuration(10000)
                                                .enableSwipeToDismiss()
                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                                .setText("Post Updated")
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        pd.setVisibility(View.GONE);
                                        Alerter.create(UpdatePost.this)
                                                .setTitle("Error")
                                                .setIcon(R.drawable.ic_check_wt)
                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                .setDuration(10000)
                                                .enableSwipeToDismiss()
                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                                .setText(e.getMessage())
                                                .show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        pd.setVisibility(View.GONE);
                        Alerter.create(UpdatePost.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show();

                    });
        }

    }

    private void updateWithMemeData(String mText, String uri) {


        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();

            if (uriTask.isSuccessful()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Image");
                hashMap.put("meme", downloadUri);
                hashMap.put("vine", "noVideo");
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                    text.setText("");
                    meme.setImageURI(null);
                    image_uri = null;
                    type.setText("Text");
                    pd.setVisibility(View.GONE);
                    update_remove.setVisibility(View.GONE);
                    update_it.setVisibility(View.VISIBLE);
                    update_vine.setVisibility(View.GONE);
                    update_it.setVisibility(View.GONE);
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(getApplicationContext(), Post.class);
                        startActivity(intent);
                        finish();


                    }, 2000);

                    Alerter.create(UpdatePost.this)
                            .setTitle("Successful")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText("Post Updated")
                            .show();


                }).addOnFailureListener(e -> {

                    pd.setVisibility(View.GONE);
                    Alerter.create(UpdatePost.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_check_wt)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText(e.getMessage())
                            .show();
                });
            }

        }).addOnFailureListener(e -> {

            pd.setVisibility(View.GONE);
            Alerter.create(UpdatePost.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });

    }

    private void updateData(String mText) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("name", name);
        hashMap.put("dp", dp);
        hashMap.put("text", mText);
        hashMap.put("meme", "noImage");
        hashMap.put("type", "Text");
        hashMap.put("vine", "noVideo");
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
        dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
            Alerter.create(UpdatePost.this)
                    .setTitle("Successful")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText("Post Updated")
                    .show();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(getApplicationContext(), Post.class);
                startActivity(intent);
                finish();


            },2000);

            text.setText("");
            vines.setVideoURI(null);
            video_uri = null;
            vines.setVisibility(View.GONE);
            update_vine.setVisibility(View.GONE);
            update_it.setVisibility(View.VISIBLE);
            type.setText("Text");
            pd.setVisibility(View.GONE);
            update_remove.setVisibility(View.GONE);
            update_remove.setVisibility(View.GONE);
            meme.setImageURI(null);
            image_uri = null;

        }).addOnFailureListener(e -> {

            pd.setVisibility(View.GONE);
            Alerter.create(UpdatePost.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });

    }

    private void loadPostData(String editPostId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    editText = ""+ds.child("text").getValue();
                    editMeme = ""+ds.child("meme").getValue();
                    editVine = ""+ds.child("vine").getValue();
                    update_remove.setVisibility(View.GONE);
                    update_it.setVisibility(View.VISIBLE);
                    update_vine.setVisibility(View.GONE);
                    text.setText(editText);
                    if (!editMeme.equals("noImage")){
                        try {
                            Picasso.get().load(editMeme).into(meme);
                            meme.setVisibility(View.VISIBLE);
                            vines.setVisibility(View.GONE);
                            update_it.setVisibility(View.VISIBLE);
                            update_vine.setVisibility(View.GONE);
                            update_remove.setVisibility(View.VISIBLE);
                        }catch (Exception ignored){

                        }
                    }

                    if (!editVine.equals("noVideo")){
                        try {
                            vines.setVideoPath(editVine);
                            vines.setVisibility(View.VISIBLE);
                            meme.setVisibility(View.GONE);
                            update_it.setVisibility(View.GONE);
                            update_remove.setVisibility(View.VISIBLE);
                            update_vine.setVisibility(View.VISIBLE);
                        }catch (Exception ignored){

                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.setVisibility(View.GONE);
                Alerter.create(UpdatePost.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText(databaseError.getMessage())
                        .show();
            }
        });

    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(UpdatePost.this)
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
                Alerter.create(UpdatePost.this)
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE   && data != null && data.getData() != null){
            image_uri = data.getData();
            meme.setImageURI(image_uri);
            update_vine.setVisibility(View.GONE);
            update_it.setVisibility(View.VISIBLE);
            meme.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            remove.setVisibility(View.VISIBLE);
            type.setText("Image");
        }if (image_uri == null){
            meme.setVisibility(View.GONE);
            update_it.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            vines.setVideoURI(video_uri);
            update_it.setVisibility(View.GONE);
            vines.setVisibility(View.VISIBLE);
            meme.setVisibility(View.GONE);
            type.setText("Video");
            remove.setVisibility(View.VISIBLE);
            update_vine.setVisibility(View.VISIBLE);
        }
        if (video_uri == null){
            update_vine.setVisibility(View.GONE);
            vines.setVisibility(View.VISIBLE);
            update_it.setVisibility(View.VISIBLE);


        }
    }

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

}