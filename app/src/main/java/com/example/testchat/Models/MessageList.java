package com.example.testchat.Models;

import androidx.annotation.NonNull;

public class MessageList {
    String name;
    String email;
    String lastMessage;
    String profilePic;
    String time;
    String chatId;

    public MessageList(String name, String email, String lastMessage, String profilePic, String time, int unseenMessage, String chatId) {
        this.name = name;
        this.email = email;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.time = time;
        this.chatId = chatId;
        this.unseenMessage = unseenMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getUnseenMessage() {
        return unseenMessage;
    }

    public void setUnseenMessage(int unseenMessage) {
        this.unseenMessage = unseenMessage;
    }

    int unseenMessage;

    @Override
    public String toString() {
        return "MessageList{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", time='" + time + '\'' +
                ", chatId='" + chatId + '\'' +
                ", unseenMessage=" + unseenMessage +
                '}';
    }
}
