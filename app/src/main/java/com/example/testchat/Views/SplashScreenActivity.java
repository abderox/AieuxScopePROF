package com.example.testchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;

public class SplashScreenActivity extends AppCompatActivity {
Animation topAnim , bottomAnim ;
TextView slogan , enterprise;
ImageView logo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom);
        logo = findViewById(R.id.logo);
        slogan = findViewById(R.id.textView);
        enterprise = findViewById(R.id.textView2);
        enterprise.setAnimation(bottomAnim);
        logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
//        startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
//        finish();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreenActivity.this, IntroActivity.class);
                startActivity(i);

                finish();
            }
        }, 3000);
    }
    }

