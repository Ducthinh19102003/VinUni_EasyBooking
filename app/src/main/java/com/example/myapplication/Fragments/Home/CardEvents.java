package com.example.myapplication.Fragments.Home;

import com.example.myapplication.EventInfo;

import java.util.ArrayList;

public class CardEvents {
    String date;
    public ArrayList<EventInfo> eventList;

    CardEvents() {

    }

    public CardEvents(String date, ArrayList<EventInfo> eventInfoArrayList) {
        this.date = date;
        this.eventList = eventInfoArrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<EventInfo> getEventInfoArrayList() {
        return eventList;
    }

    public void setEventInfoArrayList(ArrayList<EventInfo> eventInfoArrayList) {
        this.eventList = eventInfoArrayList;
    }
}
