package com.example.testchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchat.Adapters.MessageListAdapter;
import com.example.testchat.Models.MessageList;
import com.example.testchat.R;
import com.example.testchat.Services.DatabaseHelper;
import com.example.testchat.Services.RetrofitAPI;
import com.example.testchat.Services.Shared;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatList extends AppCompatActivity {

    List<MessageList> messageLists = new ArrayList<>();
    RecyclerView recyclerView;
    ImageView imageView;
    FloatingActionButton addDoctor;
    TextView myName;
    DatabaseHelper databaseHelper ;
    boolean dataSet = false;
    CircleImageView circleImageView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);
    MessageListAdapter messageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        databaseHelper = new DatabaseHelper(ChatList.this);
        // extract data
        String userEmail = getIntent().getExtras().getString("email");
        Shared.currentUser = userEmail;
        String userName = getIntent().getExtras().getString("name");
        String userPhoto = getIntent().getExtras().getString("profilePicUrl");
        String userId = getIntent().getExtras().getString("userId");
        imageView = findViewById(R.id.backHome);
        addDoctor = findViewById(R.id.addDoctor);
        myName = findViewById(R.id.myname);
        myName.setText(getString(R.string.welcome)+" "+ userName);
        addDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatList.this,DoctorsList.class);
                startActivity(intent);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_list);
        circleImageView = findViewById(R.id.imgprofile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageListAdapter = new MessageListAdapter(messageLists, ChatList.this);
        recyclerView.setAdapter(messageListAdapter);
        if (userPhoto != null && !userPhoto.isEmpty())
            Glide.with(ChatList.this).load(userPhoto).into(circleImageView);
        else circleImageView.setImageResource(R.drawable.unknowuser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            String chatId = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageLists.clear();
                int getChatCounts = (int) snapshot.child("chat").getChildrenCount();

                for (DataSnapshot dataSnapshot : snapshot.child("doctors").getChildren()
                ) {
                    String getEmail = dataSnapshot.child("email").getValue(String.class);
                    String getDoctorKey = dataSnapshot.getKey();
                    if (!getDoctorKey.equals(userId)) {
                        int getMsgCount = 0;
                        String getLastMsg = "";
                        String getTime = "";
//                        String getDate = "";
                        chatId ="";
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String profilePic = dataSnapshot.child("profilePicUrl").getValue(String.class);
                        String getProfilePic = RetrofitAPI.BASE_URL +profilePic.substring(22,profilePic.length());
                        if (getChatCounts > 0) {
                            for (DataSnapshot dataSnapshot1 : snapshot.child("chat").getChildren()
                            ) {
                                String getKey = dataSnapshot1.getKey();
                                if (dataSnapshot1.hasChild("doctor") && dataSnapshot1.hasChild("user") && dataSnapshot1.hasChild("messages")) {
                                    String getDoctor = dataSnapshot1.child("doctor").getValue(String.class);
                                    String getUser = dataSnapshot1.child("user").getValue(String.class);
                                    String getLastTime = dataSnapshot1.child("lastTime").getValue(String.class);
                                    if ((getDoctor.equals(getEmail) && getUser.equals(userEmail)) ) {
                                        chatId = getKey;
                                        getEmail = getDoctor;
                                        for (DataSnapshot msgSnapshot : dataSnapshot1.child("messages").getChildren()
                                        ) {
                                            long getLastTimestamp = Long.parseLong(msgSnapshot.getKey());
                                            if (getLastTime == null || getLastTime.isEmpty()){

                                                getLastTime = String.valueOf(System.currentTimeMillis());
                                            }
                                            long getLastTimestampLocal = Long.parseLong(getLastTime);
                                            getLastMsg = msgSnapshot.child("msg").getValue(String.class);
                                            getTime = msgSnapshot.child("time").getValue(String.class);
//                                            getDate = msgSnapshot.child("date").getValue(String.class);
                                            if (getLastTimestamp > getLastTimestampLocal)
                                                getMsgCount++;
//                                            databaseHelper.updateTempData(String.valueOf(getLastTimestamp));
                                        }
                                        dataSet = true;
                                        MessageList messageList = new MessageList(getName, getEmail, getLastMsg, getProfilePic, getTime, getMsgCount, chatId);
                                        messageLists.add(messageList);
                                        messageListAdapter.updateData(messageLists);

                                    }
                                }

                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}