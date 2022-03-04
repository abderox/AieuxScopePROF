package com.example.testchat.Views;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testchat.Adapters.BottomNavigationViewHelper;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.AcceloData;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.Shared;
import com.example.testchat.Services.WhoBG;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AcceloGraph extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private final Handler mHandler2 = new Handler();
    //permissions
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //les variables à stocker dans le ficher csv
    CSVWriter writer;
    private ArrayList<String> xes = new ArrayList<String>();
    private ArrayList<String> yes = new ArrayList<String>();
    private ArrayList<String> zes = new ArrayList<String>();
    private ArrayList<String> acc = new ArrayList<String>();
    ArrayList<String[]> csvData;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue = 0.0;
    private double accelerationPreviousValue = 0.0;
    double changeInAccelleration = 0.0;
    ProgressBar prog_shakeMeter;
    GraphView graph;
    String path = "";
    FloatingActionButton runOnback;
    TextView similar, similar2, similar3, similar4;

    //piechart
    PieChart pieChart, pieChart2;
    //the values stored in the csv file to be compared with other values

    double[] _VALS = new double[4];
    double[] _OTHER_VALS = new double[8];
    double[] COMPARE = new double[5];
    double[] COMPAREAcc = new double[5];
    private int howmany;

    SwitchMaterial simpleSwitch, simpleSwitchSV;
    //plot

    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
            new DataPoint(0, 0),
            new DataPoint(1, 0),
            new DataPoint(2, 0),
            new DataPoint(3, 0),
            new DataPoint(4, 0)
    });
    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    //viewport
    private Viewport viewport;

    private SensorEventListener sensorEventlTstener = new SensorEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
            changeInAccelleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;


//            File sdCard = Environment.getExternalStorageDirectory();
//            String path = sdCard.getAbsolutePath() + "/Download/DataAccelo.csv";
//            File thefile = new File(path);
//            boolean exist = thefile.exists();
//            if (exist) {
//                path = sdCard.getAbsolutePath() + "/Download/DataAcceloNew.csv";
//                   File newCSV = File.createTempFile(sdCard.getAbsolutePath()+"/Download/DataAcceloNew",".csv");
//            }

            if (changeInAccelleration > 6 && changeInAccelleration <= 14) {

                xes.add(valueOf(roundToTwo(x)));
                yes.add(valueOf(roundToTwo(y)));
                zes.add(valueOf(roundToTwo(z)));
                acc.add(valueOf(roundToTwo((float) changeInAccelleration)));
                howmany++;


                // here I am using this to store data in csv file
//                try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }


            prog_shakeMeter.setMax(20);
            prog_shakeMeter.setProgress((int) changeInAccelleration);
            pointsPlotted++;

            if (pointsPlotted > 1000) {
                pointsPlotted = 1;
                series.resetData(new DataPoint[]{new DataPoint(0, 0)});
            }

            series.appendData(new DataPoint(pointsPlotted, changeInAccelleration), true, pointsPlotted);
            viewport.setMinY(0);
            viewport.setMaxY(changeInAccelleration + 5);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelo_graph);
        checkPermissions();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        } else {
            // fail! we don't have an accelerometer!
            Toast.makeText(getBaseContext(), "Can't Find Accelometer on this device", Toast.LENGTH_LONG).show();
        }

        similar = findViewById(R.id.showSimilar);
        similar2 = findViewById(R.id.showSimilar2);
        similar3 = findViewById(R.id.showSimilar3);
        similar4 = findViewById(R.id.showSimilar4);

        if (!restorePreData()) {
            new MaterialAlertDialogBuilder(AcceloGraph.this)
                    .setTitle(getString(R.string.title_who))
                    .setMessage(getString(R.string.howto))
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton(getString(R.string.dontshow), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            savePrefsData();
                        }
                    })


                    .show();
        }


//                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        })


