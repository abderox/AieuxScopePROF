package com.example.testchat.Services;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testchat.Models.Result;
import com.example.testchat.Models.User;
import com.example.testchat.Views.ProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Shared {

    public static String token="";
    public static String Link="https://authmobile-1640176421715-default-rtdb.firebaseio.com/";
    public static  void login(Context context,String email, String password){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading");
        progressDialog.setTitle("wait");
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
                            Shared.Alert(context,"Error !","An Error was occurred try later ");
                            return;
                        }
                        case "not" : {
                            Shared.Alert(context,"Error !","the email or password selected is incorrect");
                            return;
                        }
                        default:
                            DatabaseHelper databaseHelper = new DatabaseHelper(context);
                            databaseHelper.deleteUser();
                            databaseHelper.addUser(user);
                            Shared.token = response.body().getResult();
                            Intent intent = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent);
                    }
                }catch (Exception e){
                    progressDialog.dismiss();
                    Shared.Alert(context,"Error !","An Error was occurred try later ");
                    Log.e(TAG,"onFailure: "+ e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Shared.Alert(context,"Error !","An Error was occurred try later ");
                Log.e(TAG,"onFailure: "+ t.getMessage());
            }
        });
    }
    public static void Alert(Context context, String Title, String Body){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title)
                .setMessage(Body)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }
    public static boolean ConnectFirebase(Context context){
        boolean[] result = {false};
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading");
        progressDialog.setTitle("wait");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Link);
        Log.v("Rrrrrrrrrrrrrrrrrrrrrr","step1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                Log.v("Rrrrrrrrrrrrrrrrrrrrrr","step2");
                if (snapshot.child("users").hasChild(Utils.Email)){
                    Toast.makeText(context, "users exist", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("users").child(Utils.Email).child("email").setValue(Utils.Email);
                    databaseReference.child("users").child(Utils.Email).child("name").setValue(Utils.Name);
                    databaseReference.child("users").child(Utils.Email).child("profilePicUrl").setValue(Utils.ProfileUrl);
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                }
                result[0] = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                result[0] = false;
                Log.v("Rrrrrrrrrrrrrrrrrrrrrr","step3");
                Log.v("error failed",error.getMessage());
            }
        });
        progressDialog.dismiss();
        return result[0] ;
    }
}
