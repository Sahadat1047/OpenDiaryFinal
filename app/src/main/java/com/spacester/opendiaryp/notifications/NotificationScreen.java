package com.spacester.opendiaryp.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.opendiaryp.adapter.AdapterNotify;
import com.spacester.opendiaryp.model.ModelNotification;
import com.spacester.opendiaryp.R;
import com.spacester.opendiaryp.SharedPref;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationScreen extends AppCompatActivity {

     String userId;
     FirebaseAuth mAuth;
    ProgressBar pg;
    RecyclerView recyclerView;
    ImageView imageView3;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notifications;
    private AdapterNotify adapterNotify;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(v -> onBackPressed());
        pg = findViewById(R.id.pg);
        pg.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.users);
        firebaseAuth = FirebaseAuth.getInstance();
        getAllNotifications();
    }

    private void getAllNotifications() {
        notifications = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelNotification modelNotification = ds.getValue(ModelNotification.class);
                            notifications.add(modelNotification);
                        }
                        adapterNotify = new AdapterNotify(NotificationScreen.this, notifications);
                        recyclerView.setAdapter(adapterNotify);
                        pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}