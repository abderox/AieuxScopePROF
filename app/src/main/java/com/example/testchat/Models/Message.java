package com.example.testchat.Models;

public class Message {
    String email;
    String message;
    String date;
    String time;
    public static String staDate ="12-01-2000";

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate() {
        if (!this.date.equals(staDate)) {
            this.checkDate = staDate;
            staDate = date;
        }
        else this.checkDate = date;

    }

    String checkDate;

    public Message(String email, String message, String date, String time) {
        this.email = email;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
