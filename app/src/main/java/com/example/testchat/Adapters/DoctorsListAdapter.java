package com.example.testchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testchat.Models.DoctorSimpleInfo;
import com.example.testchat.R;
import com.example.testchat.Services.Shared;
import com.example.testchat.Views.ChatMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.MyViewHolder> {
    List<DoctorSimpleInfo> doctorLists;
    final Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(Shared.Link);

    public DoctorsListAdapter(List<DoctorSimpleInfo> doctorLists, Context context) {
        this.doctorLists = doctorLists;
        this.context = context;
    }
    public  void updateData(List<DoctorSimpleInfo> messageLists){
        this.doctorLists = messageLists;
        notifyDataSetChanged();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        CircleImageView profilePic;
        LinearLayout rootLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.email);
            profilePic = itemView.findViewById(R.id.profilePics);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row_list_add,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        DoctorSimpleInfo list = doctorLists.get(position);
        if (list.getProfilePic()!= null && !list.getProfilePic().isEmpty()){
            Glide.with(context).load(list.getProfilePic()).into(holder.profilePic);
        }

        holder.email.setText(list.getName());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tag",list.toString());
                Intent intent = new Intent(context, ChatMessage.class);
                intent.putExtra("email",list.getEmail());
                intent.putExtra("name",list.getName());
                intent.putExtra("profilePic",list.getProfilePic());
                intent.putExtra("chatId","");
                intent.putExtra("isLocked",true);
                String currentTimestampString = String.valueOf(System.currentTimeMillis());
//               databaseReference.child("chat").child("ist.getChatId()").child("lastTime").setValue(currentTimestampString);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return doctorLists.size();
    }
}
