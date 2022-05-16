package com.example.myapplication;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class EventInfo {

    private String host;
    private ArrayList<String> members;
    private Timestamp startTime;
    private Timestamp endTime;

    public EventInfo() {

    }

    public EventInfo(String ID, String host, Timestamp startTime, Timestamp endTime) {
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
        this.members = new ArrayList<>();
    }


    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
