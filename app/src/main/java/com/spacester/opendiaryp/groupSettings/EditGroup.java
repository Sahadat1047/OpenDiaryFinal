package com.spacester.opendiaryp.groupSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spacester.opendiaryp.Adpref;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class EditGroup extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout name_layout, username_layout, bio_layout, web_layout, location_layout;
    ImageView edit, settings, menu;
    CircleImageView profile_image;
    ProgressBar pb;
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;

    @SuppressLint("StaticFieldLeak")
    static EditGroup INSTANCE;

    String dbImage;
    private Uri image_uri;
    SharedPref sharedPref;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    String GroupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        INSTANCE=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");

        name_layout = findViewById(R.id.name_layout);
        username_layout = findViewById(R.id.username_layout);
        bio_layout = findViewById(R.id.bio_layout);
        web_layout = findViewById(R.id.web_layout);
        location_layout = findViewById(R.id.location_layout);
        edit = findViewById(R.id.edit);
        menu = findViewById(R.id.imageView4);
        settings = findViewById(R.id.imageView3);
        profile_image = findViewById(R.id.profile_image);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        createBottomSheetDialog();

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Adpref adpref;
        adpref = new Adpref(this);
        if (adpref.loadAdsModeState()){
            mAdView.setVisibility(View.VISIBLE);

        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbImage = Objects.requireNonNull(dataSnapshot.child("gIcon").getValue()).toString();
                try {
                    Picasso.get().load(dbImage).into(profile_image);
                    pb.setVisibility(View.GONE);
                }
                catch (Exception e ){
                    Picasso.get().load(R.drawable.avatar).into(profile_image);
                    pb.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Alerter.create(EditGroup.this)
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
        settings.setOnClickListener(view -> onBackPressed());

        name_layout.setOnClickListener(view -> {
            Intent intent2 = new Intent(EditGroup.this, GnameActivity.class);
            startActivity(intent2);
        });

        username_layout.setOnClickListener(view -> {
            Intent intent2 = new Intent(EditGroup.this, GUsernameActivity.class);
            startActivity(intent2);
        });

        bio_layout.setOnClickListener(view -> {
            Intent intent2 = new Intent(EditGroup.this, GbioActivity.class);
            startActivity(intent2);
        });

        web_layout.setOnClickListener(view -> {
            Intent intent2 = new Intent(EditGroup.this, gLinkActivity.class);
            startActivity(intent2);
        });

        edit.setOnClickListener(v -> bottomSheetDialog.show());

        menu.setOnClickListener(v -> {
            pb.setVisibility(View.VISIBLE);
            String filePathName = "group_profile_images/" + ""+GroupId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadImageUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("gIcon", "" + downloadImageUri);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(GroupId).updateChildren(hashMap)
                            .addOnSuccessListener(aVoid -> {
                                Alerter.create(EditGroup.this)
                                        .setTitle("Successful")
                                        .setIcon(R.drawable.ic_check_wt)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .enableSwipeToDismiss()
                                        .setText("Profile Photo Updated")
                                        .show();
                                menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Alerter.create(EditGroup.this)
                                        .setTitle("Error")
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText(e.getMessage())
                                        .show();
                                menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                            });
                }
            });

        });

    }
    public static EditGroup getActivityInstance()
    {
        return INSTANCE;
    }

    public String getGroupId()
    {
        return this.GroupId;
    }
    private void createBottomSheetDialog() {
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.edit_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayout3);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
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
                pickImageFromGallery();
            } else {
                Alerter.create(EditGroup.this)
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
            image_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(image_uri).into(profile_image);
            menu.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
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


                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(dbImage);
                picRef.delete().addOnSuccessListener(aVoid -> {

                    //ProfileSet
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/d-group.jpg?alt=media&token=bfaaa505-1c06-4b2f-bc58-8b82b45a8877");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(GroupId).updateChildren(hashMap)
                            .addOnSuccessListener(aVoid1 -> {
                                Alerter.create(EditGroup.this)
                                        .setTitle("Successful")
                                        .setIcon(R.drawable.ic_check_wt)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .enableSwipeToDismiss()
                                        .setText("Profile Photo Deleted")
                                        .show();
                                menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                                bottomSheetDialog.cancel();
                            })
                            .addOnFailureListener(e -> {
                                Alerter.create(EditGroup.this)
                                        .setTitle("Error")
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText(e.getMessage())
                                        .show();
                                menu.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.GONE);
                                bottomSheetDialog.cancel();
                            });

                }).addOnFailureListener(e -> {
                });



                break;
        }
    }
}