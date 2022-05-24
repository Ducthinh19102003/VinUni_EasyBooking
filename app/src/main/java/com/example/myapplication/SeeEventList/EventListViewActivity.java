package com.example.myapplication.SeeEventList;

import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.daysInWeekArray;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.monthYearFromDate;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils.selectedDate;
import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.defaultHours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarAdapter;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.CalendarUtils;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotAdapter;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventListViewActivity extends AppCompatActivity implements EventSlotAdapter.OnItemListener, CalendarAdapter.OnItemListener {
    private RecyclerView eventRecyclerView, calendarRecyclerView;
    private TextView monthYearText;
    private static final String TAG  = "WeekViewActivity";
    String professorID = "GutdziKpOWV44uZ6aSEeQqhwfVc2";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Professors").document(professorID);
    private static ArrayList<EventInfo> eventInfoArrayList = new ArrayList<>();
    public static HashMap<String, ArrayList<EventInfo>> eventInfoHashMap = new HashMap<String, ArrayList<EventInfo>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();
        setWeekView();
        //Retrieve the array of event ids
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> eventIDArrayList = (ArrayList<String>) document.get("events");
                        //Retrieve the event arraylist from the ids
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (String doc : eventIDArrayList) {
                            Log.d("Debug", doc.trim()); //yes, a bug was here
                            tasks.add(db.collection("Events").document(doc.trim()).get());
                        }
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> list) {
                                //Do what you need to do with your list
                                Log.d("debug", list.toString());
                                //create dummy list, put everything there, check later
                                ArrayList<EventInfo> eventInfoDummyList = new ArrayList<>();
                                for (Object object : list) {

                                    EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                    eventInfoDummyList.add(fm);
                                }
                                //look into the dummy list
                                for (EventInfo event :eventInfoDummyList){
                                    if (event == null){
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(null));
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else if (event.getStartTime().compareTo(Timestamp.now()) < 0){
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(event));
                                        db.collection("Events").document(fmID).delete();
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else eventInfoArrayList.add(event);
                                }
                                eventInfoHashMap = eventInfoArrayListToHashMap(eventInfoArrayList);
                                setEventView();
                            }

                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

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
        EventSlotAdapter eventSlotAdapter = new EventSlotAdapter(passing, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setAdapter(eventSlotAdapter);
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventRecyclerView = findViewById(R.id.EventRecyclerView);
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
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
}