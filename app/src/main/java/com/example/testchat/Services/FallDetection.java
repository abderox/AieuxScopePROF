package com.example.testchat.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FallDetection extends Service implements SensorEventListener {

    private SQLiteDatabase sql;

    Handler handler = new Handler(Looper.getMainLooper());
    private Handler mPeriodicEventHandler = new Handler();
    private final int PERIODIC_EVENT_TIMEOUT = 3000;

    private Timer fuseTimer = new Timer();
    private int sendCount = 0;
    private char sentRecently = 'N';
    //Three Sensor Fusion - Variables:
    // angular speeds from gyro
    private float[] gyro = new float[3];
    private float degreeFloat;
    private float degreeFloat2;
    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];

    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];

    // magnetic field vector
    private float[] magnet = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];

    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];

    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];

    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];

    public static final float EPSILON = 0.000000001f;

    public static final int TIME_CONSTANT = 30;
    public static final float FILTER_COEFFICIENT = 0.98f;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private boolean initState = true;

    //Sensor Variables:
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senProximity;
    private SensorEvent mSensorEvent;

    //GPS
    double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    //phone
    String phoneNum="";
    String prevNumber="";
    private Toast mToastToShow;

    private Float x, y, z;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private Runnable doPeriodicTask = new Runnable() {
        public void run() {

            sentRecently = 'N';
//            mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d("Initialing Service", "OnCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPeriodicEventHandler.removeCallbacks(doPeriodicTask);
        senSensorManager.unregisterListener(this);
        sendCount = 0;
        Toast.makeText(this, "Stopped Tracking", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else {
            locationManager.removeUpdates(locationListener);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
    //start

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(FallDetection.this.getApplicationContext(), "No GPS Permission!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        latitude = locationManager.getLastKnownLocation(locationProvider).getLatitude();
        longitude = locationManager.getLastKnownLocation(locationProvider).getLongitude();

        onTaskRemoved(intent);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        initListeners();
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, TIME_CONSTANT);

        return START_STICKY;
    }

    public void initListeners() {
        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array
                // then calculate new orientation
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3);


                calculateAccMagOrientation();
                break;

            case Sensor.TYPE_GYROSCOPE:
                // process gyro data
                gyroFunction(sensorEvent);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // copy new magnetometer data into magnet array
                System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //      Log.d("MY_APP", sensor.toString() + "-" + i);
    }

    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    public void gyroFunction(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;

        // initialisation of the gyroscope based rotation matrix
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            System.arraycopy(event.values, 0, gyro, 0, 3);
            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            fusedOrientation[0] =
                    FILTER_COEFFICIENT * gyroOrientation[0]
                            + oneMinusCoeff * accMagOrientation[0];
//            Log.d("X:", ""+fusedOrientation[0]);

            fusedOrientation[1] =
                    FILTER_COEFFICIENT * gyroOrientation[1]
                            + oneMinusCoeff * accMagOrientation[1];
//            Log.d("Y:", ""+fusedOrientation[1]);
            fusedOrientation[2] =
                    FILTER_COEFFICIENT * gyroOrientation[2]
                            + oneMinusCoeff * accMagOrientation[2];
//            Log.d("Z:", ""+fusedOrientation[2]);

            //**********Sensing Danger**********
            double SMV = Math.sqrt(accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]);
//                Log.d("SMV:", ""+SMV);
            if (SMV > 25) {
                if (sentRecently == 'N') {
                    Log.d("Accelerometer vector:", "" + SMV);
                    degreeFloat = (float) (fusedOrientation[1] * 180 / Math.PI);
                    degreeFloat2 = (float) (fusedOrientation[2] * 180 / Math.PI);
                    if (degreeFloat < 0)
                        degreeFloat = degreeFloat * -1;
                    if (degreeFloat2 < 0)
                        degreeFloat2 = degreeFloat2 * -1;
//                    Log.d("Degrees:", "" + degreeFloat);
                    if (degreeFloat > 30 || degreeFloat2 > 30) {
                        Log.d("Degree1:", "" + degreeFloat);
                        Log.d("Degree2:", "" + degreeFloat2);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FallDetection.this.getApplicationContext(), "You Fall !", Toast.LENGTH_SHORT).show();
//                                ProgDialog progDialog = new ProgDialog(FallDetection.this);
//                                progDialog.execute("10");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FallDetection.this.getApplicationContext(), "Sending message!", Toast.LENGTH_SHORT).show();
                                        Cursor cursor = getAllContacts();
                                        ArrayList<Long> itemIds = new ArrayList<>();
                                        ArrayList<EmergencyContact> arrayList = new ArrayList<>();
                                        if (cursor.moveToFirst()){
                                            do{
                                                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("name"));
                                                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("contact"));

                                                arrayList.add(new EmergencyContact(username,number));
                                                long itemId = cursor.getLong(
                                                        cursor.getColumnIndexOrThrow(ContactContract.COLUMN_CONTACT));
                                                itemIds.add(itemId);
                                            }while(cursor.moveToNext());}
                                            cursor.close();

                                                    int i = 0;
                                                    while(i<arrayList.size() && i<3)
                                                    {
                                                        phoneNum = arrayList.get(i).getNumber();
                                                        String name = arrayList.get(i).getName();
                                                        prevNumber = "";
                                                        SmsManager smsManager = SmsManager.getDefault();
                                                        if (phoneNum != prevNumber && phoneNum != null) {
                                                            String msg = "Preparing message to send to "+name+ ":" + phoneNum;
                                                            showToast(msg);
                                                            String textMsg = " Danger here , I Fall :  => " + "http://www.google.com/maps/place/" + String.valueOf(latitude) + "," + String.valueOf(longitude) ;
                                                            smsManager.sendTextMessage(phoneNum, null, textMsg, null, null);
                                                           // Toast.makeText(FallDetection.this,"Sending message to "+name+ ":" + phoneNum, Toast.LENGTH_LONG).show();
                                                            prevNumber = phoneNum;
                                                            sendCount++;
                                                        }
                                                        i++;
                                                    }

                                    }
                                }, 30000);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
                                MediaPlayer mPlayer2;
                                mPlayer2 = MediaPlayer.create(getApplicationContext(), R.raw.notification);
                                mPlayer2.start();
                                v.vibrate(pattern,-1);

                            }
                        });

                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FallDetection.this.getApplicationContext(), "Sudden Movement! But looks safe", Toast.LENGTH_SHORT).show();
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(400);
                            }
                        });
                        sendCount++;
                    }
                    sentRecently='Y';
                    Log.d("Delay", "start");
                    mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);
                }
            }
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
        }
        public Cursor getAllContacts(){
            DatabaseHelper db = new DatabaseHelper(FallDetection.this);
            sql = db.getReadableDatabase();
            return sql.rawQuery("SELECT  * FROM " + ContactContract.TABLE_NAME +";",null);
        }
        public void showToast(String msg) {

            int toastDurationInMilliSeconds = 10000;
            mToastToShow = Toast.makeText(FallDetection.this, msg, Toast.LENGTH_LONG);

            CountDownTimer toastCountDown;
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 ) {
                public void onTick(long millisUntilFinished) {
                    mToastToShow.show();
                }
                public void onFinish() {
                    mToastToShow.cancel();
                }
            };

            mToastToShow.show();
            toastCountDown.start();
        }
        public boolean isScreenOn(Context context) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        }

    }
}