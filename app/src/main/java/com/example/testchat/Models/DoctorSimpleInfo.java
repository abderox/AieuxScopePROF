package com.example.testchat.Models;

public class DoctorSimpleInfo {
    String email;
    String profilePic;
    String name;

    public DoctorSimpleInfo(String email, String profilePic, String name) {
        this.email = email;
        this.profilePic = profilePic;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
