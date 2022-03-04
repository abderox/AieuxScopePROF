package com.example.testchat.Models;

public class Todo {
    String todo;
    boolean success=true;

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Todo(String todo, boolean success) {
        this.todo = todo;
        this.success = success;
    }
}
