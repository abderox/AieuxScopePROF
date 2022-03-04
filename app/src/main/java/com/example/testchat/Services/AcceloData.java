package com.example.testchat.Services;

import static java.lang.String.valueOf;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AcceloData extends Service {

    private final Handler mHandler = new Handler();
    //permissions
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //les variables à stocker dans le ficher csv
    CSVWriter writer;
    private ArrayList<String> xes = new ArrayList<String>();
    private ArrayList<String> yes = new ArrayList<String>();
    private ArrayList<String> zes = new ArrayList<String>();
    private ArrayList<String> acc = new ArrayList<String>();
    private int howmany;

    ArrayList<String[]> csvData;

    private SensorManager mSensorManager;

    private double accelerationCurrentValue = 0.0;
    private double accelerationPreviousValue = 0.0;
    double changeInAccelleration = 0.0;
    String path = "";
    private Sensor mAccelerometer;

    public AcceloData() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        File sdCard = Environment.getExternalStorageDirectory();
        path = sdCard.getAbsolutePath() + "/Download/DataAcceloSV.csv";
        csvData = new ArrayList<String[]>();

        try {
            writer = new CSVWriter(new FileWriter(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "starting service in background !", Toast.LENGTH_SHORT).show();
        onTaskRemoved(intent);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        } else {
            // fail! we don't have an accelerometer!
            Toast.makeText(getBaseContext(), "Can't Find Accelometer on this device", Toast.LENGTH_LONG).show();
        }

        Runnable mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSensorManager.unregisterListener(sensorEventlTstener);
//                mHandler.postDelayed(this, 1000);
                SharedPreferences settings = getSharedPreferences("WHOservice", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", false);
                editor.commit();

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1238);
                howmany=0;
                AcceloData.this.stopSelf();
            }
        };
        mHandler.postDelayed(mTimer1, 1000*60*10);

        mSensorManager.registerListener(sensorEventlTstener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        SharedPreferences settings = getSharedPreferences("WHOservice", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("switchkey", false);
        editor.commit();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1238);

        mSensorManager.unregisterListener(sensorEventlTstener);
        Toast.makeText(this, "The Who? service is stopped !", Toast.LENGTH_SHORT).show();
        //check for minimum movement if he is walking
        String xers[];
        String yers[];
        String zers[];
        String accs[];
        //
        xers = xes.toArray(new String[xes.size()]);
        yers = yes.toArray(new String[yes.size()]);
        zers = zes.toArray(new String[zes.size()]);
        accs = acc.toArray(new String[acc.size()]);

        csvData.add(xers);
        csvData.add(yers);
        csvData.add(zers);
        csvData.add(accs);

        try {
            if (csvData.size() > 0 && howmany > 15) {
                writer.writeAll(csvData);
                Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
                writer.close();
                csvData.clear();
                howmany =0;
            }
            else {
                Toast.makeText(getApplicationContext(), "Not enough data", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SensorEventListener sensorEventlTstener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {


            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
            changeInAccelleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            if (changeInAccelleration > 6 && changeInAccelleration <= 14) {

                xes.add(valueOf(roundToTwo(x)));
                yes.add(valueOf(roundToTwo(y)));
                zes.add(valueOf(roundToTwo(z)));
                acc.add(valueOf(roundToTwo((float)changeInAccelleration)));
                howmany++;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private  float roundToTwo(float x)
    {
        return (float) Math.round(x*100)/100;
    }

}