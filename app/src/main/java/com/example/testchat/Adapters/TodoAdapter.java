package com.example.testchat.Adapters;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testchat.Models.EmergencyContact;
import com.example.testchat.Models.Result;
import com.example.testchat.Models.Todo;
import com.example.testchat.Models.TodoModel;
import com.example.testchat.Models.TodoModelAdapt;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.example.testchat.Views.ProfileActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoAdapter extends ArrayAdapter<TodoModelAdapt> {


    private Context context;
    private  int resource;


    public TodoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TodoModelAdapt> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(resource,parent,false);
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        TextView day = convertView.findViewById(R.id.day);
        TextView date = convertView.findViewById(R.id.date);
        TextView month = convertView.findViewById(R.id.month);
        TextView category = convertView.findViewById(R.id.category);
        title.setText(getItem(position).getTitle());
        description.setText(getItem(position).getDescription());
        day.setText(getItem(position).getDay());
        date.setText(getItem(position).getDate());
        month.setText(getItem(position).getMonth());
        category.setText(getItem(position).getCategory());


        return convertView;
    }

    public static void createTask(Context context,String title, String description , String date , String category){

        APIServices service =   RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        TodoModel todoModel = new TodoModel(title, description,category,date );
        
        Call<Todo> call = service.createtask("barer "+Shared.token,todoModel);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                try{


                    if (response.body().isSuccess()) {
                        Shared.Alert(context, "Error !", "bye bye ");
                        return;
                    } else  {
                        Shared.Alert(context, "Error !", "Null");
                        return;
                    }
                }catch (Exception e){


                    Log.e(TAG,"onFailure: "+ e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Todo> call, Throwable t) {

                Shared.Alert(context,"Error !","An Error was occurred try later ");
                Log.e(TAG,"onFailure: "+ t.getMessage());
            }
        });
    }
}
