package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingProcess.SelectDate;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    ArrayList<String> professors = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();

    RoomInfo currentRoom;
    ProgressBar progressBar;

    RecyclerView.LayoutManager layoutManager;
    Timestamp startTimestamp, endTimestamp;
    String date, startTime, endTime, location;

    String selectedRoom;

    FirebaseFirestore fstore;
    EventInfo new_event;
    public static ArrayList<EventInfo> evlst;
    Spinner roomSpinner;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_booking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setStartTimeBtn = findViewById(R.id.startTimeID);
        setEndTimeBtn = findViewById(R.id.endTimeID);

        startTimeSelected = findViewById(R.id.startTimeID);
        endTimeSelected = findViewById(R.id.endTimeID);

        fstore = FirebaseFirestore.getInstance();

        makeAppointmentBtn = findViewById(R.id.setappoinment);

        setDateBtn = findViewById(R.id.setDate);
        setStartTimeBtn = findViewById(R.id.startTimeID);
        setEndTimeBtn = findViewById(R.id.endTimeID);

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

                Calendar min_date_c = Calendar.getInstance();
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
                timePickerDialog = TimePickerDialog.newInstance(RoomBookingActivity.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),false );
                timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Set Time");
                timePickerDialog.setMinTime(9, 0,0);
                timePickerDialog.setMaxTime(21,0,0);

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

            }
        });
    }
    private String onItemSelectedHandler(AdapterView<?> adapterView, View view, int position, long id) {
        Adapter adapter = adapterView.getAdapter();
        String selectedRoom = (String) adapter.getItem(position);

        Toast.makeText(getApplicationContext(), "Selected room: " + selectedRoom ,Toast.LENGTH_SHORT).show();
        return selectedRoom;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        Date chosenDate = calendar.getTime();

        String pattern = "EEEE, dd/MM";
        DateFormat df = new SimpleDateFormat(pattern);

        date = df.format(chosenDate);
        Toast.makeText(RoomBookingActivity.this, date, Toast.LENGTH_LONG).show();
        dateSelected.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }
}