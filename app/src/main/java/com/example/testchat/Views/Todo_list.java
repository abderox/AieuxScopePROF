package com.example.testchat.Views;

import static android.service.controls.ControlsProviderService.TAG;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.testchat.Adapters.BottomNavigationViewHelper;
import com.example.testchat.Adapters.EmergencyAdapter;
import com.example.testchat.Adapters.TodoAdapter;
import com.example.testchat.Adapters.customizedAlert;
import com.example.testchat.Adapters.customizedAlertM;
import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.Models.Person;
import com.example.testchat.Models.ResponseTask;
import com.example.testchat.Models.Todo;
import com.example.testchat.Models.TodoModel;
import com.example.testchat.Models.TodoModelAdapt;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.ContactContract;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.FallDetection;
import com.example.testchat.Services.FallRunningBG;
import com.example.testchat.Services.RetrofitAPI;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Todo_list extends AppCompatActivity implements customizedAlertM.customizedAlertListenerr, NavigationView.OnNavigationItemSelectedListener{


    ListView lv;
    FloatingActionButton addTask;
    private CoordinatorLayout coordinate;
    ArrayList<TodoModelAdapt> arrayList = new ArrayList<>();
    ArrayList<TodoModel> arrayListM = new ArrayList<>();
    TodoAdapter arrayAdapter;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mongo_tasks);



        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
        lv = (ListView) findViewById(R.id.tasks);
        coordinate = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationt);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        findViewById(R.id.options2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialog();

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigationHome:

                        item.setChecked(true);
                        startActivity(new Intent(Todo_list.this, MainActivity.class));
                        break;

                    case R.id.navigationMyProfile:

                        DatabaseHelper databaseHelper = new DatabaseHelper(Todo_list.this);
                        User user = databaseHelper.getUser();
                        if (user != null){
                            Shared.login(Todo_list.this, user.getEmail(), user.getPassword());
                            if (!Shared.token.isEmpty()) {
                                Intent intent = new Intent(Todo_list.this, ProfileActivity.class);
                                Todo_list.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                            }
                        }else{
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Todo_list.this);
                            if(account != null){
                                startActivity(new Intent(Todo_list.this,ProfileActivity.class));
                                overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);

                            }else{

                                startActivity(new Intent(Todo_list.this,LoginActivity.class));
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
                        startActivity(new Intent(Todo_list.this, MainActivity.class));
                        break;
                }


                return false;
            }
        });

        findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                fillComponent();
            }
        });


       fillComponent();


        addTask = (FloatingActionButton) findViewById(R.id.addTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //  createTask(getApplicationContext(),"jjjj","jdjdj",null,"jjdjdj");
//                openDialog();
                startActivity(new Intent(Todo_list.this,createNew.class));
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList.get(i).getTitle(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }


    public void openDialog() {
        customizedAlertM exampleDialog = new customizedAlertM();
        exampleDialog.show(getSupportFragmentManager(), getString(R.string.addNum));
    }

    @Override
    public void applyTexts( String Number) {

        if (  Number.length() != 10) {
            Toast.makeText(getApplicationContext(), getString(R.string.tryagainn), Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            final String  randomed = randomize();
            String textMsg = getString(R.string.senMsg) +randomed;
            DatabaseHelper databaseHelper = new DatabaseHelper(Todo_list.this);
            User user = databaseHelper.getUser();

            databaseReference.child("code").child(user.getEmail().replace('.','_')).child("email").setValue(user.getEmail());
            databaseReference.child("code").child(user.getEmail().replace('.','_')).child("code").setValue(randomed);

            smsManager.sendTextMessage(Number, null, textMsg, null, null);
            Toast.makeText(getApplicationContext(), getString(R.string.messageSe)+Number, Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    private void fillComponent() {
        APIServices service = RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        Call<ResponseTask> call = service.getDataTask("Bearer " + Shared.token);
        call.enqueue(new Callback<ResponseTask>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseTask> call, retrofit2.Response<ResponseTask> response) {
                try {

                    if(response.body().getTodos()!=null && response.body().getTodos().length!=0)
                    {
                        Toast.makeText(getApplicationContext(), response.body().getTodos()[0].getTitle(), Toast.LENGTH_SHORT).show();

                        for(int i=0 ; i<response.body().getTodos().length ; i++) {
                            arrayListM.add(response.body().getTodos()[i]);
                        }
                        function();
                    }





                } catch (Exception e) {
                    Shared.Alert(Todo_list.this, "Error !", "An Error was occurred try later ");
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseTask> call, Throwable t) {
                Shared.Alert(Todo_list.this, "Error !", "An Error was occurred try later ");
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

//    private void getTasks() {
//
//        String url = "https://2e54-102-52-140-208.ngrok.io/todo";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                url, null, new Response.Listener<JSONObject>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//
//
//                    if(response.getBoolean("success")) {
//                        JSONArray jsonArray = response.getJSONArray("todos");
//
//                        if(jsonArray.length() == 0) {
//
//                        } else {
//                            for(int i = 0; i < jsonArray.length(); i ++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                TodoModel todoModel = new TodoModel(
//                                        jsonObject.getString("_id"),
//                                        jsonObject.getString("title"),
//                                        jsonObject.getString("description"),
//                                        jsonObject.getString("category"),
//                                        jsonObject.getString("date")
//                                );
//                                arrayListM.add(todoModel);
//                            }
//                            function();
//
//
//                        }
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                if (error == null || error.networkResponse == null) {
//                    return;
//                }
//
//                String body;
//
//                try {
//                    body = new String(error.networkResponse.data,"UTF-8");
//                    JSONObject errorObject = new JSONObject(body);
//
//                } catch (UnsupportedEncodingException | JSONException e) {
//                    // exception
//                    e.printStackTrace();
//                }
//
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "barr eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InJveGFANDI4MEBnbWFpbC5jb20iLCJ1c2VySWQiOiI2MWNiODlmMjJjNTdjMWU4ZTE4ZTExOGEiLCJpYXQiOjE2NDQ4MzEwMjgsImV4cCI6MTY0NDgzODIyOH0.G170hPPtVcwUjHoat92lgWIWVnl8rtcPYxV19CqNrD0");
//                return headers;
//            }
//        };
//
//        // set retry policy
//        int socketTime = 3000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        jsonObjectRequest.setRetryPolicy(policy);
//
//        // request add
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
//    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void function()
    {
        Handler mhandler = new Handler();
        for(int i=0 ;i<arrayListM.size();i++)
        {
            if(arrayListM.get(i).getDate() !=null)
            {
                String day , month , date  ="";
                month = arrayListM.get(i).getDate().split("-")[1];
                date = getMonth(Integer.parseInt(month)).substring(0,3);
                day = arrayListM.get(i).getDate().split("-")[2].split("T")[0];
                Format f = new SimpleDateFormat("EEEE");
                String str = f.format(new Date(2022,Integer.parseInt(month),Integer.parseInt(day)-1));
                arrayList.add(new TodoModelAdapt(arrayListM.get(i).getTitle(),arrayListM.get(i).getDescription(),arrayListM.get(i).getCategory(),str.substring(0,3),day,date));
            }
            else {

                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                System.out.println(new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()));

                arrayList.add(new TodoModelAdapt(arrayListM.get(i).getTitle(),arrayListM.get(i).getDescription(),arrayListM.get(i).getCategory(),new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()) ,valueOf(calendar.get(Calendar.DATE)),valueOf(Calendar.MONTH)));
            }


        }

        Toast.makeText(getApplicationContext(), " Please Wait a moment !", Toast.LENGTH_SHORT).show();
        Runnable mTimer1 = new Runnable() {
            @Override
            public void run() {
                arrayAdapter = new TodoAdapter(getApplicationContext(), R.layout.simplerow2, arrayList);
                lv.setAdapter(arrayAdapter);
            }
        };

        mhandler.postDelayed(mTimer1,100);

    }
    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDay(int day) {
        return DayOfWeek.of(day).toString();
    }

    private String randomize()
    {
        Random random = new Random();
        String str="";
        for(int i =0 ; i< 5; i++)
        {
            str += random.nextInt(9);
        }
        return str;
    }
}