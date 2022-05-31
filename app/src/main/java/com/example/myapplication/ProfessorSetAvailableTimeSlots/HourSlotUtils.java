package com.example.myapplication.ProfessorSetAvailableTimeSlots;


import com.google.firebase.Timestamp;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class HourSlotUtils {
    public static ArrayList<Timestamp> profAvailableSlots;
    public static ArrayList<Timestamp> defaultHours() {
        ArrayList<Timestamp> defaultHours = new ArrayList<>();
        defaultHours.add(new Timestamp(32400, 0));
        defaultHours.add(new Timestamp(34200, 0));
        defaultHours.add(new Timestamp(36000, 0));
        defaultHours.add(new Timestamp(37800, 0));
        defaultHours.add(new Timestamp(39600, 0));
        defaultHours.add(new Timestamp(46800, 0));
        defaultHours.add(new Timestamp(48600, 0));
        defaultHours.add(new Timestamp(50400, 0));
        defaultHours.add(new Timestamp(52200, 0));
        defaultHours.add(new Timestamp(54000, 0));
        defaultHours.add(new Timestamp(55800, 0));
        defaultHours.add(new Timestamp(57600, 0));
        ZoneOffset zone = ZoneOffset.of("+07:00");
        for (int i = 0; i < defaultHours.size(); i++) {
            long day = CalendarUtils.selectedDate.atStartOfDay().toEpochSecond(zone);
            long hour = defaultHours.get(i).getSeconds();
            defaultHours.set(i, new Timestamp(day + hour, 0));
        }
        return defaultHours;
    }


    public static void clearMySchedule() {
        ZoneOffset zone = ZoneOffset.of("+07:00");
        profAvailableSlots.removeIf(hour -> hour.compareTo(Timestamp.now()) < 0);
    }
    public static HashMap<String, ArrayList<Timestamp>> timestampArrayListToHashMap(ArrayList<Timestamp> timestampArrayList){
        HashMap<String, ArrayList<Timestamp>> timeStampHashMap = new HashMap<String, ArrayList<Timestamp>>();
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        for (Timestamp availableTimeSlot: timestampArrayList){
            Date date = availableTimeSlot.toDate();
            String dateString = df.format(date);
            if (timeStampHashMap.containsKey(dateString)) timeStampHashMap.get(dateString).add(availableTimeSlot);
            else {
                timeStampHashMap.put(dateString, new ArrayList<Timestamp>());
                timeStampHashMap.get(dateString).add(availableTimeSlot);
            }
        }
        return timeStampHashMap;
    }
}