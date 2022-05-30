package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class ProfessorInfo implements Parcelable {
    private String email;
    private String password;
    private String name;
    private String subject;
    private ArrayList<String> Events;
    private ArrayList<Timestamp> availableTimeSlots;
    private String UID;
    private String location;
    private String researchInterest;

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
        this.researchInterest = null;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.name);
        dest.writeString(this.subject);
        dest.writeStringList(this.Events);
        dest.writeTypedList(this.availableTimeSlots);
        dest.writeString(this.UID);
        dest.writeString(this.location);
    }

    public void readFromParcel(Parcel source) {
        this.email = source.readString();
        this.password = source.readString();
        this.name = source.readString();
        this.subject = source.readString();
        this.Events = source.createStringArrayList();
        this.availableTimeSlots = source.createTypedArrayList(Timestamp.CREATOR);
        this.UID = source.readString();
        this.location = source.readString();
    }

    protected ProfessorInfo(Parcel in) {
        this.email = in.readString();
        this.password = in.readString();
        this.name = in.readString();
        this.subject = in.readString();
        this.Events = in.createStringArrayList();
        this.availableTimeSlots = in.createTypedArrayList(Timestamp.CREATOR);
        this.UID = in.readString();
        this.location = in.readString();
    }

    public static final Parcelable.Creator<ProfessorInfo> CREATOR = new Parcelable.Creator<ProfessorInfo>() {
        @Override
        public ProfessorInfo createFromParcel(Parcel source) {
            return new ProfessorInfo(source);
        }

        @Override
        public ProfessorInfo[] newArray(int size) {
            return new ProfessorInfo[size];
        }
    };

    public String getResearchInterest() {
        return researchInterest;
    }

    public void setResearchInterest(String researchInterest) {
        this.researchInterest = researchInterest;
    }
}

