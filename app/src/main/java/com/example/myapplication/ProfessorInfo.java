package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class ProfessorInfo {
    private String email;
    private String password;
    private String name;
    private String subject;
    private ArrayList<String> Events;
    private ArrayList<Timestamp> availableTimeSlots;
    private String UID;

    public ProfessorInfo() {

    }

    public ProfessorInfo(String email, String password, String name, String subject, String UID) {
        this.name = name;
        this.password = password;
        this.subject = subject;
        this.email = email;
        this.Events = new ArrayList<>();
        this.availableTimeSlots = new ArrayList<>();
        this.UID = UID;
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

    public ArrayList<String> getEvents(){
        return this.Events;
    }

    public ArrayList<Timestamp> getAvailableTimeSlots(){
        return this.availableTimeSlots;
    }

    public void setEvents(ArrayList<String> eventsList){ this.Events = eventsList; }

    public void setAvailableTimeSlots(ArrayList<Timestamp> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}

