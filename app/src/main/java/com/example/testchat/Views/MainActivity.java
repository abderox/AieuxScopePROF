package com.example.testchat.Views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.testchat.Adapters.BottomNavigationBehavior;
import com.example.testchat.Adapters.DarkModePrefManager;
import com.example.testchat.Models.Person;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.ContactContract;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.FallDetection;
import com.example.testchat.Services.FallRunningBG;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.example.testchat.Services.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private static final int MODE_DARK = 0;
    private static final int MODE_LIGHT = 1;
    public static boolean switchVar = false;
    boolean isChecked;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);
    //check on db that there is at least one contact to swith the feature on
    private SQLiteDatabase sql;
    SwitchMaterial ss;
    Cursor mcursor;
    int icount;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigationMyProfile:
                    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                    User user = databaseHelper.getUser();
                    if (user != null){
                        Shared.login(MainActivity.this, user.getEmail(), user.getPassword());
                        if (!Shared.token.isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            MainActivity.this.startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                        }
                    }else{
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                        if(account != null){
                            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                            overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);

                        }else{

                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            overridePendingTransition(android.R.anim.slide_out_right,R.anim.slide_in_left );

                        }
                    }
                    return true;


                case R.id.navigationHome:
                    return true;

                case R.id.navigationMenu:
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDarkMode(getWindow());
        setContentView(R.layout.activity_main);
        DatabaseHelper dpHelper = new DatabaseHelper(this);

        sql = dpHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM " + ContactContract.TABLE_NAME;
        mcursor = sql.rawQuery(count, null);
        mcursor.moveToFirst();
        icount = mcursor.getInt(0);
        mcursor.close();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.ifall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, FallDetectionActivity.class);
                startActivity(intent);


            }
        });
        findViewById(R.id.myDoctor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setTitle(getString(R.string.wait));
                progressDialog.show();
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                User user = databaseHelper.getUser();
                if (user != null){
                    Shared.login(MainActivity.this, user.getEmail(), user.getPassword());
                    if (!Shared.token.isEmpty()){
                        APIServices service = RetrofitAPI.getRetrofitInstance().create(APIServices.class);
                        Call<Person> call = service.getDataPerson("Bearer " + Shared.token);
                        call.enqueue(new Callback<Person>() {
                            @Override
                            public void onResponse(Call<Person> call, Response<Person> response) {
                                try {
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String userId = user.getEmail().replace('.','_');
                                            if (!snapshot.child("users").hasChild(userId)){
                                                databaseReference.child("users").child(userId).child("email").setValue(response.body().getEmail());
                                                databaseReference.child("users").child(userId).child("name").setValue(response.body().getFullName());
                                                databaseReference.child("users").child(userId).child("profilePicUrl").setValue(RetrofitAPI.BASE_URL +response.body().getImagePath());
                                            }
                                            progressDialog.dismiss();
                                            Intent intent= new Intent(MainActivity.this, ChatList.class);
                                            intent.putExtra("email",response.body().getEmail());
                                            intent.putExtra("name",response.body().getFullName());
                                            intent.putExtra("profilePicUrl",RetrofitAPI.BASE_URL +response.body().getImagePath());
                                            intent.putExtra("userId",userId);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
//                                            Log.v("error failed",error.getMessage());
                                        }
                                    });

                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Shared.Alert(MainActivity.this, getString(R.string.failed), getString(R.string.failed_to_retrieve_data));
//                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<Person> call, Throwable t) {
                                progressDialog.dismiss();
                                Shared.Alert(MainActivity.this, getString(R.string.failed), getString(R.string.failed_to_retrieve_data));
//                                Log.e(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                    }
                    progressDialog.dismiss();

                }else{
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                    if(account != null){
                        String userName = account.getDisplayName();
                        String userEmail = account.getEmail();
                        String userPhoto = String.valueOf(account.getPhotoUrl());
                        String userId = account.getEmail().replace('.','_');
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.child("users").hasChild(userId)){
                                    databaseReference.child("users").child(userId).child("email").setValue(userEmail);
                                    databaseReference.child("users").child(userId).child("name").setValue(userName);
                                    databaseReference.child("users").child(userId).child("profilePicUrl").setValue(userPhoto);
                                }
                                progressDialog.dismiss();
                                Intent intent= new Intent(MainActivity.this, ChatList.class);
                                intent.putExtra("email",userEmail);
                                intent.putExtra("name",userName);
                                intent.putExtra("profilePicUrl",userPhoto);
                                intent.putExtra("userId",userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
//                                Log.v("error failed",error.getMessage());
                            }
                        });
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                    }
                }

            }
        });

        findViewById(R.id.medecineREM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medrem = new Intent(getApplicationContext(), Agenda.class);
                startActivity(medrem);
            }
        });
        findViewById(R.id.steps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medrem = new Intent(getApplicationContext(), AcceloGraph.class);
                startActivity(medrem);
            }
        });
        findViewById(R.id.waterreminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent water1 = new Intent(getApplicationContext(), Splashscreen2.class);
                startActivity(water1);
            }
        });
        findViewById(R.id.my_agenda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                User user = databaseHelper.getUser();
                if (user != null){
                    Shared.login(MainActivity.this, user.getEmail(), user.getPassword());
                    Handler handler =new Handler();
                    Runnable runer ;
                    runer = new Runnable(){
                        @Override
                        public void run() {
                            if (!Shared.token.isEmpty()) {
                                Intent agenda = new Intent(getApplicationContext(), Todo_list.class);
                                startActivity(agenda);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                            }
                        }
                    };
                    handler.postDelayed(runer,2000);

                }else{


                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        overridePendingTransition(android.R.anim.slide_out_right,R.anim.slide_in_left );


                }

            }

        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        bottomNavigationView.setSelectedItemId(R.id.navigationHome);


        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*for collapsed side bar*/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_manage) {

            Intent intent = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_switch) {
            isChecked = !item.isChecked();
            View v = item.getActionView();
            ss = (SwitchMaterial) v;
            Intent intent = new Intent(getApplicationContext(), FallDetection.class);

            if (isChecked && !switchVar && !FallDetectionActivity.switchvar) {
                if (icount > 0) {
                    ss.setChecked(true);
                    switchVar = FallDetectionActivity.switchvar = true;
                    Toast.makeText(getApplicationContext(), "Safe Walk ! We track you for safety", Toast.LENGTH_SHORT).show();
                    startService(intent);
                    Intent bg_running = new Intent(getApplicationContext(), FallRunningBG.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, bg_running, 0);
                    long futureInMillis = SystemClock.elapsedRealtime() + 1000;
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    assert alarmManager != null;
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
                    item.setChecked(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Add at least one contact then try again", Toast.LENGTH_LONG).show();
                }

            } else {
                ss.setChecked(false);
                stopService(intent);
                item.setChecked(false);
                switchVar = FallDetectionActivity.switchvar = false;
            }


            return true;
        } else if (id == R.id.nav_dark_mode) {

            //code for setting dark mode
            DarkModePrefManager darkModePrefManager = new DarkModePrefManager(this);
            darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();

        } else if (id == R.id.Accelo) {
            Intent intent = new Intent(MainActivity.this, AcceloGraph.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //works only for that activity ... I should create a class to use it f blasyss khrin
    public void setDarkMode(Window window) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            changeStatusBar(MODE_DARK, window);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            changeStatusBar(MODE_LIGHT, window);
        }
    }

    public void changeStatusBar(int mode, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.contentStatusBar));
            //Light mode
            if (mode == MODE_LIGHT) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("openNav", 0);
        if (preferences.getBoolean("openvaav", true)) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("openvaav", false);
            editor.apply();
        }
    }
}
