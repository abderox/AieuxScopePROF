package com.example.testchat.Models;

public class ResponseTask {
    TodoModel[] todos;

    public TodoModel[] getTodos() {
        return todos;
    }

    public void setTodos(TodoModel[] todos) {
        this.todos = todos;
    }

    public ResponseTask(TodoModel[] todos) {
        this.todos = todos;
    }
}
