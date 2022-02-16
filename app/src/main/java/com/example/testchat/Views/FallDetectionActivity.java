package com.example.testchat.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.testchat.R;
import com.example.testchat.Services.ContactContract;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.FallDetection;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class FallDetectionActivity extends AppCompatActivity implements customizedAlert.customizedAlertListener ,NavigationView.OnNavigationItemSelectedListener  {

    ListView lv;
    EditText edit;
    private SQLiteDatabase sql;
    String provider;
    FloatingActionButton addContacts;
    private CoordinatorLayout coordinate;
    ArrayList<EmergencyContact> arrayList = new ArrayList<>();
    EmergencyAdapter arrayAdapter;

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
        SwitchMaterial simpleSwitch = (SwitchMaterial) findViewById(R.id.switchfall);

        Boolean switchState = simpleSwitch.isChecked();
        DatabaseHelper dpHelper = new DatabaseHelper(this);
        sql = dpHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM "+ ContactContract.TABLE_NAME;
        Cursor mcursor = sql.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);

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
                        startActivity(new Intent(FallDetectionActivity.this, MainActivity.class));
                        break;

                    case R.id.navigationMyProfile:

                        break;


                }


                return false;
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(icount>0){
                        Toast.makeText(getApplicationContext(),"Safe walking! We track you for safety",Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getApplicationContext(), FallDetection.class);
                        startService(intent);
                    }else{
                        simpleSwitch.setChecked(false);
                        Toast.makeText(getApplicationContext(),"Add at least one contact then try again",Toast.LENGTH_SHORT).show();
                    }
                    mcursor.close();
                }
                else{
                    Intent intent= new Intent(getApplicationContext(), FallDetection.class);
                    stopService(intent);
                }
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

        if(icount>0){
            Cursor cursor = getAllContacts();
            if (cursor.moveToFirst()){
                do{
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("contact"));
                    arrayList.add(new EmergencyContact(username,number));
                }while(cursor.moveToNext());
            }
            cursor.close();
             arrayAdapter = new EmergencyAdapter(this,R.layout.simplerow,arrayList);
            lv.setAdapter(arrayAdapter);
            Snackbar snackbar=Snackbar.make(coordinate,"To add more contacts PRESS \"+\" "  ,Snackbar.LENGTH_INDEFINITE);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setAction("OKAY", view -> {

            });
//                snackbar.setAnchorView(floatingButton);
            snackbar.show();

        }
        else {
            Snackbar snackbar=Snackbar.make(coordinate,"No contacts yet ! add now."  ,Snackbar.LENGTH_INDEFINITE);
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
            Toast.makeText(getApplicationContext(),arrayList.get(i).getNumber(), Toast.LENGTH_LONG).show();
               showPopUpMenu(view,i);
                return false;
            }
        });
    }
    public void openDialog() {
        customizedAlert exampleDialog = new customizedAlert();
        exampleDialog.show(getSupportFragmentManager(), "Add contact");
    }

    @Override
    public void applyTexts(String Name, String Number) {
        //Insert into db
        if(Name.length() != 10 && Number.length() !=10 ){
            Toast.makeText(getApplicationContext(),"Please enter again!",Toast.LENGTH_SHORT).show();
        }else{
            DatabaseHelper db = new DatabaseHelper(this);
            db.addNewContact(Name,Number);
            Toast.makeText(getApplicationContext(),"Contact Added",Toast.LENGTH_SHORT).show();
            Cursor cursor = getAllContacts();
            if (cursor.moveToFirst()){
                do{
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex("contact"));
                    arrayList.add(new EmergencyContact(username,number));
                    Toast.makeText(this, Number, Toast.LENGTH_SHORT).show();
                }while(cursor.moveToNext());
            }
            cursor.close();
            EmergencyAdapter arrayAdapter = new EmergencyAdapter(this,R.layout.simplerow,arrayList);
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
                    alertDialogBuilder.setTitle("Delete contact").setMessage("Are you sure , you want to delete this contact ?").
                            setPositiveButton("Yes", (dialog, which) -> {
                                deleteContactFromBynum(position);
                                Toast.makeText(getApplicationContext(),"You have deleted from contacts", Toast.LENGTH_LONG).show();

                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();
                    break;

            }
            return false;
        });
        popupMenu.show();
    }
    private void deleteContactFromBynum( int position) {
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


    public Cursor getAllContacts(){
        return sql.rawQuery("SELECT  * FROM " + ContactContract.TABLE_NAME +";",null);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


}