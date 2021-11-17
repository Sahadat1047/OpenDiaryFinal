package com.spacester.opendiaryp.authPhone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.MainActivity;
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;
import java.util.HashMap;
import java.util.Objects;

public class Final extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressBar progressBar5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        mAuth = FirebaseAuth.getInstance();
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        Button button = findViewById(R.id.button3);
        progressBar5 = findViewById(R.id.progressBar5);
        button.setOnClickListener(v -> {
            progressBar5.setVisibility(View.VISIBLE);
            final String email = mEmail.getText().toString().trim();
            final String password = mPassword.getText().toString().trim();
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(password);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0){

                        Alerter.create(Final.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Username already exist")
                                .show();
                        progressBar5.setVisibility(View.INVISIBLE);

                    }else {
                        if (TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
                            Alerter.create(Final.this)
                                    .setTitle("Error")
                                    .setIcon(R.drawable.ic_error)
                                    .setBackgroundColorRes(R.color.colorPrimary)
                                    .setDuration(10000)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .enableSwipeToDismiss()
                                    .setText("Enter name & username ")
                                    .show();
                            progressBar5.setVisibility(View.INVISIBLE);

                        } else {
                            final String phonenumber = getIntent().getStringExtra("phone");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userid = Objects.requireNonNull(firebaseUser).getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("name", email);
                            hashMap.put("email", "");
                            hashMap.put("username", password);
                            hashMap.put("bio", "");
                            hashMap.put("phone", phonenumber);
                            hashMap.put("location","");
                            hashMap.put("verified","");
                            hashMap.put("link","");
                            hashMap.put("status","online");
                            hashMap.put("typingTo","noOne");
                            hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");
                            mDatabase.setValue(hashMap).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Final.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    progressBar5.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Alerter.create(Final.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .setDuration(10000)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .enableSwipeToDismiss()
                            .setText(databaseError.getMessage())
                            .show();
                    progressBar5.setVisibility(View.INVISIBLE);
                }
            });
        });
    }

}
