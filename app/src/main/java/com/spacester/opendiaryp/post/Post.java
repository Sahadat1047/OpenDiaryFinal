package com.spacester.opendiaryp.post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
public class Post extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    ImageView meme,cancel;
    VideoView vines;
    ConstraintLayout add_meme,add_vines,remove_lt;
    Button post, post_vine;
    EditText text;
    TextView mName,type;
    CircleImageView circleImageView3;
    ProgressBar pd;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String name, dp, id;
    private Uri image_uri, video_uri;
    MediaController mediaController;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        meme = findViewById(R.id.meme);
        add_meme = findViewById(R.id.constraintLayout3);
        post = findViewById(R.id.post_it);
        post_vine = findViewById(R.id.post_vine);
        text = findViewById(R.id.post_text);
        type = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        circleImageView3 = findViewById(R.id.circleImageView3);
        pd = findViewById(R.id.pb);
        cancel = findViewById(R.id.imageView);
        cancel.setOnClickListener(v -> onBackPressed());
        add_vines = findViewById(R.id.vines_lt);
        remove_lt = findViewById(R.id.remove_lt);
        vines = findViewById(R.id.vines);
        mediaController = new MediaController(this);
        vines.setMediaController(mediaController);
        mediaController.setAnchorView(vines);
        MediaController ctrl = new MediaController(Post.this);
        ctrl.setVisibility(View.GONE);
        vines.setMediaController(ctrl);
        vines.start();
        vines.setOnPreparedListener(mp -> mp.setLooping(true));
        remove_lt.setOnClickListener(v -> {

            meme.setImageURI(null);
            image_uri = null;
post.setVisibility(View.VISIBLE);
vines.setVisibility(View.GONE);
post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.GONE);
            type.setText("Text");
        });

        post_vine.setOnClickListener(v -> {
            pd.setVisibility(View.VISIBLE);
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(Post.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter caption")
                        .show();
            }else {
                uploadVine(mText, String.valueOf(video_uri));
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

        add_meme.setOnClickListener(v -> {

            type.setText("Meme");
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
        post.setOnClickListener(v -> {
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(Post.this)
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
            if (image_uri == null){
                pd.setVisibility(View.VISIBLE);
                uploadData(mText, "noImage");
            }
            else {
                uploadData(mText, String.valueOf(image_uri));
                pd.setVisibility(View.VISIBLE);
            }

        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type!=null){
          if ("text/plain".equals(type)){
              sendText(intent);
          }
        else if (type.startsWith("image")){
                sendImage(intent);
            }else if (type.startsWith("video")){
                sendVideo(intent);
            }
        }

    }

    private void sendVideo(Intent intent) {
        Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null){
            video_uri = videoUri;
            vines.setVideoURI(video_uri);
            vines.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            post.setVisibility(View.GONE);
        }
    }

    private void sendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null){
            image_uri = imageUri;
            meme.setImageURI(image_uri);
            meme.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);

        }
    }

    private void sendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText!=null){
            text.setText(sharedText);
        }
    }

    private void uploadData(String mText, String uri) {
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
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("name", name);
                            hashMap.put("dp", dp);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("text", mText);
                            hashMap.put("type", "Image");
                            hashMap.put("pViews", "0");
                            hashMap.put("pComments","0");
                            hashMap.put("meme", downloadUri);
                            hashMap.put("vine", "noVideo");
                            hashMap.put("pTime", timeStamp);
                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                            dRef.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(aVoid -> {
                                        text.setText("");
                                        meme.setImageURI(null);
                                        image_uri = null;
                                        remove_lt.setVisibility(View.GONE);
                                        pd.setVisibility(View.GONE);
                                        type.setText("Text");
                                        Alerter.create(Post.this)
                                                .setTitle("Successful")
                                                .setIcon(R.drawable.ic_check_wt)
                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                .setDuration(10000)
                                                .enableSwipeToDismiss()
                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                                .setText("Post Uploaded")
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        pd.setVisibility(View.GONE);
                                        Alerter.create(Post.this)
                                                .setTitle("Successful")
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
                        Alerter.create(Post.this)
                                .setTitle("Successful")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show();
                    });
        }else {
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("pId", timeStamp);
                hashMap.put("text", mText);
            hashMap.put("pViews", "0");
                hashMap.put("meme", "noImage");
            hashMap.put("type", "Text");
                hashMap.put("vine", "noVideo");
            hashMap.put("pComments","0");
                hashMap.put("pTime", timeStamp);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                            text.setText("");
                            meme.setImageURI(null);
                            image_uri = null;
                            type.setText("Text");
                            pd.setVisibility(View.GONE);

                            Alerter.create(Post.this)
                                    .setTitle("Successful")
                                    .setIcon(R.drawable.ic_check_wt)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .setText("Post Uploaded")
                                    .show();
                        })
                        .addOnFailureListener(e -> {
                            pd.setVisibility(View.GONE);
                            Alerter.create(Post.this)
                                    .setTitle("Successful")
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

    private void uploadVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("pId", timeStamp);
                hashMap.put("text", mText);
                hashMap.put("type", "Video");
                hashMap.put("pViews", "0");
                hashMap.put("pComments","0");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                hashMap.put("pTime", timeStamp);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {

                            Alerter.create(Post.this)
                                    .setTitle("Successful")
                                    .setIcon(R.drawable.ic_check_wt)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .setText("Post Uploaded")
                                    .show();

                            text.setText("");
                            vines.setVideoURI(null);
                            video_uri = null;
                            vines.setVisibility(View.GONE);
                            post_vine.setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                            type.setText("Text");
                            pd.setVisibility(View.GONE);
                            remove_lt.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> Alerter.create(Post.this)
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
            Alerter.create(Post.this)
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
                Alerter.create(Post.this)
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
                Alerter.create(Post.this)
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
            post_vine.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            meme.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            type.setText("Image");
        }if (image_uri == null){
            meme.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            vines.setVideoURI(video_uri);
            post.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            vines.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            meme.setVisibility(View.GONE);
            type.setText("Video");
        }
        if (video_uri == null){
            vines.setVisibility(View.GONE);
        }
    }

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

}