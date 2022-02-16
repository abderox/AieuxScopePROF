package com.example.testchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;
import com.example.testchat.Adapters.updatecup;


public class choosedrink extends AppCompatActivity {

    TextView tx;
    Button cup;
    Intent intentcup;
    NumberPicker numberpicker;
     ImageView Drop;
    TextView Remaining;
    TextView needs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_choosedrink);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button cup = findViewById(R.id.startbtn);
        TextView needs = findViewById(R.id.showdialog2);



        needs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(choosedrink.this );
// Setting Dialog Title
                alertDialog.setTitle("reminder dialog!");

// Setting Dialog Message
                alertDialog.setMessage("Experts generally recommend that you should consume at least 1.7 liters of fluid per 24 hours." +
                        "Which mean you need 6 takes of water today stay tuned we will remind you!");
// Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.idea);
// Showing Alert Message
                alertDialog.show();
            }
        });




        cup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentcup = new Intent(choosedrink.this, updatecup.class);
                startActivity(intentcup);
            }
        });

    }


}




