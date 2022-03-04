package com.example.testchat.Views;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.Models.Todo;
import com.example.testchat.Models.TodoModel;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class createNew extends AppCompatActivity {

    private FloatingActionButton addTask;
    TextView task , desc , category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createtaskk);
        addTask = (FloatingActionButton) findViewById(R.id.addToMongo);
        task = findViewById(R.id.addTaskTitle);
        desc = findViewById(R.id.addTaskDescription);
        category = findViewById(R.id.taskCategory);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask(getApplicationContext(),task.getText().toString(),desc.getText().toString(),null,category.getText().toString());
                Handler handler = new Handler();
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),Todo_list.class));
                    }
                };
                handler.postDelayed(run,2000);
            }
        });
    }




    public  void createTask(Context context, String title, String description , String date , String category){

        APIServices service =   RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        TodoModel todoModel = new TodoModel(title, description,category,date );

        Call<Todo> call = service.createtask("barer "+ Shared.token,todoModel);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                try{


                    if (response.body().getTodo()!=null) {
                        Shared.Alert(context, "Error !", "Done");
                        return;
                    } else  {
                        Shared.Alert(context, "Error !", "error occured");
                        return;
                    }
                }catch (Exception e){


                    Log.e(TAG,"onFailure: "+ e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Todo> call, Throwable t) {

                Toast.makeText(createNew.this,"Just keep waiting" ,Toast.LENGTH_LONG).show();
               // Shared.Alert(context,"Error !","An Error was occurred try later ");
                Log.e(TAG,"onFailure: "+ t.getMessage());
            }
        });
    }

}