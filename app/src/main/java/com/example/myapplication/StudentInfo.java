package com.example.myapplication;
public class StudentInfo {
    private String email;
    private String password;
    private String name;
    private String ID;
    private String hobby;

    public StudentInfo() {

    }

    public StudentInfo(String email, String password, String name, String ID) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.ID = ID;
        this.hobby = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
