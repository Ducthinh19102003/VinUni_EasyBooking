package com.example.myapplication.ProfessorSetAvailableTimeSlots;


import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;


public class HourSlotUtils {
    public static ArrayList<Timestamp> profAvailableSlots;
    public static ArrayList<Timestamp> defaultHours(){
        ArrayList<Timestamp> defaultHours = new ArrayList<>();
        defaultHours.add(new Timestamp(32400,0));
        defaultHours.add(new Timestamp(34200,0));
        defaultHours.add(new Timestamp(36000,0));
        defaultHours.add(new Timestamp(37800,0));
        defaultHours.add(new Timestamp(39600,0));
        defaultHours.add(new Timestamp(46800,0));
        defaultHours.add(new Timestamp(48600,0));
        defaultHours.add(new Timestamp(50400,0));
        defaultHours.add(new Timestamp(52200,0));
        defaultHours.add(new Timestamp(54000,0));
        defaultHours.add(new Timestamp(55800,0));
        defaultHours.add(new Timestamp(57600,0));
        ZoneOffset zone = ZoneOffset.of("+07:00");
        for (int i = 0; i<defaultHours.size(); i++){
            long day = CalendarUtils.selectedDate.atStartOfDay().toEpochSecond(zone);
            long hour = defaultHours.get(i).getSeconds();
            defaultHours.set(i, new Timestamp(day+hour, 0));
        }
        return defaultHours;
    }

    public static void clearMySchedule() {
        ZoneOffset zone = ZoneOffset.of("+07:00");
        profAvailableSlots.removeIf(hour -> hour.compareTo(new Timestamp(LocalDate.now().atStartOfDay().toEpochSecond(zone), 0)) < 0);
    }
}
