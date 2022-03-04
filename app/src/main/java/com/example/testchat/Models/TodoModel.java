package com.example.testchat.Models;

public class TodoModel {
    private String id, title, description,category  , createdAt ,date;
    private boolean finished;
    public TodoModel( String title, String description, String category ,String date) {

        this.title = title;
        this.description = description;
        this.category = category;
        this.date =date;
    }

    public String getCategory() {
        return category;
    }

    public boolean getFinished() {
        return finished;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}