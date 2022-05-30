package com.example.myapplication.Fragments.Calendar;

import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.daysInWeekArray;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.monthYearFromDate;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.defaultHours;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.profAvailableSlots;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingProcess.SelectDate;
import com.example.myapplication.EventInfo;
import com.example.myapplication.Fragments.Home.EventAdapter;
import com.example.myapplication.Fragments.Home.HomeFragment;
import com.example.myapplication.Login;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarAdapter;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotAdapter;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCalendarBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CalendarFragment extends Fragment implements EventSlotAdapter.OnItemListener, CalendarAdapter.OnItemListener {

    private FragmentCalendarBinding binding;
    private RecyclerView eventRecyclerView, calendarRecyclerView;
    private TextView monthYearText;
    private static final String TAG  = "WeekViewActivity";
    private static ArrayList<EventInfo> eventInfoArrayList = new ArrayList<>();
    public static HashMap<String, ArrayList<EventInfo>> eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();
        retrieveData();
    return root;
    }

    private HashMap<String, ArrayList<EventInfo>> eventInfoArrayListToHashMap(ArrayList<EventInfo> eventInfoArrayList) {
        HashMap<String, ArrayList<EventInfo>> eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        for (EventInfo event: eventInfoArrayList){
            Date date = event.getStartTime().toDate();
            String dateString = df.format(date);
            if (eventInfoHashMap.containsKey(dateString)) eventInfoHashMap.get(dateString).add(event);
            else {
                eventInfoHashMap.put(dateString, new ArrayList<EventInfo>());
                eventInfoHashMap.get(dateString).add(event);
            }
        }
        return eventInfoHashMap;
    }

    private void setEventView() {
        String selectedDateString = CalendarUtils.selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        ArrayList<EventInfo> passing = new ArrayList<>();
        if (eventInfoHashMap.containsKey(selectedDateString)) passing = eventInfoHashMap.get(selectedDateString);
        if (CalendarUtils.selectedDate.compareTo(LocalDate.now()) < 0) passing = new ArrayList<>();
        EventAdapter eventAdapter = new EventAdapter(passing);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setAdapter(eventAdapter);
    }

    private void initWidgets()
    {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        calendarRecyclerView = binding.calendarRecyclerView;
        monthYearText = binding.monthYearTV;
        eventRecyclerView = binding.EventRecyclerView;
        Button nextWeekButton = binding.nextWeekButton;
        Button prevWeekButton = binding.previousWeekButton;
        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWeekAction(view);
            }
        });
        prevWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWeekAction(view);
            }
        });
        eventRecyclerView.addItemDecoration(itemDecoration);
        eventInfoArrayList = new ArrayList<>();
        eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();
    }

    @Override
    public void onItemClick(int position, EventInfo event) {

    }

    private void setWeekView()
    {
        Log.d("WeekViewActivity", "setWeekView");
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
        setEventView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
        setEventView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
        setEventView();
    }
    public void retrieveData() {
        FirebaseFirestore.getInstance().collection(Login.userType).document(Login.userID).collection("Events")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Login", "Getting document");
                        EventInfo event = document.toObject(EventInfo.class);
                        if (event.getStartTime().compareTo(Timestamp.now()) < 0) {
                            document.getReference().delete();
                        }
                        else {
                            Log.d("Login", "event is " + event);
                            eventInfoArrayList.add(event);
                        }
                    }
                    Collections.sort(eventInfoArrayList);

                    if(eventInfoArrayList.size() > 0) {
                        SelectDate.evlst = eventInfoArrayList;
                        Log.d("Debug", eventInfoArrayList + "");
                        eventInfoHashMap = eventInfoArrayListToHashMap(eventInfoArrayList);
                        setWeekView();
                        setEventView();
                    }
                    else setWeekView();
                } else {
                    Log.d("Login", "Error getting documents: ", task.getException());
                    setWeekView();
                }
            }
        });
    }

}