package com.spacester.opendiaryp.menu;

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
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    EditText pass,name;
    ImageView imageView3,imageView4;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        pass = findViewById(R.id.pass);
        name = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView3.setOnClickListener(v -> onBackPressed());
        imageView4.setOnClickListener(v -> {
            String oldP = name.getText().toString().trim();
            String newP = pass.getText().toString().trim();
            if (TextUtils.isEmpty(oldP)){
                Alerter.create(ChangePassword.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter your current password")
                        .show();
                return;
            }else if (TextUtils.isEmpty(newP)){
                Alerter.create(ChangePassword.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter your new password")
                        .show();
                return;
            }else if (newP.length()<6){
                Alerter.create(ChangePassword.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Password should have minimum 6 characters")
                        .show();
                return;
            }
            updatePassword(oldP,newP);
        });
    }

    private void updatePassword(String oldP, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), oldP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updatePassword(newP)
                        .addOnSuccessListener(aVoid1 -> Alerter.create(ChangePassword.this)
                                .setTitle("Succes")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Password updated")
                                .show()).addOnFailureListener(e -> Alerter.create(ChangePassword.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText(Objects.requireNonNull(e.getMessage()))
                                .show())).addOnFailureListener(e -> Alerter.create(ChangePassword.this)
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