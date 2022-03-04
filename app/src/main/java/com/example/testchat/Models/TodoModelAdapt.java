package com.example.testchat.Models;

public class TodoModelAdapt {

    private String  title, description,category  , day ,date , month;

    public TodoModelAdapt(String title, String description, String category, String day, String date, String month) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.day = day;
        this.date = date;
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }
    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}