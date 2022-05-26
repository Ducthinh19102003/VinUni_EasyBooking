package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.ProfessorSetAvailableTimeSlots.WeekViewActivity;
import com.example.myapplication.Fragments.Calendar.EventListViewActivity;
import com.google.firebase.Timestamp;

public class HomePageProfessor extends AppCompatActivity {
    //Need to pass professor name in for further activities
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_professor);
    }

    public void toSetAvailableTimeSlot(View view) {
        Intent k = new Intent(this, WeekViewActivity.class);
        startActivity(k);
    }

    public void toViewEvent(View view) {
        Intent k = new Intent(this, EventListViewActivity.class);
        startActivity(k);
    }
    public void toSetDummyEvent(View view) {
//       memberJoinEvent("pv7cQBcJqqd4FaWrLM8qxe16mis1",
//               "SCAjKS04hBKsCHFgZ37s");
//        EventInfo dummyEvent = new EventInfo("GutdziKpOWV44uZ6aSEeQqhwfVc2", new Timestamp(1653876000, 0), new Timestamp(1653879600, 0));
//        eventToDatabase(dummyEvent);
        RoomInfo.roomBooking("A101", new EventInfo("GutdziKpOWV44uZ6aSEeQqhwfVc2", new Timestamp(1653886000, 0), new Timestamp(1653889600, 0)));
    }
}