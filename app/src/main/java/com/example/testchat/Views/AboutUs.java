package com.example.testchat.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.testchat.R;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        findViewById(R.id.contactEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + getString(R.string.aieuxscopeccontact_gmail_com)));
                    intent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.aieuxscopeCust));
                    intent.putExtra(Intent.EXTRA_TEXT, "Hello! AieuxScope");
                    startActivity(intent);
                } catch(Exception e) {
                    Toast.makeText(AboutUs.this, "Sorry...You don't have any mail app", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}