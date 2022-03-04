package com.example.testchat.Services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.testchat.Models.Result;
import com.example.testchat.Models.User;
import com.example.testchat.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Shared {

    public static String token="";
    public static String currentUser="";
    public static String Link="https://authmobile-1640176421715-default-rtdb.firebaseio.com/";
    public static  void login(Context context,String email, String password){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setTitle(context.getString(R.string.wait));
        progressDialog.show();
        APIServices service =   RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        User user = new User(email,password);
        Call<Result> call = service.login(user);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try{
                    progressDialog.dismiss();
                    switch (response.body().getResult()){
                        case "error":{
                            Shared.Alert(context,context.getString(R.string.failed),context.getString(R.string.an_error_was_occurred_try_later));
                            Shared.token = "";

                            return;
                        }
                        case "not" : {
                            Shared.Alert(context,context.getString(R.string.failed),context.getString(R.string.the_email_or_password));
                            Shared.token = "";
                            return;
                        }
                        default:
                            DatabaseHelper databaseHelper = new DatabaseHelper(context);
                            databaseHelper.deleteUser();
                            databaseHelper.addUser(user);
                            Shared.token = response.body().getResult();

                    }
                }catch (Exception e){
                    progressDialog.dismiss();
                    Shared.Alert(context,context.getString(R.string.failed),context.getString(R.string.failed_to_login_try_later));
                    Shared.token = "";
//                    Log.e(TAG,"onFailure: "+ e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Shared.Alert(context,context.getString(R.string.failed),context.getString(R.string.failed_to_login_try_later));
//                Log.e(TAG,"onFailure: "+ t.getMessage());
            }
        });
    }
    public static void Alert(Context context, String Title, String Body){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title)
                .setMessage(Body)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }
}
