package com.example.testchat.Views;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.example.testchat.Adapters.BottomNavigationViewHelper;
import com.example.testchat.Adapters.EmergencyAdapter;
import com.example.testchat.Adapters.customizedAlert;
import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.ContactContract;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.FallDetection;
import com.example.testchat.Services.FallRunningBG;
import com.example.testchat.Services.Shared;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Calendar;

public class FallDetectionActivity extends AppCompatActivity implements customizedAlert.customizedAlertListener, NavigationView.OnNavigationItemSelectedListener {

    ListView lv;
    EditText edit;
    private SQLiteDatabase sql;
    String provider;
    FloatingActionButton addContacts;
    private CoordinatorLayout coordinate;
    SwitchMaterial simpleSwitch;
    ArrayList<EmergencyContact> arrayList = new ArrayList<>();
    EmergencyAdapter arrayAdapter;
    private PendingIntent pendingIntent;
    public static boolean switchvar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contact);

        //permissions sms & gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS;
            PERMISSIONS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            return;
        }

        lv = (ListView) findViewById(R.id.contacts);
        coordinate = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
         simpleSwitch = (SwitchMaterial) findViewById(R.id.switchfall);


        SharedPreferences settings = getSharedPreferences("Fallservice", 0);
        boolean silent = settings.getBoolean("switchkeyfall", false);
        simpleSwitch.setChecked(silent);

        simpleSwitch.setChecked(switchvar || MainActivity.switchVar);
        Boolean switchState = simpleSwitch.isChecked();
        DatabaseHelper dpHelper = new DatabaseHelper(this);
        sql = dpHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM " + ContactContract.TABLE_NAME;
        Cursor mcursor = sql.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationt);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        if(icount<1)
        {
            simpleSwitch.setEnabled(false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigationHome:

                        item.setChecked(true);
                        startActivity(new Intent(FallDetectionActivity.this, MainActivity.class));
                        break;

                    case R.id.navigationMyProfile:

                        DatabaseHelper databaseHelper = new DatabaseHelper(FallDetectionActivity.this);
                        User user = databaseHelper.getUser();
                        if (user != null){
                            Shared.login(FallDetectionActivity.this, user.getEmail(), user.getPassword());
                            if (!Shared.token.isEmpty()) {
                                Intent intent = new Intent(FallDetectionActivity.this, ProfileActivity.class);
                                FallDetectionActivity.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                            }
                        }else{
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(FallDetectionActivity.this);
                            if(account != null){
                                startActivity(new Intent(FallDetectionActivity.this,ProfileActivity.class));
                                overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);

                            }else{

                                startActivity(new Intent(FallDetectionActivity.this,LoginActivity.class));
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
                        startActivity(new Intent(FallDetectionActivity.this, MainActivity.class));
                        break;
                }


                return false;
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (icount > 0) {
                        setyyy();
                        switchvar = true;
                        MainActivity.switchVar = true;
                        Toast.makeText(getApplicationContext(), getString(R.string.safetrack), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FallDetection.class);
                        startService(intent);

//                        Intent bg_running = new Intent(getApplicationContext(), FallRunningBG.class);
//                         pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, bg_running, 0);
//                        long futureInMillis = SystemClock.elapsedRealtime () + 2000 ;
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
//                        assert alarmManager != null;
//                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;


                    } else {

                        simpleSwitch.setChecked(false);
                        Toast.makeText(getApplicationContext(), getString(R.string.tryagainfall), Toast.LENGTH_SHORT).show();
                    }
                    mcursor.close();
                } else {
                    switchvar = false;
                    MainActivity.switchVar = false;
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1233);
                    Intent intent = new Intent(getApplicationContext(), FallDetection.class);
                    stopService(intent);
                }
                SharedPreferences settings = getSharedPreferences("Fallservice", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkeyfall", isChecked);
                editor.commit();
            }
        });


        findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(FallDetectionActivity.this)
                        .setTitle(getString(R.string.title_ifall_alert))
                        .setMessage(getString(R.string.supporting_text_ifall))
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

        if (icount > 0) {
            Cursor cursor = getAllContacts();
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("contact"));
                    arrayList.add(new EmergencyContact(username, number));
                } while (cursor.moveToNext());
            }
            cursor.close();
            arrayAdapter = new EmergencyAdapter(this, R.layout.simplerow, arrayList);
            lv.setAdapter(arrayAdapter);
            simpleSwitch.setEnabled(true);
            Snackbar snackbar = Snackbar.make(coordinate, getString(R.string.addmore), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setAction("OKAY", view -> {

            });
//                snackbar.setAnchorView(floatingButton);
            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(coordinate, getString(R.string.nocontact), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setAction("CANCEL", view -> {
                //
            });
//                snackbar.setAnchorView(floatingButton);
            snackbar.show();

        }
        addContacts = (FloatingActionButton) findViewById(R.id.add);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList.get(i).getNumber(), Toast.LENGTH_LONG).show();
                showPopUpMenu(view, i);
                return false;
            }
        });
    }


    public void openDialog() {
        customizedAlert exampleDialog = new customizedAlert();
        exampleDialog.show(getSupportFragmentManager(), getString(R.string.addcontact));
    }

    @Override
    public void applyTexts(String Name, String Number) {
        //Insert into db
        if (Name.length() != 18 && Number.length() != 10) {
            Toast.makeText(getApplicationContext(), getString(R.string.tryagainn), Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHelper db = new DatabaseHelper(this);
            db.addNewContact(Name, Number);
            Toast.makeText(getApplicationContext(), getString(R.string.contadded), Toast.LENGTH_SHORT).show();
            Cursor cursor = getAllContacts();
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("contact"));
                    arrayList.add(new EmergencyContact(username.toUpperCase(), number));
                    Toast.makeText(this, Number, Toast.LENGTH_SHORT).show();
                } while (cursor.moveToNext());
            }
            cursor.close();
            EmergencyAdapter arrayAdapter = new EmergencyAdapter(this, R.layout.simplerow, arrayList);
            lv.setAdapter(arrayAdapter);
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }


    }

    public void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialog_AppCompat_Light);
                    alertDialogBuilder.setTitle(getString(R.string.deletecontt)).setMessage(getString(R.string.areyousuredelete)).
                            setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                                deleteContactFromBynum(position);
                                Toast.makeText(getApplicationContext(), getString(R.string.addedsucces), Toast.LENGTH_LONG).show();

                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();
                    break;

            }
            return false;
        });
        popupMenu.show();
    }

    private void deleteContactFromBynum(int position) {
        @SuppressLint("StaticFieldLeak")
        class GetSavedContacts extends AsyncTask<Void, Void, ArrayList<EmergencyContact>> {
            @Override
            protected ArrayList<EmergencyContact> doInBackground(Void... voids) {

                DatabaseHelper db = new DatabaseHelper(FallDetectionActivity.this);
                EmergencyContact em = arrayList.get(position);
                db.ct(em.getNumber());
                return arrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<EmergencyContact> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);

            }
        }
        GetSavedContacts savedContacts = new GetSavedContacts();
        savedContacts.execute();
    }

    private void removeAtPosition(int position) {
        arrayList.remove(position);
        arrayAdapter.notifyDataSetChanged();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    public Cursor getAllContacts() {
        return sql.rawQuery("SELECT  * FROM " + ContactContract.TABLE_NAME + ";", null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private void setyyy() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, FallRunningBG.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 10000 * 60 * 30, pendingIntent);

        //Toast.makeText(this, "Notif will show up ", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!simpleSwitch.isChecked())
        {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1238);
        }
    }
}