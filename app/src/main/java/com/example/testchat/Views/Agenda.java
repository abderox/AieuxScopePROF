package com.example.testchat.Views;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchat.Adapters.BottomNavigationViewHelper;
import com.example.testchat.Adapters.CreateTaskBottomSheetFragment;
import com.example.testchat.Adapters.ShowCalendarViewBottomSheet;
import com.example.testchat.Adapters.TaskAdapter;
import com.example.testchat.Models.Task;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.Shared;
import com.example.testchat.Services.broadcastReceiver.AlarmBroadcastReceiver;
import com.example.testchat.Services.database.DatabaseClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class Agenda extends AppCompatActivity implements CreateTaskBottomSheetFragment.setRefreshListener  {

    @BindView(R.id.taskRecycler)
    RecyclerView taskRecycler;
    @BindView(R.id.addTask)
    TextView addTask;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    @BindView(R.id.noDataImage)
    ImageView noDataImage;
    @BindView(R.id.calendar)
    ImageView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_agenda);

//        findViewById(R.id.add_task_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        taskRecycler =findViewById(R.id.taskRecycler);
        noDataImage =findViewById(R.id.noDataImage);
        calendar = findViewById(R.id.calendar);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationt);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigationHome:

                        item.setChecked(true);
                        startActivity(new Intent(Agenda.this, MainActivity.class));
                        break;

                    case R.id.navigationMyProfile:

                        DatabaseHelper databaseHelper = new DatabaseHelper(Agenda.this);
                        User user = databaseHelper.getUser();
                        if (user != null){
                            Shared.login(Agenda.this, user.getEmail(), user.getPassword());
                            if (!Shared.token.isEmpty()) {
                                Intent intent = new Intent(Agenda.this, ProfileActivity.class);
                                Agenda.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                            }
                        }else{
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Agenda.this);
                            if(account != null){
                                startActivity(new Intent(Agenda.this,ProfileActivity.class));
                                overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);

                            }else{

                                startActivity(new Intent(Agenda.this,LoginActivity.class));
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
                        startActivity(new Intent(Agenda.this, MainActivity.class));
                        break;
                }


                return false;
            }
        });

        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        Glide.with(getApplicationContext()).load(R.drawable.first_note).into(noDataImage);

        findViewById(R.id.addTask).setOnClickListener(view -> {
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskId(0, false, this, Agenda.this);
            createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        getSavedTasks();

        findViewById(R.id.calendar).setOnClickListener(view -> {
            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(this, tasks, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks() {

        @SuppressLint("StaticFieldLeak")
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                noDataImage.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}



