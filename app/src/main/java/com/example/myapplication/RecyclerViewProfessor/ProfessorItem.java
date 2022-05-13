package com.example.myapplication.RecyclerViewProfessor;
public class ProfessorItem {
    private String email;
    private String name;
    private String subject;
    private int resourceID;

    public ProfessorItem(String email, String name, String subject, int resourceID) {
        this.email = email;
        this.name = name;
        this.subject = subject;
        this.resourceID = resourceID;
    }

    public ProfessorItem() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getResourceID() {
        return resourceID;
    }

    public void setImageUrl(int resourceID) {
        this.resourceID = resourceID;
    }
}
