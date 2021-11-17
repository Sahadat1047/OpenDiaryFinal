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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.MainActivity;
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;

import java.util.Objects;

public class SignIn extends AppCompatActivity {
    TextView register,forgot;
    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        forgot = findViewById(R.id.forgot);
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        Button button = findViewById(R.id.button3);
        progressBar = findViewById(R.id.progressBar);


        register.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
        });
        forgot.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, ForgotPassword.class);
            startActivity(intent);
        });
        button.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Alerter.create(SignIn.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter email & password")
                        .show();
                progressBar.setVisibility(View.INVISIBLE);

            }else {
                login(email,password);
            }
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignIn.this, task -> {
            if (task.isSuccessful()){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Alerter.create(SignIn.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText(databaseError.getMessage())
                                .show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }else {
                String msg = Objects.requireNonNull(task.getException()).getMessage();
                Alerter.create(SignIn.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText(Objects.requireNonNull(msg))
                        .show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}

