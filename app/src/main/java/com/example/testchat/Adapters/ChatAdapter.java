package com.example.testchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testchat.Models.Message;
import com.example.testchat.R;

import java.util.List;

public class ChatAdapter extends  RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    List<Message> messages;
    final Context context;
    final String userEmail;
    public static String date = "12-01-2000";

    public ChatAdapter(List<Message> messages, Context context, String userEmail) {
        this.messages = messages;
        this.context = context;
        this.userEmail = userEmail;
    }
    @SuppressLint("NotifyDataSetChanged")
    public  void updateData(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }
    public void clear() {
        int size = this.messages.size();
        this.messages.clear();
        notifyItemRangeRemoved(0, size);
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout opoLayout,myLayout,dateChatLayout;
        TextView opoMsg,myMsg,dateChat;
        TextView opoDate,myDate;
        MyViewHolder(View itemView) {
            super(itemView);
            opoLayout = itemView.findViewById(R.id.opoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            dateChatLayout = itemView.findViewById(R.id.dateChatLayout);
            opoMsg = itemView.findViewById(R.id.opoMsg);
            myMsg = itemView.findViewById(R.id.myMsg);
            dateChat = itemView.findViewById(R.id.dateChat);
            opoDate = itemView.findViewById(R.id.opoDate);
            myDate = itemView.findViewById(R.id.myDate);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messages.get(position);
        if (!message.getDate().equals(message.getCheckDate())){
            holder.dateChat.setText(message.getDate());
            holder.dateChatLayout.setVisibility(View.VISIBLE);
        }else {
            holder.dateChatLayout.setVisibility(View.GONE);
        }
        if (message.getEmail().equals(userEmail)){
            holder.myMsg.setText(message.getMessage());
            holder.myDate.setText(message.getTime());
            holder.opoLayout.setVisibility(View.GONE);
            holder.myLayout.setVisibility(View.VISIBLE);
        }else{
            holder.opoMsg.setText(message.getMessage());
            holder.opoDate.setText(message.getTime());
            holder.myLayout.setVisibility(View.GONE);
            holder.opoLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
