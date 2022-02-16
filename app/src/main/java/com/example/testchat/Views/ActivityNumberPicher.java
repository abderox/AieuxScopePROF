package com.example.testchat.Views;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;
import com.example.testchat.Services.DatabaseHelper;

public class ActivityNumberPicher extends AppCompatActivity {


    Button register;
    DatabaseHelper db;
    private TextView temperaturelabel;
    private SensorManager mSensorManager;
    private Sensor mTemperature;
    TextView poids ;
    TextView hauteur;
    TextView myResult;
    Button btn ;
    private final static String NOT_SUPPORTED_MESSAGE = "Sorry, sensor not available for this device.";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_picher);
        btn = findViewById(R.id.bmibtn);
        NumberPicker numberPicker = findViewById(R.id.numberPicker);
        TextView txtPoidsV = findViewById(R.id.txtPoidsV);
        final String[] values = {"55", "58", "60", "65", "70", "75", "80", "85"};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setDisplayedValues(values);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //String text = "Changed from " + values[oldVal] + " to " + values[newVal];
                // Toast.makeText(ActivityNumberPicher.this, text, Toast.LENGTH_SHORT).show();
                txtPoidsV.setText(values[newVal]);
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
                txtPriseV.setText(prise[newVal]);
            }

        });
        NumberPicker numberPicker4 = findViewById(R.id.numberPicker4);
        TextView txHauteurV = findViewById(R.id.txtHauteurV);
        final String[] hauteurs = {"1.75", "1.70", "1.65", "1.60", "1.55", "1.50", "1.80", "1.85", "1.90"};
        numberPicker4.setMinValue(0);
        numberPicker4.setMaxValue(hauteurs.length - 1);
        numberPicker4.setDisplayedValues(hauteurs);
        numberPicker4.setWrapSelectorWheel(true);
        numberPicker4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txHauteurV.setText(hauteurs[newVal]);
            }

        });
        NumberPicker numberPicker3 = findViewById(R.id.numberPicker3);
        TextView txPeriodeV = findViewById(R.id.txtPeriodeV);
        final String[] periodes = {"30", "45", "60", "75", "90"};
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(periodes.length - 1);
        numberPicker3.setDisplayedValues(periodes);
        numberPicker3.setWrapSelectorWheel(true);
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txPeriodeV.setText(periodes[newVal]);
            }

        });

        TextView txPeriode = findViewById(R.id.txtPeriodeV);
        TextView txtPoids = findViewById(R.id.txtPoidsV);
        TextView txtPrise = findViewById(R.id.txtPrisesV);
        TextView txthauteur = findViewById(R.id.txtHauteurV);
        register = findViewById(R.id.register);

        db = new DatabaseHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poidsTXT = txtPoids.getText().toString();
                String priseTXT = txtPrise.getText().toString();
                String periodeTXT = txPeriode.getText().toString();
                String hauteurTXT = txthauteur.getText().toString();
                Boolean checkinsertdata = db.insertuserdata(poidsTXT, priseTXT, periodeTXT, hauteurTXT);
                if (checkinsertdata == true) {
                    Toast.makeText(ActivityNumberPicher.this, "New entry inserted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityNumberPicher.this, "New entry not inserted", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn = findViewById(R.id.bmibtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculateBMI();
            }

            private void CalculateBMI() {
                double weight = 0;
                double height = 0;
                double bmi = 0;
                String msg = "";

                poids = findViewById(R.id.txtPoidsV);
                hauteur = findViewById(R.id.txtHauteurV);

                //calculateButton = (Button) findViewById(R.id.calculate_bim_BTN);

                // yourBIM = (TextView) findViewById(R.id.your_bim);

                weight = Double.parseDouble(poids.getText().toString());
                height = Double.parseDouble(hauteur.getText().toString());

                bmi = height * height;
                bmi = weight / bmi;

                //yourBIM.setText(String.valueOf(bmi));

                if (bmi < 18.5) {
                    msg = "UnderWeight";
                } else if (bmi > 18.5 && bmi < 24.9) {
                    msg = "Normal Weight";
                } else if (bmi > 25 && bmi < 29.9) {
                    msg = "OverWeight";
                } else if (bmi > 30) {
                    msg = "Obesity";
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityNumberPicher.this);
// Setting Dialog Title
                alertDialog.setTitle("Your result");

// Setting Dialog Message
                alertDialog.setMessage(msg);
// Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.idea);
// Showing Alert Message
                alertDialog.show();


            }
        });

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        float ambient_temperature = event.values[0];
//        temperaturelabel.setText("Ambient Temperature:\n " + String.valueOf(ambient_temperature) + getResources().getString(R.string.celsius));
//    }
//
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Do something here if sensor accuracy changes.
//    }

    }
}