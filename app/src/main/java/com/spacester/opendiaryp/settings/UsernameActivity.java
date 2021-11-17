package com.spacester.opendiaryp.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("Convert2Lambda")
public class UsernameActivity extends AppCompatActivity {

    EditText mUsername;
    private DatabaseReference mDatabase;
    ImageView button,settings;
    ProgressBar progressBar8;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_username);
        button = findViewById(R.id.menu);
        button = findViewById(R.id.menu);
        mUsername = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar8 = findViewById(R.id.progressBar8);
        progressBar8.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                mUsername.setText(name);
                progressBar8.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar8.setVisibility(View.GONE);
                Alerter.create(Objects.requireNonNull(UsernameActivity.this))
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText(databaseError.getMessage())
                        .show();

            }
        });
        button.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String username = mUsername.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Alerter.create(UsernameActivity.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText("Enter Username")
                        .show();
                progressBar8.setVisibility(View.GONE);

            } else {
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(username);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            Alerter.create(UsernameActivity.this)
                                    .setTitle("Error")
                                    .setIcon(R.drawable.ic_error)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setText("Username already exist")
                                    .show();
                            progressBar8.setVisibility(View.GONE);

                        } else {

                            addUsername(username);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Alerter.create(UsernameActivity.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setText(databaseError.getMessage())
                                .show();
                        progressBar8.setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    private void addUsername(String username) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username);
        mDatabase.updateChildren(hashMap);
        StyleableToast st = new StyleableToast(Objects.requireNonNull(UsernameActivity.this), "Username updated", Toast.LENGTH_LONG);
        st.setBackgroundColor(Color.parseColor("#001E55"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_check_wt);
        st.setMaxAlpha();
        st.show();
        progressBar8.setVisibility(View.GONE);
    }
}