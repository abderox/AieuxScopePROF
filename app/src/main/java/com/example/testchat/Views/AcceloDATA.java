package com.example.testchat.Views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testchat.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AcceloDATA extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;
    private ArrayList<String> xes = new ArrayList<String>();
    private ArrayList<String> yes = new ArrayList<String>();
    private ArrayList<String> zes = new ArrayList<String>();

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelo_data);
        initializeViews();
        checkPermissions();
        //  save = (Button) findViewById(R.id.save_btn);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;

        } else {
            // fail! we don't have an accelerometer!
            Toast.makeText(getBaseContext(), "Can't Find Data ", Toast.LENGTH_LONG).show();
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }
    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }

        String entry = currentX.getText().toString() + "," + currentY.getText().toString() + "," + currentZ.getText().toString() + ",";
        xes.add(currentX.getText().toString());
        yes.add(currentY.getText().toString());
        zes.add(currentZ.getText().toString());

        //convert arraylist to string array

        String xers[] = xes.toArray(new String[xes.size()]);
        String yers[] = yes.toArray(new String[yes.size()]);
        String zers[] = zes.toArray(new String[zes.size()]);

        ArrayList<String[]> csvData = new ArrayList<String[]>();
        csvData.add(xers);
        csvData.add(yers);
        csvData.add(zers);
        File sdCard = Environment.getExternalStorageDirectory();
        // default all fields are enclosed in double quotes
        // default separator is a comma
        try (CSVWriter writer = new CSVWriter(new FileWriter(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv"))) {
            writer.writeAll(csvData);
            Toast.makeText(getBaseContext(), getString(R.string.save), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

//            File sdCard = Environment.getExternalStorageDirectory();
//            File dir = new File(sdCard.getAbsolutePath() + "/Download");
//
//            Boolean dirsMade = dir.mkdir();
//            //System.out.println(dirsMade);
//            Log.v("Accel", dirsMade.toString());
//
//            File file = new File(dir, "AcceloDATA.csv");
//            FileOutputStream f = new FileOutputStream(file, true);
//
//            try {
//                f.write(entry.getBytes());
//                f.flush();
//                f.close();
//                Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


    }
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    @SuppressLint("SetTextI18n")
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    @SuppressLint("SetTextI18n")
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }

        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Toast.makeText(getBaseContext(), getString(R.string.permissionSen), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                // Permission Denied
                Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}