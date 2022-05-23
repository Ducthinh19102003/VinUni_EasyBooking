package com.example.myapplication.CreateCustomEvent;

import com.example.myapplication.EventInfo;
import com.google.firebase.Timestamp;

public class CreateCustomEventTest {
    static String hostID = "GutdziKpOWV44uZ6aSEeQqhwfVc2";
    static Timestamp startTime = new Timestamp(32400,0);
    static Timestamp endTime = new Timestamp(32400,0);
    public static void test(){
        EventInfo createdEvent = new EventInfo(hostID, startTime, endTime);
        createdEvent.setNote("pikapika");
        createdEvent.addMembers("pv7cQBcJqqd4FaWrLM8qxe16mis1");
        EventInfo.eventToDatabase(createdEvent);
    }
}
