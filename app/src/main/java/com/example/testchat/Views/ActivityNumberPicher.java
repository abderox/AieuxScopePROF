package com.example.testchat.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;
import com.example.testchat.Services.DatabaseHelper;

public class ActivityNumberPicher extends AppCompatActivity {


    Button register;
    DatabaseHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_picher);
        NumberPicker numberPicker = findViewById(R.id.numberPicker);
        TextView txtPoidsV = findViewById(R.id.txtPoidsV);


        final String[] values = {"55kg", "58kg", "60kg", "65kg", "70kg", "75kg", "80kg", "85kg"};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setDisplayedValues(values);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //String text = "Changed from " + values[oldVal] + " to " + values[newVal];
               // Toast.makeText(ActivityNumberPicher.this, text, Toast.LENGTH_SHORT).show();
                txtPoidsV.setText(values[newVal] );


            }

        });

        NumberPicker numberPicker2 = findViewById(R.id.numberPicker2);
        TextView txtPriseV = findViewById(R.id.txtPrisesV);


        final String[] prise = {"6 cups", "7 cups", "8 cups", "9 cups", "10 cups"};
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(prise.length - 1);
        numberPicker2.setDisplayedValues(prise);
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txtPriseV.setText(prise[newVal] );


            }

        });

        NumberPicker numberPicker3 = findViewById(R.id.numberPicker3);
        TextView txPeriodeV = findViewById(R.id.txtPeriodeV);


        final String[] periodes = {"Ete","Hivers"};
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(periodes.length - 1);
        numberPicker3.setDisplayedValues(periodes);
        numberPicker3.setWrapSelectorWheel(true);
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txPeriodeV.setText(periodes[newVal] );


            }

      });

        TextView txPeriode = findViewById(R.id.txtPeriodeV);
        TextView txtPoids = findViewById(R.id.txtPoidsV);
        TextView txtPrise = findViewById(R.id.txtPrisesV);
        register = findViewById(R.id.register);

        db = new DatabaseHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poidsTXT = txtPoids.getText().toString();
                String priseTXT = txtPrise.getText().toString();
                String periodeTXT = txPeriode.getText().toString();

                Boolean checkinsertdata = db.insertuserdata(poidsTXT, priseTXT, periodeTXT);

                if(checkinsertdata == true){
                    Toast.makeText(ActivityNumberPicher.this, "New entry inserted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ActivityNumberPicher.this, "New entry not inserted", Toast.LENGTH_SHORT).show();
                }

            }
        });

        }
}