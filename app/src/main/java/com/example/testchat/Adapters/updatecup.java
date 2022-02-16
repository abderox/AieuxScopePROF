package com.example.testchat.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.R;
import com.example.testchat.Services.AlarmReceiver;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Views.ActivityNumberPicher;
import com.example.testchat.Views.prise;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class updatecup extends AppCompatActivity {


    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SQLiteDatabase rqt;
    Switch witch;


    Intent intentprise;
    Intent intentnumber;
    Intent intentstatistic;
    FloatingActionButton myFab1;
    FloatingActionButton myFab2;
    FloatingActionButton myFab;
    Boolean isTrue = true;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_updatecup);
        Button set = findViewById(R.id.setAlarmBtn);
        Switch witch = (Switch) findViewById(R.id.my);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton myFab1 = (FloatingActionButton) findViewById(R.id.fab1);
        FloatingActionButton myFab2 = (FloatingActionButton) findViewById(R.id.fab2);
        myFab.setColorFilter(Color.WHITE);
        myFab1.setColorFilter(Color.WHITE);
        myFab2.setColorFilter(Color.WHITE);


        final Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        final Animation rotateBack = AnimationUtils.loadAnimation(this, R.anim.rotate_back);
        final Animation open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        final Animation close = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        witch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    PackageManager pm  = updatecup.this.getPackageManager();
                    ComponentName componentName = new ComponentName(updatecup.this,AlarmReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplicationContext(), "you have cancelled your water Intaker", Toast.LENGTH_LONG).show();
                }
                else{
                    PackageManager pm  = updatecup.this.getPackageManager();
                    ComponentName componentName = new ComponentName(updatecup.this,AlarmReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplicationContext(), "you can use your water reminder now", Toast.LENGTH_LONG).show();
                }

            }

        });

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrue){
                    myFab.startAnimation(rotate);
                    myFab1.startAnimation(open);
                    myFab2.startAnimation(open);
                    myFab1.setVisibility(View.VISIBLE);
                    myFab2.setVisibility(View.VISIBLE);
                    myFab1.setClickable(true);
                    myFab2.setClickable(true);
                    isTrue= false;

                }
                else {
                    myFab.startAnimation(rotateBack);
                    myFab1.startAnimation(close);
                    myFab2.startAnimation(close);
                    myFab1.setVisibility(View.INVISIBLE);
                    myFab2.setVisibility(View.INVISIBLE);
                    myFab1.setClickable(false);
                    myFab2.setClickable(false);
                    isTrue= true;

                }

            }
        });
        myFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentnumber = new Intent(updatecup.this,ActivityNumberPicher.class);
                startActivity(intentnumber);

            }
        });


        Button cancel = findViewById(R.id.cancelAlarmBtn);
        Button select = findViewById(R.id.selectTimeBtn);
        createNotificationChannel();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();


            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAlarm();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelAlarm();

            }
        });
        myFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentstatistic = new Intent(updatecup.this,StatisticActivity.class);
                startActivity(intentstatistic);

            }
        });



    }


    private void cancelAlarm() {

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        if (alarmManager == null){

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {

        DatabaseHelper dpHelper = new DatabaseHelper(this);
        rqt = dpHelper.getWritableDatabase();
        Cursor cur = rqt.rawQuery("SELECT periode FROM UserInfo ",null);
        cur.moveToFirst();
        @SuppressLint("Range") String period = cur.getString(cur.getColumnIndex("periode"));
        System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm "+period);
        int p = Integer.parseInt(period);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
        // AlarmManager.INTERVAL_DAY,pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * p, pendingIntent);

        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();



    }

    @SuppressLint("Range")
    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(),"android");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView time = findViewById(R.id.selectedTime);
                if (picker.getHour() > 12){

                    time.setText(
                            String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                    );

                }else {

                    time.setText(picker.getHour()+" : " + picker.getMinute() + " AM");

                }

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

            }
        });


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "androidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("android",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }



}