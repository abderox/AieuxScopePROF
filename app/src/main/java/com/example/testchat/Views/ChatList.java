package com.example.testchat.Views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.testchat.Models.MessageList;

import com.example.testchat.Adapters.MessageListAdapter;
import com.example.testchat.R;
import com.example.testchat.Services.Shared;
import com.example.testchat.Services.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatList extends AppCompatActivity {

    List<MessageList> messageLists = new ArrayList<>();
    RecyclerView recyclerView;
    ImageView imageView;
    TextView myName;
    boolean dataSet = false;
    CircleImageView circleImageView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);
    MessageListAdapter messageListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        imageView = findViewById(R.id.backHome);
        myName = findViewById(R.id.myname);
        myName.setText("Welcome "+ Utils.Name);
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

        ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setCancelable(false);
        progressDialog.setMessage("loading");
        progressDialog.setTitle("wait");
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePicUrl = snapshot.child("users").child(Utils.Email.replace('.','_')).child("profilePicUrl").getValue(String.class);

                if (profilePicUrl != null && !profilePicUrl.isEmpty())
                    Glide.with(ChatList.this).load(profilePicUrl).into(circleImageView);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.v("error failed", error.getMessage());
            }
        });
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
                    Log.v("Test Error ", getDoctorKey);
                    if (!getDoctorKey.equals(Utils.Email.replace('.','_'))) {
                        int getMsgCount = 0;
                        String getLastMsg = "";
                        String getTime = "";
                        String getDate = "";
                        chatId ="";
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String getProfilePic = dataSnapshot.child("profilePicUrl").getValue(String.class);
                        if (getChatCounts > 0) {
                            for (DataSnapshot dataSnapshot1 : snapshot.child("chat").getChildren()
                            ) {
                                String getKey = dataSnapshot1.getKey();
                                if (dataSnapshot1.hasChild("doctor") && dataSnapshot1.hasChild("user") && dataSnapshot1.hasChild("messages")) {
                                    String getDoctor = dataSnapshot1.child("doctor").getValue(String.class);
                                    String getUser = dataSnapshot1.child("user").getValue(String.class);
                                    if ((getDoctor.equals(getEmail) && getUser.equals(Utils.Email)) ) {
                                        chatId = getKey;
                                        getEmail = getDoctor;
                                        for (DataSnapshot msgSnapshot : dataSnapshot1.child("messages").getChildren()
                                        ) {
                                            long getLastTimestamp = Long.parseLong(msgSnapshot.getKey());
                                            long getLastTimestampLocal = Long.parseLong(Utils.LasTimestamp);
                                            getLastMsg = msgSnapshot.child("msg").getValue(String.class);
                                            getTime = msgSnapshot.child("time").getValue(String.class);
                                            getDate = msgSnapshot.child("date").getValue(String.class);
                                            if (getLastTimestamp > getLastTimestampLocal)
                                                getMsgCount++;
                                            Utils.LasTimestamp = msgSnapshot.getKey();
                                        }


                                    }
                                }

                            }
                        }
                        dataSet = true;
                        MessageList messageList = new MessageList(getName, getEmail, getLastMsg, getProfilePic, getTime, getMsgCount, chatId);
                        messageLists.add(messageList);
                        Iterator<MessageList> iterator = messageLists.iterator();
                        while (iterator.hasNext()) {
                            Log.v( "Test Error ", iterator.next().getEmail());
                        }
                        messageListAdapter.updateData(messageLists);

                        Log.v("Test Error ", "IN LOOP NOW "+ getEmail);
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