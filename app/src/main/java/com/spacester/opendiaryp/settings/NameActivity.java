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

@SuppressWarnings({"ConstantConditions", "Convert2Lambda"})
public class NameActivity extends AppCompatActivity {

    EditText mName;
    ImageView settings,menu;
    private DatabaseReference mDatabase;
    private String userId;
    ProgressBar progressBar8;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_name);

        mName = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        progressBar8 = findViewById(R.id.progressBar8);
        progressBar8.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                progressBar8.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar8.setVisibility(View.GONE);
                Alerter.create(NameActivity.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText(databaseError.getMessage())
                        .show();

            }
        });
        menu.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String name = mName.getText().toString().trim();
            if(TextUtils.isEmpty(name)) {
                Alerter.create(NameActivity.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setText("Enter Name")
                        .show();
                progressBar8.setVisibility(View.GONE);
            } else {

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String child = ds.getKey();
                            if (dataSnapshot.child(child).hasChild("Comments")){
                                String child1 = ""+dataSnapshot.child(child).getKey();
                                Query child2 = FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("id").equalTo(userId);
                                child2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            String child = ds.getKey();
                                            dataSnapshot.getRef().child(child).child("mane").setValue(name);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name").equalTo(name);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        addUsername(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Alerter.create(NameActivity.this)
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

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                Query query = ref.orderByChild("id").equalTo(userId);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String child = ds.getKey();
                            dataSnapshot.getRef().child(child).child("name").setValue(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    private void addUsername(String name) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        mDatabase.updateChildren(hashMap);
        StyleableToast st = new StyleableToast(Objects.requireNonNull(NameActivity.this), "Name updated", Toast.LENGTH_LONG);
        st.setBackgroundColor(Color.parseColor("#001E55"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_check_wt);
        st.setMaxAlpha();
        st.show();
        progressBar8.setVisibility(View.INVISIBLE);
    }


}