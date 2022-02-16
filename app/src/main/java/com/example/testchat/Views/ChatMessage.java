package com.example.testchat.Views;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.testchat.Models.Message;
import com.example.testchat.R;
import com.example.testchat.Adapters.ChatAdapter;
import com.example.testchat.Services.Shared;
import com.example.testchat.Services.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        doctorName = findViewById(R.id.name);
        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.recycler_messages);
        doctorProfile = findViewById(R.id.doctorProfilePic);
        editMsg = findViewById(R.id.editMsg);
        Calendar calander = Calendar.getInstance();
        String getname = getIntent().getExtras().getString("name");
        doctorName.setText(getname);
        String getEmail = getIntent().getExtras().getString("email");
        String getProfilePic = getIntent().getExtras().getString("profilePic");
        Log.v("Image Error ",getProfilePic);

        if (getProfilePic!= null && !getProfilePic.isEmpty()){
            Glide.with(ChatMessage.this).load(getProfilePic).into(doctorProfile);
        }
        chatKey = getIntent().getExtras().getString("chatId");
        //recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(messages, ChatMessage.this, Utils.Email);
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
                finish();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String getEditMsg = editMsg.getText().toString();
                if (!getEditMsg.equals("")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                    String time = simpleDateFormat.format(calander.getTime());
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = simpleDateFormat.format(calander.getTime());
                    String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                    Utils.LasTimestamp = currentTimestamp;
                    databaseReference.child("chat").child(chatKey).child("doctor").setValue(getEmail);
                    databaseReference.child("chat").child(chatKey).child("user").setValue(Utils.Email);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(getEditMsg);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("email").setValue(Utils.Email);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("date").setValue(date);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("time").setValue(time);
                    editMsg.setText("");
                }
            }
        });

    }
}