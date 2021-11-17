package com.spacester.opendiaryp.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.spacester.opendiaryp.authEmail.SignIn;
import com.spacester.opendiaryp.authPhone.GenerateOTP;
import com.spacester.opendiaryp.R;

public class IntroLast extends AppCompatActivity {

    Button button4, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_last);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button3.setOnClickListener(v -> {
            Intent intent = new Intent(IntroLast.this, GenerateOTP.class);
            startActivity(intent);
        });
        button4.setOnClickListener(v -> {


            Intent intent = new Intent(IntroLast.this, SignIn.class);
            startActivity(intent);
        });
    }
}
