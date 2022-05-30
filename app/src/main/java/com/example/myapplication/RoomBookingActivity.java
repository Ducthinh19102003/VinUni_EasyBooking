package com.example.myapplication;

import static com.example.myapplication.Login.currentProfessor;
import static com.example.myapplication.Login.currentStudent;
import static com.example.myapplication.Login.professorList;
import static com.example.myapplication.Login.studentList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;


import com.example.myapplication.BookingProcess.SelectDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


import com.google.firebase.Timestamp;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class RoomBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    MaterialCardView setDateBtn, setStartTimeBtn, setEndTimeBtn;
    AppCompatButton makeAppointmentBtn;
    EditText meetingTitle, meetingParticipants, meetingNote;
    TextView dateSelected, startTimeSelected, endTimeSelected;

    int timetype;

    ArrayList<String> professors = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();

    RoomInfo currentRoom;
    ProgressBar progressBar;

    Timestamp startTimestamp, endTimestamp;
    String date, selectedRoom, start, end;


    FirebaseFirestore fstore;
    EventInfo new_event;
    Spinner roomSpinner;
    Calendar calendar;
    String host;
    ArrayList<EventInfo> roomEvents;
    boolean isToday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_booking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setStartTimeBtn = findViewById(R.id.startTimeID);
        setEndTimeBtn = findViewById(R.id.endTimeID);
        setDateBtn = findViewById(R.id.MeetingDateID);

        dateSelected = findViewById(R.id.datetxt);
        startTimeSelected = findViewById(R.id.startTimetxt);
        endTimeSelected = findViewById(R.id.endTimeTxt);

        meetingTitle = findViewById(R.id.meetingNameID);
        meetingParticipants = findViewById(R.id.ParticipantsID);
        meetingNote = findViewById(R.id.NoteID);

        fstore = FirebaseFirestore.getInstance();
        roomEvents = new ArrayList<>();

        makeAppointmentBtn = findViewById(R.id.AppointBtnID);

        setDateBtn = findViewById(R.id.MeetingDateID);
        setStartTimeBtn = findViewById(R.id.startTimeID);
        setEndTimeBtn = findViewById(R.id.endTimeID);
        progressBar = findViewById(R.id.progress);

        calendar = Calendar.getInstance();
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(RoomBookingActivity.this,
                        calendar.get(Calendar.YEAR), // Initial year selection
                        calendar.get(Calendar.MONTH), // Initial month selection
                        calendar.get(Calendar.DAY_OF_MONTH)  // Inital day selection
                );
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Set Date");
                Log.d("min date", Calendar.HOUR_OF_DAY + "");
                Calendar min_date_c = Calendar.getInstance();
                if (min_date_c.get(Calendar.HOUR_OF_DAY) >= 21) {
                    min_date_c.add(Calendar.DATE, 1);
                }
                datePickerDialog.setMinDate(min_date_c);
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(RoomBookingActivity.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
        setStartTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                timetype = 0;
                timePickerDialog = TimePickerDialog.newInstance(RoomBookingActivity.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.setThemeDark(false);

                timePickerDialog.setTitle("Set Time");
                Log.d("today", isToday + "");
                if (isToday == true)
                    timePickerDialog.setMinTime(calendar.get(Calendar.HOUR) + 1, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                else timePickerDialog.setMinTime(9, 0, 0);
                timePickerDialog.setMaxTime(21, 0, 0);

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(RoomBookingActivity.this, "Timepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
            }
        });

        setEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetype = 1;
                timePickerDialog = TimePickerDialog.newInstance(RoomBookingActivity.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.setThemeDark(false);


                if (isToday == true)
                    timePickerDialog.setMinTime(calendar.get(Calendar.HOUR) + 1, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                else timePickerDialog.setMinTime(9, 0, 0);
                timePickerDialog.setMaxTime(21, 0, 0);

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(RoomBookingActivity.this, "Timepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
            }
        });


        makeAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = meetingTitle.getText().toString().trim();
                String participants = meetingParticipants.getText().toString().trim();
                ArrayList<String> participantList = new ArrayList<>();
                if (Login.portal == 1) {
                    participantList.add(Login.currentStudent.getEmail());
                    host = currentStudent.getName();
                } else {
                    participantList.add(Login.currentProfessor.getEmail());
                    host = currentProfessor.getName();
                }

                String note = meetingNote.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    meetingTitle.setError("Add meeting title!");
                    meetingTitle.requestFocus();
                    return;
                }
                ArrayList<String> check = new ArrayList<>();
                if (!TextUtils.isEmpty(participants)) {
                    participantList.addAll(Arrays.asList(participants.split("\\s*,\\s*")));
                }
                for (String str : participantList) {
                    if (professorList.contains(str)) {
                        professors.add(str);
                    } else if (studentList.contains(str)) {
                        students.add(str);
                    } else check.add(str);
                }
                if (check.size() > 0) {
                    Toast.makeText(RoomBookingActivity.this, "Unavailable record(s): " + check, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (start == null) {
                    Toast.makeText(RoomBookingActivity.this, "Start time is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (end == null) {
                    Toast.makeText(RoomBookingActivity.this, "End time is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    meetingNote.setError("Add meeting note!");
                    meetingNote.requestFocus();
                    return;
                }
                if (startTimestamp.compareTo(endTimestamp) >= 0) {
                    Toast.makeText(RoomBookingActivity.this, "Start time must be sooner than end time!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                retrieveRoomData();

                new_event = new EventInfo(host, participantList, startTimestamp, endTimestamp, note, title, selectedRoom);
                ArrayList<String> roomConflict = checkRoomConflict(new_event, roomEvents);

                progressBar.setVisibility(View.GONE);
                if (roomConflict.size() > 0) {
                    Log.d("roomConflict", roomConflict + "1");
                    String message = "Choosen timeslot conflict with followings events: ";
                    for (int i = 0; i < roomConflict.size(); i++) {

                        message += "Event " + (i + 1) + ": " + roomConflict.get(i) + "\n";
                    }
                    Toast.makeText(RoomBookingActivity.this, message, Toast.LENGTH_SHORT).show();
                    roomEvents = new ArrayList<>();
                    return;
                }
                ArrayList<String> userConflict = checkRoomConflict(new_event, SelectDate.evlst);
                if (userConflict.size() > 0) {
                    Toast.makeText(RoomBookingActivity.this, "Choosen timeslot conflict with " + userConflict, Toast.LENGTH_SHORT).show();
                    return;
                }
                EventToFireBase();
                startActivity(new Intent(RoomBookingActivity.this, HomePage.class));
            }
        });

        //this part is dedicated  to the spinner
        roomSpinner = findViewById(R.id.roomSpinner);
        String[] rooms = {"A101", "A102", "A103", "A104", "A105", "A106"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                rooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roomSpinner.setAdapter(adapter);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = onItemSelectedHandler(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(RoomBookingActivity.this, "Please choose a room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String onItemSelectedHandler(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        String selectedRoom = (String) adapter.getItem(position);

        Toast.makeText(getApplicationContext(), "Selected room: " + selectedRoom, Toast.LENGTH_SHORT).show();
        return selectedRoom;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        Date chosenDate = calendar.getTime();


        if (year == Calendar.getInstance().get(Calendar.YEAR) && monthOfYear == Calendar.getInstance().get(Calendar.MONTH) && dayOfMonth == Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            isToday = true;
        Log.d("RoomBooking", isToday + "");


        String pattern = "EEEE, dd/MM";
        DateFormat df = new SimpleDateFormat(pattern);

        date = df.format(chosenDate);
        Toast.makeText(RoomBookingActivity.this, date, Toast.LENGTH_LONG).show();
        dateSelected.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String getMinute = minute < 10 ? "0" + minute : minute + "";

        calendar.set(Calendar.HOUR, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        if (timetype == 0) {
            startTimestamp = new Timestamp(calendar.getTime());
            startTimeSelected.setText(hourOfDay + ":" + getMinute);
            start = hourOfDay + ":" + getMinute;
        } else {
            endTimestamp = new Timestamp(calendar.getTime());
            endTimeSelected.setText(hourOfDay + ":" + getMinute);
            end = hourOfDay + ":" + getMinute;
        }
    }
    public void retrieveRoomData() {
        fstore.collection("Rooms").document(selectedRoom).collection("Events")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("RoomBooking", document + "");
                                EventInfo event = document.toObject(EventInfo.class);
                                if (event.getStartTime().compareTo(Timestamp.now()) < 0) {
                                    document.getReference().delete();
                                } else {
                                    Log.d("Login", "event is " + event);
                                    roomEvents.add(event);
                                }
                            }
                            Collections.sort(roomEvents);
                        } else {
                            Log.d("Get Events", "Error getting events: ", task.getException());
                        }
                    }
                });
    }

    public ArrayList<String> checkRoomConflict(EventInfo newEvent, ArrayList<EventInfo> eventInfoArrayList){
        //loop thru everything because it's the only way. Like bruh.
        //May use binary search later. But there are 2 cases so I'm not sure.

        DateFormat df = new SimpleDateFormat("HH:mm");
        ArrayList<String> conflictEvents = new ArrayList<>();
        for (int i = 0; i < eventInfoArrayList.size(); i++){
            //Conflict cases
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0
                    && newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getEndTime()) < 0)
                //Event start as another is happening
                conflictEvents.add(df.format(eventInfoArrayList.get(i).getStartTime().toDate()) + " - " +  df.format(eventInfoArrayList.get(i).getEndTime().toDate()));
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) < 0
                    && newEvent.getEndTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0)
                //Another event would start as this event is happening
                conflictEvents.add(df.format(eventInfoArrayList.get(i).getStartTime().toDate()) + " - " + df.format(eventInfoArrayList.get(i).getEndTime().toDate()));
        }
        Log.d("roomConflict", "method: " + conflictEvents);
        return conflictEvents;
    }

    public void EventToFireBase() {
        fstore.collection("Rooms").document(selectedRoom).collection("Events")
                .add(new_event);

        for(String s: students) {
            Query query = fstore.collection("Students").whereEqualTo("email", s);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ID = document.getId();
                        fstore.collection("Students").document(ID).collection("Events")
                                .add(new_event);
                    }
                }
            });
        }
        for(String s: professors) {
            Query query = fstore.collection("Professors").whereEqualTo("email", s);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ID = document.getId();
                        fstore.collection("Professors").document(ID).collection("Events")
                                .add(new_event);
                    }
                }
            });
        }
    }
    public ArrayList<String> checkTimeConflict(EventInfo newEvent, ArrayList<EventInfo> eventInfoArrayList){
        //loop thru everything because it's the only way. Like bruh.
        //May use binary search later. But there are 2 cases so I'm not sure.
        ArrayList<String> conflictEvents = new ArrayList<>();
        for (int i = 0; i < eventInfoArrayList.size(); i++){
            //Conflict cases
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0
                    && newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getEndTime()) < 0)
                //Event start as another is happening
                conflictEvents.add(eventInfoArrayList.get(i).getMeetingName().toUpperCase());
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) < 0
                    && newEvent.getEndTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0)
                //Another event would start as this event is happening
                conflictEvents.add(eventInfoArrayList.get(i).getMeetingName().toUpperCase());
        }
        return conflictEvents;
    }
}