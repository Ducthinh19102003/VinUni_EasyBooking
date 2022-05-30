package com.example.myapplication.ProfessorSetAvailableTimeSlots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.daysInWeekArray;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.monthYearFromDate;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.defaultHours;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.profAvailableSlots;

import com.example.myapplication.EventInfo;
import com.example.myapplication.Fragments.Calendar.CalendarFragment;
import com.example.myapplication.Fragments.Home.HomeFragment;
import com.example.myapplication.HomePage;
import com.example.myapplication.Login;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, HourSlotAdapter.OnItemListener2
{
    private static final String TAG  = "WeekViewActivity";
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private RecyclerView hourSlotsRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection(Login.userType).document(Login.userID);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("WeekViewActivity", "Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        CalendarFragment.eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();
        setWeekView();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        HourSlotUtils.profAvailableSlots = (ArrayList<Timestamp>) document.get("availableTimeSlots");
                        HourSlotUtils.clearMySchedule();
                        setHourSlotsView();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        hourSlotsRecyclerView = findViewById(R.id.hourSlotsRecyclerView);
    }

    private void setHourSlotsView()
    {
        ArrayList<Timestamp> hours = defaultHours();
        HourSlotAdapter hourSlotAdapter = new HourSlotAdapter(hours, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        hourSlotsRecyclerView.setLayoutManager(layoutManager);
        hourSlotsRecyclerView.setAdapter(hourSlotAdapter);
    }

    private void setWeekView()
    {
        Log.d("WeekViewActivity", "setWeekView");
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
        setHourSlotsView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
        setHourSlotsView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
        setHourSlotsView();
    }

    @Override
    public void onItemClick(int position, Timestamp hour) {
        Log.d("HourSlotClicked", hour.toString());
        if (HourSlotUtils.profAvailableSlots.contains(hour))
            HourSlotUtils.profAvailableSlots.remove(hour);
        else HourSlotUtils.profAvailableSlots.add(hour);
        setHourSlotsView();
    }

    public void submitAvailableTimeSlots(View view) {
        Collections.sort(HourSlotUtils.profAvailableSlots);
        docRef.update("availableTimeSlots", profAvailableSlots);
        Intent k = new Intent(this, HomePage.class);
        startActivity(k);
    }
}