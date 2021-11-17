package com.spacester.opendiaryp.groupSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

@SuppressWarnings({"unchecked", "rawtypes", "Convert2Lambda"})
public class gLinkActivity extends AppCompatActivity {

    SharedPref sharedPref;
    EditText mName;
    ImageView settings, menu;
    private DatabaseReference mDatabase;
    String GroupId;
    ProgressBar progressBar8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_link);
        mName = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.menu);
        progressBar8 = findViewById(R.id.progressBar8);
        progressBar8.setVisibility(View.VISIBLE);
        GroupId= EditGroup.getActivityInstance().getGroupId();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("gLink").getValue()).toString();
                mName.setText(name);
                progressBar8.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Alerter.create(Objects.requireNonNull(gLinkActivity.this))
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
        menu.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String name = mName.getText().toString();
            Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("gLink").equalTo(name);
            usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    addUsername(name);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Alerter.create(Objects.requireNonNull(gLinkActivity.this))
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
        });

    }

    private void addUsername(String name) {
        Map hashMap = new HashMap();
        hashMap.put("gLink", name);
        mDatabase.updateChildren(hashMap);
        StyleableToast st = new StyleableToast(Objects.requireNonNull(Objects.requireNonNull(gLinkActivity.this)), "Link updated", Toast.LENGTH_LONG);
        st.setBackgroundColor(Color.parseColor("#001E55"));
        st.setTextColor(Color.WHITE);
        st.setIcon(R.drawable.ic_check_wt);
        st.setMaxAlpha();
        st.show();
        progressBar8.setVisibility(View.GONE);
    }
}
