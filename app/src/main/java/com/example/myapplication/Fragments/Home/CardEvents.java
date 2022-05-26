package com.example.myapplication.Fragments.Home;

import com.example.myapplication.EventInfo;

import java.util.ArrayList;

public class CardEvents {
    String date;
    ArrayList<EventInfo> eventInfoArrayList;

    CardEvents() {

    }

    CardEvents(String date, ArrayList<EventInfo> eventInfoArrayList) {
        this.date = date;
        this.eventInfoArrayList = eventInfoArrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<EventInfo> getEventInfoArrayList() {
        return eventInfoArrayList;
    }

    public void setEventInfoArrayList(ArrayList<EventInfo> eventInfoArrayList) {
        this.eventInfoArrayList = eventInfoArrayList;
    }
}
