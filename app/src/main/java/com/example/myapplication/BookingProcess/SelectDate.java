package com.example.myapplication.BookingProcess;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotAdapter;
import com.example.myapplication.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.firebase.Timestamp;

public class SelectDate extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener  {

    DatePickerDialog datePickerDialog ;
    AppCompatButton setDateBtn, setTimeBtn;
    TextView dateSelected, timeSelected;

    RecyclerView timeSlotRecyclerView;
    TimeSlotAdapter timeSlotAdapter;
    RecyclerView.LayoutManager layoutManager;

    public static ArrayList<Timestamp> availableSlots;
    public static String UID_professor;
    ArrayList<Calendar> calendarList;
    HashMap<String, ArrayList<Timestamp>> categorizedTimeslots;
    static String date;
    ArrayList<Timestamp> hours;

    void setTimeSlotRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter(SelectDate.this, hours);
        layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        timeSlotRecyclerView.setLayoutManager(layoutManager);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        calendarList = new ArrayList<Calendar>();
        timeSlotRecyclerView = findViewById(R.id.timeSlotID);

        setCalendarArrays();
        categorizedTimeslots = timestampArrayListToHashMap();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dateSelected = findViewById(R.id.dateDisplay);
        timeSelected = findViewById(R.id.timeDisplay);

        setDateBtn = findViewById(R.id.setDate);
        setTimeBtn = findViewById(R.id.setTime);

        Calendar now = Calendar.getInstance();
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(SelectDate.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH)// Inital day selection
                );
                datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Set Date");

                Calendar min_date_c = Calendar.getInstance();
                datePickerDialog.setMinDate(min_date_c);

                Calendar[] calendarArray = calendarList.toArray(new Calendar[calendarList.size()]);
                datePickerDialog.setSelectableDays(calendarArray);
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(SelectDate.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
    }
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + "0" + (monthOfYear+1) + "/"+ year;
        Toast.makeText(SelectDate.this, date, Toast.LENGTH_LONG).show();
        dateSelected.setText(date);
        hours = categorizedTimeslots.get(date);
        setTimeSlotRecyclerView();
    }

    public void setCalendarArrays() {
        for (Timestamp timestamp: availableSlots) {
            Date date = timestamp.toDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            calendarList.add(cal);
        }
    }

    public HashMap<String, ArrayList<Timestamp>> timestampArrayListToHashMap(){
        HashMap<String, ArrayList<Timestamp>> timeStampHashMap = new HashMap<String, ArrayList<Timestamp>>();
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        for (Timestamp availableTimeSlot: availableSlots){
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