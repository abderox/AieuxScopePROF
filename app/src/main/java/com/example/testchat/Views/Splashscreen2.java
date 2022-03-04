package com.example.testchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;
import com.example.testchat.Views.choosedrink;

public class Splashscreen2 extends AppCompatActivity {

    private Intent myintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreeen2);
        myintent = new Intent(this, choosedrink.class);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView textView=findViewById(R.id.textView);
        TextView textView2=findViewById(R.id.textView2);
        textView.animate().translationX(1000).setDuration(1000).setStartDelay(1500);
        textView2.animate().translationY(2000).setDuration(1000).setStartDelay(2000);
        splashScreen(3000);
    }

    private void splashScreen(final int x) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(x);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(myintent);
                finish();
            }
        }).start();
    }

}
