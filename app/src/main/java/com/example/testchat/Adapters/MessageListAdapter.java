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
import com.example.testchat.Models.MessageList;
import com.example.testchat.R;
import com.example.testchat.Views.ChatMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {
    List<MessageList> messageLists;
    final  Context context;

    public MessageListAdapter(List<MessageList> messageLists, Context context) {
        this.messageLists = messageLists;
        this.context = context;
    }
    public  void updateData(List<MessageList> messageLists){
        this.messageLists = messageLists;
        notifyDataSetChanged();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMsg, unseenMsg, time;
        CircleImageView profilePic;
        LinearLayout rootLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            lastMsg = itemView.findViewById(R.id.latMsg);
            time = itemView.findViewById(R.id.date);
            unseenMsg = itemView.findViewById(R.id.unseenMsg);
            profilePic = itemView.findViewById(R.id.profilePics);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row_list,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


            MessageList list = messageLists.get(position);
            holder.time.setText(list.getTime());
            holder.lastMsg.setText(list.getLastMessage());
        if (list.getProfilePic()!= null && !list.getProfilePic().isEmpty()){
                Glide.with(context).load(list.getProfilePic()).into(holder.profilePic);
            }

            holder.name.setText(list.getName());
            holder.lastMsg.setText(list.getLastMessage());
            holder.time.setText(list.getTime());
            if (list.getUnseenMessage() == 0) holder.unseenMsg.setVisibility(View.GONE);
            else{
                holder.unseenMsg.setText(String.valueOf(list.getUnseenMessage()));
                holder.unseenMsg.setVisibility(View.VISIBLE);
            }
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("tag",list.toString());
                    holder.unseenMsg.setVisibility(View.GONE);
                    Intent intent = new Intent(context, ChatMessage.class);
                    intent.putExtra("email",list.getEmail());
                    intent.putExtra("name",list.getName());
                    intent.putExtra("profilePic",list.getProfilePic());
                    intent.putExtra("chatId",list.getChatId());
                    context.startActivity(intent);
                }
            });
    }


    @Override
    public int getItemCount() {
        return messageLists.size();
    }
}