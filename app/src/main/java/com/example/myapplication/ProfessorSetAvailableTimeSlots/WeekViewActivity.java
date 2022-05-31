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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.myapplication.BookingProcess.SelectDate.evlst;
import static com.example.myapplication.EventInfo.checkConflict;
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
    public static ArrayList<String> daysWithAvailableTimeSlots;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection(Login.userType).document(Login.userID);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("WeekViewActivity", "Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CalendarFragment.eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();
        HourSlotUtils.profAvailableSlots = Login.currentProfessor.getAvailableTimeSlots();
        HourSlotUtils.clearMySchedule();
        daysWithAvailableTimeSlots = new ArrayList<>();
        for (Timestamp timeslot : profAvailableSlots) daysWithAvailableTimeSlots.add((new SimpleDateFormat("dd/MM/yyyy")).format(timeslot.toDate()));
        setWeekView();
        setHourSlotsView();
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
        else {
            if (!checkConflict(hour, evlst, getApplicationContext())) HourSlotUtils.profAvailableSlots.add(hour);
        }
        daysWithAvailableTimeSlots = new ArrayList<>();
        for (Timestamp timeslot : profAvailableSlots) daysWithAvailableTimeSlots.add((new SimpleDateFormat("dd/MM/yyyy")).format(timeslot.toDate()));
        setWeekView();
        setHourSlotsView();
    }

    public void submitAvailableTimeSlots(View view) {
        Collections.sort(HourSlotUtils.profAvailableSlots);
        docRef.update("availableTimeSlots", profAvailableSlots);
        Login.currentProfessor.setAvailableTimeSlots(profAvailableSlots);
        Intent k = new Intent(this, HomePage.class);
        startActivity(k);
    }
}