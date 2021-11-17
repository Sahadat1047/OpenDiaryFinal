package com.spacester.opendiaryp.authPhone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.MainActivity;
import com.spacester.opendiaryp.R;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationId;
    private EditText mVerify;
    TextView textView7;
    ProgressBar progressBar7;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        mAuth = FirebaseAuth.getInstance();
        progressBar7 = findViewById(R.id.progressBar7);
        textView7 = findViewById(R.id.textView7);
        textView7.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyOTP.this, GenerateOTP.class);
            startActivity(intent);
        });
        mVerify = findViewById(R.id.editText);
        Button button5 = findViewById(R.id.button5);
        final String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);
        button5.setOnClickListener(v -> {
            progressBar7.setVisibility(View.VISIBLE);
            String code = mVerify.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6){
                Alerter.create(VerifyOTP.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter OTP")
                        .show();
                progressBar7.setVisibility(View.INVISIBLE);
            }else {
                verifyCode(code);
            }
        });
    }

    private  void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyOTP.this, task -> {
                    if (task.isSuccessful()) {
                        final String phone = getIntent().getStringExtra("phonenumber");
                        Query userQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(phone);
                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()>0){
                                    Intent intent = new Intent(VerifyOTP.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    progressBar7.setVisibility(View.INVISIBLE);
                                }else {
                                    Intent intent = new Intent(VerifyOTP.this, Final.class);
                                    intent.putExtra("phone", phone);
                                    startActivity(intent);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    finish();
                                    progressBar7.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Alerter.create(VerifyOTP.this)
                                        .setTitle("Error")
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorPrimary)
                                        .setDuration(10000)
                                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                        .enableSwipeToDismiss()
                                        .setText(databaseError.getMessage())
                                        .show();
                                progressBar7.setVisibility(View.INVISIBLE);
                            }
                        });


                    } else {
                        // Sign in failed, display a message and update the UI
                        String msg = Objects.requireNonNull(task.getException()).getMessage();
                        Alerter.create(VerifyOTP.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText(Objects.requireNonNull(msg))
                                .show();
                        progressBar7.setVisibility(View.INVISIBLE);

                    }
                });
    }
    private void sendVerificationCode(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                mVerify.setText(code);
                verifyCode(code);
                progressBar7.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Alerter.create(VerifyOTP.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setDuration(10000)
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .enableSwipeToDismiss()
                    .setText(Objects.requireNonNull(e.getMessage()))
                    .show();
            progressBar7.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            progressBar7.setVisibility(View.INVISIBLE);
        }
    };
}