//        try ( writer = new CSVWriter(new FileWriter(path))) {
//            Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            writer = new CSVWriter(new FileWriter(path));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        prog_shakeMeter = findViewById(R.id.progressBar);
        graph = (GraphView) findViewById(R.id.graph);
        simpleSwitch = (SwitchMaterial) findViewById(R.id.switchwho);
        simpleSwitchSV = (SwitchMaterial) findViewById(R.id.switchwhoSV);
        pieChart = findViewById(R.id.piechart);
        pieChart2 = findViewById(R.id.piechart2);
        SharedPreferences settings = getSharedPreferences("WHOservice", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        simpleSwitchSV.setChecked(silent);

        viewport = graph.getViewport();
        viewport.setScalable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);


        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    howmany=0;
                    Intent intent = new Intent(getApplicationContext(), AcceloData.class);
                    stopService(intent);
                    simpleSwitchSV.setEnabled(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.fivesec), Toast.LENGTH_SHORT).show();
                    Runnable mTimer1 = new Runnable() {
                        @Override
                        public void run() {
                            File sdCard = Environment.getExternalStorageDirectory();
                            path = sdCard.getAbsolutePath() + "/Download/DataAccelo.csv";
                            File thefileS = new File(path);
                            csvData = new ArrayList<String[]>();
                            boolean exist = thefileS.exists();

                            if (exist && thefileS.length() > 0) {

                                path = sdCard.getAbsolutePath() + "/Download/DataAcceloNew.csv";
                                try {
                                    writer = new CSVWriter(new FileWriter(path, false));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //            Toast.makeText(this, "that works yeeeeeeeeeeeeeeeeep", Toast.LENGTH_SHORT).show();
                                //                    File newCSV = File.createTempFile(sdCard.getAbsolutePath()+"/Download/DataAcceloNew",".csv");
                            } else {
                                try {
                                    writer = new CSVWriter(new FileWriter(path));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            mSensorManager.registerListener(sensorEventlTstener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

//                            mHandler.postDelayed(this, 5000);
                        }
                    };
                    mHandler.postDelayed(mTimer1, 5000);


                } else {


                    simpleSwitchSV.setEnabled(true);
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
                        if (csvData.size() > 0 && howmany> 15) {
                            writer.writeAll(csvData);
                            Toast.makeText(getBaseContext(), getString(R.string.save), Toast.LENGTH_LONG).show();
                            writer.close();
                            csvData.clear();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.notenough), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mSensorManager.unregisterListener(sensorEventlTstener);
                    pointsPlotted = 1;
                    series.resetData(new DataPoint[]{
                            new DataPoint(0, 0),
                            new DataPoint(1, 0),
                            new DataPoint(2, 0),
                            new DataPoint(3, 0),
                            new DataPoint(4, 0)
                    });
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            }
        });

        //reset files
        findViewById(R.id.reset_file).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                new MaterialAlertDialogBuilder(AcceloGraph.this)
                        .setTitle(getString(R.string.reset))
                        .setMessage(getString(R.string.resetdef))
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                mSensorManager.unregisterListener(sensorEventlTstener);
                                simpleSwitch.setChecked(false);
                                Intent intent = new Intent(getApplicationContext(), AcceloData.class);
                                stopService(intent);
                                simpleSwitchSV.setChecked(false);

                                File sdCard = Environment.getExternalStorageDirectory();
                                File file1 = new File(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv");
                                File file2 = new File(sdCard.getAbsolutePath() + "/Download/DataAcceloSV.csv");
                                File file3 = new File(sdCard.getAbsolutePath() + "/Download/DataAcceloNew.csv");
                                if (file1.exists()) {
                                    if (file1.delete()) {
                                        Toast.makeText(AcceloGraph.this, getString(R.string.resetTodef), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (file2.exists()) {
                                    if (file2.delete()) {
                                        Toast.makeText(AcceloGraph.this, getString(R.string.resetTodef), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (file3.exists()) {
                                    if (file3.delete()) {
                                        Toast.makeText(AcceloGraph.this, getString(R.string.resetTodef), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);

                            }
                        })
                        .show();
            }
        });
        //help button

        findViewById(R.id.optionsAn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AcceloGraph.this)
                        .setTitle(getString(R.string.title_who))
                        .setMessage(getString(R.string.AcceloAnalyse))
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })

//                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        })
                        .show();


            }
        });

        simpleSwitchSV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    mSensorManager.unregisterListener(sensorEventlTstener);
                    setyyy();
                    simpleSwitch.setEnabled(false);

                    Runnable mTimer1 = new Runnable() {
                        @Override
                        public void run() {
                            mSensorManager.unregisterListener(sensorEventlTstener);
                            Intent intent = new Intent(getApplicationContext(), AcceloData.class);
                            startService(intent);
                        }
                    };
                    mHandler2.postDelayed(mTimer1, 5000);

                } else {
                    simpleSwitch.setEnabled(true);
                    Intent intent = new Intent(getApplicationContext(), AcceloData.class);
                    stopService(intent);
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1238);
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                SharedPreferences settings = getSharedPreferences("WHOservice", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
            }
        });

        findViewById(R.id.runOnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AcceloGraph.this)
                        .setTitle(getString(R.string.startRunningSVwho))
                        .setMessage(getString(R.string.Servicehelp_who))
                        .setIcon(R.drawable.ic_baseline_info_24)
                        .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(getString(R.string.start), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mSensorManager.unregisterListener(sensorEventlTstener);
                                setyyy();
                                Intent intent = new Intent(getApplicationContext(), AcceloData.class);
                                startService(intent);
                                simpleSwitchSV.setEnabled(true);
                                simpleSwitchSV.setChecked(true);
                            }
                        })
                        .show();


            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationt);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigationHome:

                        item.setChecked(true);
                        startActivity(new Intent(AcceloGraph.this, MainActivity.class));
                        break;

                    case R.id.navigationMyProfile:

                        DatabaseHelper databaseHelper = new DatabaseHelper(AcceloGraph.this);
                        User user = databaseHelper.getUser();
                        if (user != null){
                            Shared.login(AcceloGraph.this, user.getEmail(), user.getPassword());
                            if (!Shared.token.isEmpty()) {
                                Intent intent = new Intent(AcceloGraph.this, ProfileActivity.class);
                                AcceloGraph.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                            }
                        }else{
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(AcceloGraph.this);
                            if(account != null){
                                startActivity(new Intent(AcceloGraph.this,ProfileActivity.class));
                                overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);

                            }else{

                                startActivity(new Intent(AcceloGraph.this,LoginActivity.class));
                                overridePendingTransition(android.R.anim.slide_out_right,R.anim.slide_in_left );

                            }
                        }
                        return true;

                    case R.id.navigationMenu:
                        item.setChecked(true);
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("openNav", 0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("openvaav", true);
                        editor.apply();
                        startActivity(new Intent(AcceloGraph.this, MainActivity.class));
                        break;
                }


                return false;
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    protected void onResume() {
        super.onResume();


        if (!simpleSwitchSV.isChecked()) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1238);
        }
        function();

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventlTstener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
//        Toast.makeText(getBaseContext(), "Permission is already granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(getBaseContext(), getString(R.string.permgranted), Toast.LENGTH_LONG).show();
            } else {
                // Permission Denied
                Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setyyy() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, WhoBG.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 10000 * 60 * 30, pendingIntent);

        //Toast.makeText(this, "Notif will show up ", Toast.LENGTH_SHORT).show();
    }

    private boolean restorePreData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("getTOwho", 0);
        return preferences.getBoolean("dontshow", false);
    }

    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("getTOwho", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dontshow", true);
        editor.apply();
    }

    private float roundToTwo(float x) {
        return (float) Math.round(x * 100) / 100;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void function() {
        final int[] count = {0};
        int countOther = 0;
        final int[] columns = {1};

        File sdCard = Environment.getExternalStorageDirectory();
        File thefile1 = new File(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv");

        if (!simpleSwitchSV.isChecked() && thefile1.exists() && thefile1.length() > 0) {
            try (CSVReader reader = new CSVReader(new FileReader(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv"))) {
                String[] nextRecord;


//                while ((nextRecord = reader.) != null) {
//
//                    System.out.print("////////////////////////////////////////////////////////////// hello " + nextRecord[0] + "\t");
//
//                    for(int j=0 ; j<nextRecord.length ; j++)
//                    {
//                        System.out.print("//////////////////////////////////////////////////////////////" + nextRecord[j] + "\t");
//                        _VALS[count] += Double.parseDouble(nextRecord[j]);
//                    }
//                    count++;
//                    System.out.println();
//                }




                List<String[]> r = reader.readAll();

                r.forEach(x -> {
                    columns[0]=x.length;
                int a =0;
                            for (String s : x) {
//                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd "+Arrays.toString(x)+" cc "+x[i]);
                                _VALS[count[0]] +=Double.parseDouble(s);
                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd " + s);
                            }
                            count[0]++;
                        }
                );


                Runnable mTimer1 = new Runnable() {
                    @Override
                    public void run() {

                        COMPARE[0] = Math.sqrt(Math.pow(_VALS[0] / columns[0], 2) + Math.pow(_VALS[1] / columns[0], 2) + Math.pow(_VALS[2] / columns[0], 2));
                        COMPAREAcc[0] = _VALS[3]/ columns[0];
                       // Toast.makeText(getApplicationContext(), "Your data => " + _VALS[3]/ columns[0], Toast.LENGTH_LONG).show();
                        function3();
                        function2();
                    }
                };
                mHandler.postDelayed(mTimer1, 500);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void function2() {
        final int[] columns = {1};
        final int[] countNew = {4};

        //newdata

        File sdCard = Environment.getExternalStorageDirectory();
        File mainfile = new File(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv");
        File thefileNew = new File(sdCard.getAbsolutePath() + "/Download/DataAcceloNew.csv");
        if (!simpleSwitch.isChecked() && mainfile.exists() && thefileNew.exists() && mainfile.length() > 0 && thefileNew.length() > 0) {
            try (CSVReader reader = new CSVReader(new FileReader(sdCard.getAbsolutePath() + "/Download/DataAcceloNew.csv"))) {
                String[] nextRecord;



//                while ((nextRecord = reader.readNext()) != null) {
//                    for (String cell : nextRecord) {
//                        System.out.print("//////////////////////////////////////////////////////////////" + cell + "\t");
//                        _OTHER_VALS[countNew] += Double.parseDouble(cell);
//
//                    }
//                    countNew++;
//                    System.out.println();
//                }

                List<String[]> r = reader.readAll();

                r.forEach(x -> {
                    columns[0] =x.length;

                            for (String s : x) {
//                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd "+Arrays.toString(x)+" cc "+x[i]);
                                _OTHER_VALS[countNew[0]] +=Double.parseDouble(s);
                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd " + s);
                            }
                    countNew[0]++;
                        }
                );

                Runnable mTimer1 = new Runnable() {
                    @Override
                    public void run() {
                        COMPARE[3] = Math.sqrt(Math.pow(_OTHER_VALS[4] / columns[0], 2) + Math.pow(_OTHER_VALS[5] / columns[0], 2) + Math.pow(_OTHER_VALS[6] / columns[0], 2));
                        COMPAREAcc[2] =  _OTHER_VALS[7] / columns[0];
                        //Toast.makeText(getApplicationContext(), "Your data => " + _VALS[2], Toast.LENGTH_LONG).show();
                      //  Toast.makeText(getApplicationContext(), "here : => " + COMPAREAcc[2], Toast.LENGTH_SHORT).show();
                        if ((COMPAREAcc[2] / COMPAREAcc[0]) < 1) {
                            COMPAREAcc[4] = (COMPAREAcc[2] / COMPAREAcc[0]) * 100;
                        } else {
                            COMPAREAcc[4] = (COMPAREAcc[0] / COMPAREAcc[2]) * 100;
                        }

                        if ((COMPARE[3] / COMPARE[0]) < 1) {
                            COMPARE[4] = (COMPARE[3] / COMPARE[0]) * 100;
                        } else {
                            COMPARE[4] = (COMPARE[0] / COMPARE[3]) * 100;
                        }

                        if (COMPAREAcc[2] > 6 && COMPAREAcc[2]<= 14) {
                            similar2.setText("" + (int) COMPARE[4] + "% SIMILAR");
                            similar4.setText("Avg accelo " + (int) COMPAREAcc[4] + "%");
                            // Toast.makeText(this, "here : => " + COMPARE[2], Toast.LENGTH_SHORT).show();
                            pieChart2.addPieSlice(
                                    new PieModel(
                                            "% Similarity",
                                            Integer.parseInt(valueOf((int) COMPARE[4])),
                                            Color.parseColor("#29B6F6")));
                            pieChart2.addPieSlice(
                                    new PieModel(
                                            "none",
                                            Integer.parseInt(valueOf(100 - (int) COMPARE[4])),
                                            Color.parseColor("#EF5350")));
                            pieChart2.startAnimation();
                        } else {
                            similar2.setText("Data not valid");
                            similar4.setText("Data not valid");
                            // Toast.makeText(this, "here : => " + COMPARE[2], Toast.LENGTH_SHORT).show();
                            pieChart2.addPieSlice(
                                    new PieModel(
                                            "% Similarity",
                                            0,
                                            Color.parseColor("#29B6F6")));
                            pieChart2.addPieSlice(
                                    new PieModel(
                                            "none",
                                            100,
                                            Color.parseColor("#EF5350")));
                            pieChart2.startAnimation();
                        }
                    }
                };
                mHandler.postDelayed(mTimer1, 500);


                //  Toast.makeText(this, _OTHER_VALS[0] +" / "+" / "+ _OTHER_VALS[1]+ " / "+_OTHER_VALS[2] + " / other " + COMPARE[1], Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }




        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void function3() {
        File sdCard = Environment.getExternalStorageDirectory();
        final int[] countOther = {0};
        final int[] columns = {1};

        File thefile = new File(sdCard.getAbsolutePath() + "/Download/DataAcceloSV.csv");
        File mainfile = new File(sdCard.getAbsolutePath() + "/Download/DataAccelo.csv");

        if (!simpleSwitchSV.isChecked() && mainfile.exists() && thefile.exists() && mainfile.length() > 0 && thefile.length() > 0) {
            try (CSVReader reader = new CSVReader(new FileReader(sdCard.getAbsolutePath() + "/Download/DataAcceloSV.csv"))) {
                String[] nextRecord;



//                while ((nextRecord = reader.readNext()) != null) {
//                    for (String cell : nextRecord) {
//                        System.out.print("//////////////////////////////////////////////////////////////" + cell + "\t");
//                        _OTHER_VALS[countOther] += Double.parseDouble(cell);
//
//                    }
//                    countOther++;
//                    System.out.println();
//                }

                List<String[]> r = reader.readAll();

                r.forEach(x -> {
                    columns[0] = x.length;

                            for (String s : x) {
//                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd "+Arrays.toString(x)+" cc "+x[i]);
                                _OTHER_VALS[countOther[0]] +=Double.parseDouble(s);
                                System.out.println("qhdkjhqkjdhqkjdhkjqhkd " + s);
                            }
                            countOther[0]++;
                        });


                Runnable mTimer1 = new Runnable() {
                    @Override
                    public void run() {
                        COMPARE[1] = Math.sqrt(Math.pow(_OTHER_VALS[0] / columns[0], 2) + Math.pow(_OTHER_VALS[1] / columns[0], 2) + Math.pow(_OTHER_VALS[2] / columns[0], 2));
                        COMPAREAcc[1] = _OTHER_VALS[3] / columns[0];

                                  Toast.makeText(getApplicationContext(), "here : => " + COMPAREAcc[1], Toast.LENGTH_SHORT).show();

                        if ((COMPAREAcc[1] / COMPAREAcc[0]) < 1) {
                            COMPAREAcc[3] = (COMPAREAcc[1] / COMPAREAcc[0]) * 100;
                        } else {
                            COMPAREAcc[3] = (COMPAREAcc[0] / COMPAREAcc[1]) * 100;
                        }

                        if ((COMPARE[1] / COMPARE[0]) < 1) {
                            COMPARE[2] = (COMPARE[1] / COMPARE[0]) * 100;
                        } else {
                            COMPARE[2] = (COMPARE[0] / COMPARE[1]) * 100;
                        }

                        if (COMPAREAcc[1] > 6 && COMPAREAcc[1] <= 14) {
                            similar.setText("" + (int) COMPARE[2] + "% SIMILAR");
                            similar3.setText("Avg accelo " + (int) COMPAREAcc[3] + "%");
//            Toast.makeText(this, "here : => " + COMPARE[2], Toast.LENGTH_SHORT).show();
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "% Similarity",
                                            Integer.parseInt(valueOf((int) COMPARE[2])),
                                            Color.parseColor("#FFA726")));
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "none",
                                            Integer.parseInt(valueOf(100 - (int) COMPARE[2])),
                                            Color.parseColor("#66BB6A")));
                            pieChart.startAnimation();
                        } else {
                            similar.setText("Data not valid");
                            similar3.setText("Data not valid");
//            Toast.makeText(this, "here : => " + COMPARE[2], Toast.LENGTH_SHORT).show();
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "% Similarity",
                                            0,
                                            Color.parseColor("#FFA726")));
                            pieChart.addPieSlice(
                                    new PieModel(
                                            "none",
                                            100,
                                            Color.parseColor("#66BB6A")));
                            pieChart.startAnimation();
                        }

                    }

                };
                mHandler.postDelayed(mTimer1, 500);

                // Toast.makeText(this, _OTHER_VALS[0] +" / "+" / "+ _OTHER_VALS[1]+ " / "+_OTHER_VALS[2] + " / other " + COMPARE[1], Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


}