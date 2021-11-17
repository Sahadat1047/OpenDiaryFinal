package com.spacester.opendiaryp.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;

import java.util.Objects;

public class ChangeEmail extends AppCompatActivity {

    EditText pass,name;
    ImageView imageView3,imageView4;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView3.setOnClickListener(v -> onBackPressed());
        imageView4.setOnClickListener(v -> {
            String newE = name.getText().toString().trim();
            String newP = pass.getText().toString().trim();
            if (TextUtils.isEmpty(newE)){
                Alerter.create(ChangeEmail.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter your new Email")
                        .show();
                return;
            }
            if (TextUtils.isEmpty(newP)){
                Alerter.create(ChangeEmail.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter your password")
                        .show();
                return;
            }
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){
                        Alerter.create(ChangeEmail.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Email already exist")
                                .show();
                        return;
                    }
                    updateEmail(newE,newP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void updateEmail(String newE, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), newP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updateEmail(newE)
                        .addOnSuccessListener(aVoid1 -> Alerter.create(ChangeEmail.this)
                                .setTitle("Succes")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Email updated")
                                .show()).addOnFailureListener(e -> Alerter.create(ChangeEmail.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText(Objects.requireNonNull(e.getMessage()))
                                .show())).addOnFailureListener(e -> Alerter.create(ChangeEmail.this)
                                        .setTitle("Error")
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorPrimary)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText(Objects.requireNonNull(e.getMessage()))
                                        .show());
    }
}