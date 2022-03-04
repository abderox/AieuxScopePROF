package com.example.testchat.Views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testchat.Models.Result;
import com.example.testchat.Models.User;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText txt_email,txt_phone,txt_password,txt_fullName;
    Button register,addImage;

    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    int RC_SIGN_IN =0;
    ImageView img;
    int RESULT_LOAD_IMAGE=1;
    String picturePath = null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
        register = findViewById(R.id.cirRegisterButton);
        txt_email = findViewById(R.id.editTextEmailR);
        txt_password = findViewById(R.id.editTextPasswordR);
        txt_phone = findViewById(R.id.editTextMobile);
        txt_fullName = findViewById(R.id.editTextName);
        addImage = findViewById(R.id.addImage);
        img = findViewById(R.id.imagePreView);
//        txt_fullName.setText("ahmed");
//        txt_email.setText("test1@test.com");
//        txt_password.setText("test");
//        txt_phone.setText("0643562789");
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_phone.getText().toString().matches("")) {
                    Shared.Alert(RegisterActivity.this,getString(R.string.important),getString(R.string.the_phone_field_is_required));
                    return;
                }if (txt_email.getText().toString().matches("")) {
                    Shared.Alert(RegisterActivity.this,getString(R.string.important),getString(R.string.the_email_field_is_required));
                    return;
                }if (txt_fullName.getText().toString().matches("")) {
                    Shared.Alert(RegisterActivity.this,getString(R.string.important),getString(R.string.the_name_field_is_required));
                    return;
                }if (txt_password.getText().toString().matches("")) {
                    Shared.Alert(RegisterActivity.this,getString(R.string.important),getString(R.string.the_password_field_is_required));
                    return;
                }
            addUser(txt_email.getText().toString(),txt_password.getText().toString(),txt_fullName.getText().toString()
              ,Long.parseLong(txt_phone.getText().toString()));
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onLoginClick(View view){
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    private void addUser(String email,String password,String fullName,long phone){
        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setTitle(getString(R.string.wait));
        progressDialog.show();
        APIServices service =   RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        if (picturePath != null) {
            User user = new User(email,password);
            File file = new File(picturePath);
            RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody b_email = RequestBody.create(MediaType.parse("text/plain"),email);
            RequestBody b_password = RequestBody.create(MediaType.parse("text/plain"),password);
            RequestBody b_fullName = RequestBody.create(MediaType.parse("text/plain"),fullName);
            RequestBody b_phone = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(phone));
            MultipartBody.Part part = MultipartBody.Part.createFormData("personImage",file.getName(),fbody);
            Call<Result> call = service.registration(part,b_email,b_password,b_fullName,b_phone);
            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    try {
                        progressDialog.dismiss();
                        switch (response.body().getResult()){
                            case "error":{
                                Shared.Alert(RegisterActivity.this,getString(R.string.failed),getString(R.string.registration_failed_try_later));
                                return;
                            }
                            case "not" : {
                                Shared.Alert(RegisterActivity.this,getString(R.string.failed),getString(R.string.the_email_selected_is_already_exist));
                                return;
                            }
                            default:
                                DatabaseHelper databaseHelper = new DatabaseHelper(RegisterActivity.this);
                                databaseHelper.deleteUser();
                                databaseHelper.addUser(user);
                                Shared.token = response.body().getResult();
                                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                                startActivity(intent);
                        }
                    }catch (Exception e){
                        progressDialog.dismiss();
                        Shared.Alert(RegisterActivity.this,getString(R.string.failed),getString(R.string.registration_failed_try_later));
//                        Log.e(TAG,"onFailure: "+ e.getMessage());
                    }

                   }
                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    progressDialog.dismiss();
                    Shared.Alert(RegisterActivity.this,getString(R.string.failed),getString(R.string.registration_failed_try_later));
//                    Log.e(TAG,"onFailure: "+ t.getMessage());
                }
            });
        }else{
            Shared.Alert(RegisterActivity.this,getString(R.string.important),getString(R.string.choose_an_image_for_your_profile));
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            img.setImageURI(selectedImage);
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI();
        } catch (ApiException e) {
            Shared.Alert(RegisterActivity.this,getString(R.string.failed),getString(R.string.failed_to_login_try_later));
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private  void updateUI(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
