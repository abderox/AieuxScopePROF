package com.example.testchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testchat.Adapters.DoctorsListAdapter;
import com.example.testchat.Models.DoctorSimpleInfo;
import com.example.testchat.R;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DoctorsList extends AppCompatActivity {
    List<DoctorSimpleInfo> doctors = new ArrayList<>();
    DoctorsListAdapter doctorAdapter;
    RecyclerView recyclerView;
    int REQUEST_CODE_SPEECH_INPUT =1;
    ImageView backBtn;
    EditText searchText;
    ImageButton searchBtn, recogniteBtn;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);
        backBtn = findViewById(R.id.backBtn);
        recogniteBtn = findViewById(R.id.recognite);
        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);
        recyclerView = findViewById(R.id.recycler_doctors);
        doctorAdapter = new DoctorsListAdapter(doctors,DoctorsList.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(DoctorsList.this));
        recyclerView.setAdapter(doctorAdapter);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchText.getText().toString();
                if (!text.isEmpty())
                    getList(text);            }
        });
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = searchText.getText().toString();
                if (!text.isEmpty())
               getList(text);

                return false;
            }
        });
        recogniteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_text));
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast.makeText(DoctorsList.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
            });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorsList.this,ChatList.class));
                overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DoctorsList.this,ChatList.class));
        overridePendingTransition(android.R.anim.slide_out_right, R.anim.slide_in_left);
    }
    private void getList(String text) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctors.clear();
                if (snapshot.hasChild("chat")) {
                    for (DataSnapshot doctor :snapshot.child("doctors").getChildren()
                    ) {
                        String email = doctor.child("email").getValue(String.class);
                        String namePart = email.substring(0, email.length() - 10);
                        if (namePart.contains(text)){
                            String profilePic = doctor.child("profilePicUrl").getValue(String.class);
                            String profilePicUrl = RetrofitAPI.BASE_URL +profilePic.substring(22,profilePic.length());
                            String name = doctor.child("name").getValue(String.class);
                            doctors.add(new DoctorSimpleInfo(email,profilePicUrl,name));
                        }
                        doctorAdapter.updateData(doctors);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchText.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }
}