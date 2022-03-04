package com.example.testchat.Views;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchat.Adapters.ChatAdapter;
import com.example.testchat.Models.Message;
import com.example.testchat.R;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.Shared;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatMessage extends AppCompatActivity {
    TextView doctorName, status;
    ImageView backBtn, doctorProfile;
    List<Message> messages = new ArrayList<>();
    LinearLayout sendBtn;
    String chatKey;
    EditText editMsg;
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper ;
    boolean isLocked = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        databaseHelper = new DatabaseHelper(ChatMessage.this);
        doctorName = findViewById(R.id.name);
        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.recycler_messages);
        doctorProfile = findViewById(R.id.doctorProfilePic);
        editMsg = findViewById(R.id.editMsg);
        String getname = getIntent().getExtras().getString("name");
        doctorName.setText(getname);
        String getEmail = getIntent().getExtras().getString("email");
        String getProfilePic = getIntent().getExtras().getString("profilePic");
         isLocked = getIntent().getExtras().getBoolean("isLocked");

        if (getProfilePic!= null && !getProfilePic.isEmpty()){
            Glide.with(ChatMessage.this).load(getProfilePic).into(doctorProfile);
        }
        chatKey = getIntent().getExtras().getString("chatId");
        //recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(messages, ChatMessage.this, Shared.currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatMessage.this));
        recyclerView.setAdapter(chatAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatAdapter.clear();
                Message.staDate= "12-01-2000";
                messages.clear();
                if (chatKey.isEmpty()) {
                    chatKey = "1";
                    if (snapshot.hasChild("chat")) {
                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                        for (DataSnapshot data : snapshot.child("chat").getChildren()
                        ) {
                            String doctor = data.child("doctor").getValue(String.class);
                            String user = data.child("user").getValue(String.class);
                            if (getEmail.equals(doctor)&& Shared.currentUser.equals(user))chatKey =data.getKey();
                        }
                    }
                }
                if (snapshot.hasChild("chat")) {
                    for (DataSnapshot data : snapshot.child("chat").child(chatKey).child("messages").getChildren()
                    ) {
                        if (data.hasChild("msg") && data.hasChild("email")) {
                            String getTimestamp = data.getKey();
                            String _getEmail = data.child("email").getValue(String.class);
                            String _getMessage = data.child("msg").getValue(String.class);
                            String _getTime = data.child("time").getValue(String.class);
                            String _getDate = data.child("date").getValue(String.class);
                            if (_getDate != null){
                                Message _message = new Message(_getEmail, _getMessage, _getDate, _getTime);
                                _message.setCheckDate();
                                messages.add(_message);
                            }
                            chatAdapter.updateData(messages);
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTimestampString = String.valueOf(System.currentTimeMillis());
                if (!isLocked)
                databaseReference.child("chat").child(chatKey).child("lastTime").setValue(currentTimestampString);
//                String currentTimestamp = String.valueOf(System.currentTimeMillis());
//                databaseHelper.updateTempData(currentTimestamp);
                   finish();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                long currentTimestamp = System.currentTimeMillis();
                String getEditMsg = editMsg.getText().toString();
                if (!getEditMsg.equals("")) {
                    Date resultdate = new Date(currentTimestamp);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String time = simpleDateFormat.format(resultdate);
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = simpleDateFormat.format(resultdate);
                    String currentTimestampString = String.valueOf(currentTimestamp);
                    databaseReference.child("chat").child(chatKey).child("doctor").setValue(getEmail);
                    databaseReference.child("chat").child(chatKey).child("user").setValue(Shared.currentUser);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestampString).child("msg").setValue(getEditMsg);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestampString).child("email").setValue(Shared.currentUser);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestampString).child("date").setValue(date);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestampString).child("time").setValue(time);
                    databaseReference.child("chat").child(chatKey).child("lastTime").setValue(currentTimestampString);
                    editMsg.setText("");
                    currentTimestampString = String.valueOf(System.currentTimeMillis());
                    databaseReference.child("chat").child(chatKey).child("lastTime").setValue(currentTimestampString);
                    isLocked = false;
//                    databaseHelper.updateTempData(currentTimestampString);

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String currentTimestampString = String.valueOf(System.currentTimeMillis());
        if (!isLocked)
        databaseReference.child("chat").child(chatKey).child("lastTime").setValue(currentTimestampString);
    }
}