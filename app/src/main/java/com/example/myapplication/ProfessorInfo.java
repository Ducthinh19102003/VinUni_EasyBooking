package com.example.myapplication;
public class ProfessorInfo {
    private String email;
    private String password;
    private String name;
    private String subject;


    public ProfessorInfo() {

    }

    public ProfessorInfo(String email, String password, String name, String subject) {
        this.name = name;
        this.password = password;
        this.subject = subject;
        this.email = email;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

