package com.example.testchat.Models;

public class Person extends  User{
    private String fullName;
    private String imagePath;
    private long phone;


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFullName() {
        return fullName;
    }

    public Person(String email, String password, boolean rememberMe, String fullName, String imagePath, long phone) {
        super(email, password);
        this.fullName = fullName;
        this.imagePath = imagePath;
        this.phone = phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}
