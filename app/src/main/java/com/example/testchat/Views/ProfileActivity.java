package com.example.testchat.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.testchat.Models.Person;
import com.example.testchat.R;
import com.example.testchat.Services.APIServices;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    TextView Nameup, Emailup, Name, Email, Phone, backHome;
    ImageView imgProfile;
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout = findViewById(R.id.logout);
        Nameup = findViewById(R.id.profileNameup);
        backHome = findViewById(R.id.profileBackHome);
        Name = findViewById(R.id.profileName);
        Email = findViewById(R.id.profileEmail);
        Phone = findViewById(R.id.profilePhone);
        Emailup = findViewById(R.id.profileEmailup);
        imgProfile = findViewById(R.id.profileImage);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            fillComponentGoogle(account);
        } else {
            fillComponent();
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(ProfileActivity.this);
                databaseHelper.deleteUser();
                Shared.token="";
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);
                if (account != null) signOut();
                onBackPressed();
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
    }
    private void fillComponentGoogle(GoogleSignInAccount acct) {
        String personName = acct.getDisplayName();
        String personEmail = acct.getEmail();
        Uri personPhoto = acct.getPhotoUrl();
        Nameup.setText(personName);
        Emailup.setText(personEmail);
        Email.setText(personEmail);
        Name.setText(personName);
        Phone.setText(R.string.not_available);
        String ProfileUrl = String.valueOf(personPhoto);
        if (personPhoto != null) {
            Glide.with(ProfileActivity.this).load(String.valueOf(personPhoto)).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.unknowuser);
        }


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void fillComponent() {
        APIServices service = RetrofitAPI.getRetrofitInstance().create(APIServices.class);
        Call<Person> call = service.getDataPerson("Bearer " + Shared.token);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                try {
                    Nameup.setText(response.body().getFullName());
                    Emailup.setText(response.body().getEmail());
                    Email.setText(response.body().getEmail());
                    Name.setText(response.body().getFullName());
                    Phone.setText(String.valueOf(response.body().getPhone()));
                    Glide.with(ProfileActivity.this).load(RetrofitAPI.BASE_URL + response.body().getImagePath()).into(imgProfile);

                } catch (Exception e) {
                    Shared.Alert(ProfileActivity.this, getString(R.string.failed), getString(R.string.failed_to_retrieve_data));
//                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                Shared.Alert(ProfileActivity.this, getString(R.string.failed), getString(R.string.failed_to_retrieve_data));
//                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}