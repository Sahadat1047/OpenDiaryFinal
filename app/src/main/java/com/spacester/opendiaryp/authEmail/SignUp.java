package com.spacester.opendiaryp.authEmail;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.menu.Policy;
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;
import java.util.HashMap;
import java.util.Objects;


public class SignUp extends AppCompatActivity {
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    TextView textView2;
    DatabaseReference reference;
    ProgressBar progressBar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TextView register = findViewById(R.id.textView3);
        textView2 = findViewById(R.id.textView2);
        textView2.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Policy.class);
            startActivity(intent);
        });
        register.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();
        progressBar2 = findViewById(R.id.progressBar2);
        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mPassword = findViewById(R.id.password);
        Button button = findViewById(R.id.button4);
        button.setOnClickListener(view -> {
            progressBar2.setVisibility(View.VISIBLE);
            final   String email = mEmail.getText().toString().trim();
            final   String name = mName.getText().toString().trim();
            final String password = mPassword.getText().toString().trim();

            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0){

                        Alerter.create(SignUp.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Email already exist")
                                .show();
                        progressBar2.setVisibility(View.INVISIBLE);

                    }else {
                        if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                            Alerter.create(SignUp.this)
                                    .setTitle("Error")
                                    .setIcon(R.drawable.ic_error)
                                    .setBackgroundColorRes(R.color.colorPrimary)
                                    .setDuration(10000)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .enableSwipeToDismiss()
                                    .setText("Enter email & name & password")
                                    .show();
                            progressBar2.setVisibility(View.INVISIBLE);

                        } else if (password.length()<6){
                            Alerter.create(SignUp.this)
                                    .setTitle("Error")
                                    .setIcon(R.drawable.ic_error)
                                    .setBackgroundColorRes(R.color.colorPrimary)
                                    .setDuration(10000)
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .enableSwipeToDismiss()
                                    .setText("Password should have minimum 6 characters")
                                    .show();
                            progressBar2.setVisibility(View.INVISIBLE);
                        }else {
                            register_btn(email,name,password);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Alerter.create(SignUp.this)
                            .setTitle("Error")
                            .setIcon(R.drawable.ic_error)
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .setDuration(10000)
                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                            .enableSwipeToDismiss()
                            .setText(databaseError.getMessage())
                            .show();
                    progressBar2.setVisibility(View.INVISIBLE);
                }
            });


        });

    }

    private void register_btn(final String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userid = Objects.requireNonNull(firebaseUser).getUid();

                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", userid);
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("username", "");
                hashMap.put("bio", "");
                hashMap.put("verified","");
                hashMap.put("location","");
                hashMap.put("status","online");
                hashMap.put("typingTo","noOne");
                hashMap.put("link","");
                hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/memespace-34a96.appspot.com/o/avatar.jpg?alt=media&token=8b875027-3fa4-4da4-a4d5-8b661d999472");
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Intent intent = new Intent(SignUp.this, Finish.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        progressBar2.setVisibility(View.INVISIBLE);
                    }
                });
            }else {
                String msg = Objects.requireNonNull(task.getException()).getMessage();
                Alerter.create(SignUp.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText(Objects.requireNonNull(msg))
                        .show();
                progressBar2.setVisibility(View.INVISIBLE);
            }
        });

    }
}
