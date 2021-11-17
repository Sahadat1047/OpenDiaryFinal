package com.spacester.opendiaryp.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
public class CreateGroup extends AppCompatActivity {

    ImageView back,edit;
    CircleImageView profile_image;
    Button create;
    EditText mName,mUsername,mBio,mLink;
    SharedPref sharedPref;
    private Uri image_uri;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private FirebaseAuth mAuth;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        back = findViewById(R.id.imageView3);
        edit = findViewById(R.id.edit);
        profile_image = findViewById(R.id.profile_image);
        create = findViewById(R.id.button3);
        mName = findViewById(R.id.password);
        mUsername = findViewById(R.id.username);
        mBio = findViewById(R.id.bio);
        mLink = findViewById(R.id.link);
        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(v -> onBackPressed());
        edit.setOnClickListener(v -> {
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
        create.setOnClickListener(v -> {
       username = mUsername.getText().toString().trim();
            Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("gUsername").equalTo(username);
            usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0){
                        Alerter.create(CreateGroup.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Username already exist")
                                .show();

                    }else {
                        if (TextUtils.isEmpty(username)){
                            Alerter.create(CreateGroup.this)
                                    .setTitle("Error")
                                    .setIcon(R.drawable.ic_error)
                                    .setBackgroundColorRes(R.color.colorPrimary)
                                    .setDuration(10000)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .enableSwipeToDismiss()
                                    .setText("Enter Username")
                                    .show();


                        }else {
                            createGroup();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Alerter.create(CreateGroup.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .setDuration(10000)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .enableSwipeToDismiss()
                            .setText(databaseError.getMessage())
                            .show();

                }
            });

        });

    }

    private void createGroup() {
        String name = mName.getText().toString();
        String bio = mBio.getText().toString();
        String link = mLink.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username)){
            Alerter.create(CreateGroup.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setDuration(10000)
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .enableSwipeToDismiss()
                    .setText("Enter name & username")
                    .show();
            return;
        }
        String timeStamp = ""+System.currentTimeMillis();
        if (image_uri == null){
            createNoImageGroup(""+timeStamp,
                    ""+name,""+username,
                    ""+bio,""+link);
        }else {
            String fileNameAndPAth = "Group_image/"+"image" + timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPAth);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()){
                            createImageGroup(""+timeStamp,
                                    ""+name,""+username,
                                    ""+bio,""+link, ""+downloadUri);
                        }
                    }).addOnFailureListener(e -> {

                    });

        }
    }

    private void createImageGroup(String timeStamp, String name, String username,String bio,String link, String downloadUri) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+timeStamp);
        hashMap.put("gName", ""+name);
        hashMap.put("gUsername", ""+username);
        hashMap.put("gBio", ""+bio);
        hashMap.put("gLink", ""+link);
        hashMap.put("gIcon", ""+downloadUri);
        hashMap.put("timestamp", ""+timeStamp);
        hashMap.put("createdBy", ""+mAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("id", mAuth.getUid());
                    hashMap1.put("role","creator");
                    hashMap.put("timestamp", timeStamp);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(timeStamp).child("Participants").child(Objects.requireNonNull(mAuth.getUid()))
                            .setValue(hashMap1)
                            .addOnSuccessListener(aVoid1 -> {
                                Alerter.create(CreateGroup.this)
                                        .setTitle("Successful")
                                        .setIcon(R.drawable.ic_check_wt)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText("Group Created")
                                        .show();
                                new Handler().postDelayed(() -> onBackPressed(), 1000);
                            }).addOnFailureListener(e -> Alerter.create(CreateGroup.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText(e.getMessage())
                            .show());

                }).addOnFailureListener(e -> Alerter.create(CreateGroup.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText(e.getMessage())
                        .show());

    }

    private void createNoImageGroup(String timeStamp, String name, String username, String bio, String link) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+timeStamp);
        hashMap.put("gName", ""+name);
        hashMap.put("gUsername", ""+username);
        hashMap.put("gBio", ""+bio);
        hashMap.put("gLink", ""+link);
        hashMap.put("gIcon", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/d-group.jpg?alt=media&token=bfaaa505-1c06-4b2f-bc58-8b82b45a8877");
        hashMap.put("timestamp", ""+timeStamp);
        hashMap.put("createdBy", ""+mAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("id", mAuth.getUid());
                    hashMap1.put("role","creator");
                    hashMap.put("timestamp", timeStamp);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(timeStamp).child("Participants").child(Objects.requireNonNull(mAuth.getUid()))
                            .setValue(hashMap1)
                            .addOnSuccessListener(aVoid1 -> {
                                Alerter.create(CreateGroup.this)
                                        .setTitle("Successful")
                                        .setIcon(R.drawable.ic_check_wt)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText("Group Created")
                                        .show();
                                new Handler().postDelayed(() -> onBackPressed(), 1000);
                            }).addOnFailureListener(e -> Alerter.create(CreateGroup.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setDuration(10000)
                            .enableSwipeToDismiss()
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .setText(e.getMessage())
                            .show());

                }).addOnFailureListener(e -> Alerter.create(CreateGroup.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText(e.getMessage())
                        .show());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Alerter.create(CreateGroup.this)
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(image_uri).into(profile_image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}