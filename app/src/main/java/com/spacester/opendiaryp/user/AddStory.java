package com.spacester.opendiaryp.user;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("ALL")
public class AddStory extends AppCompatActivity  {

    ImageView imageView3, imageView4;
    ImageView imageView14;
    TextView textView13;
    ImageView imageView15;

    String myUid;
    String storyId;
    long timeend;
    String mText;

    DatabaseReference reference;
    SharedPref sharedPref;
    private Uri image_uri;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

         reference = FirebaseDatabase.getInstance().getReference("Story").child(myUid);
         storyId = reference.push().getKey();
         timeend = System.currentTimeMillis()+86400000;

        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView14 = findViewById(R.id.imageView14);
        textView13 = findViewById(R.id.textView13);
        imageView15 = findViewById(R.id.imageView15);

        if (image_uri == null) {
            imageView15.setVisibility(View.GONE);
        }

        imageView4.setOnClickListener(v -> {
            String image = image_uri.toString();
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String filePathAndName = "Story/" + "Story_" + timeStamp;
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(image)).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                if (uriTask.isSuccessful()) {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageUri", downloadUri);
                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                    hashMap.put("timeend", timeend);
                    hashMap.put("storyid", storyId);
                    hashMap.put("userid", myUid);

                    reference.child(storyId).setValue(hashMap);
                    onBackPressed();

                }
            }).addOnFailureListener(e -> Alerter.create(AddStory.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show());
        });

        imageView14.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });
        textView13.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });
        imageView3.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(AddStory.this)
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
                Alerter.create(AddStory.this)
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
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_uri = Objects.requireNonNull(data).getData();
            imageView15.setImageURI(image_uri);
            textView13.setVisibility(View.GONE);
            imageView14.setVisibility(View.GONE);
            imageView15.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}