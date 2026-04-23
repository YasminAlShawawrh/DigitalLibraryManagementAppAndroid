package com.example.a12122028_1220848_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a12122028_1220848_courseproject.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        logo.startAnimation(fade);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            finish();
        }, 2500);
    }
}