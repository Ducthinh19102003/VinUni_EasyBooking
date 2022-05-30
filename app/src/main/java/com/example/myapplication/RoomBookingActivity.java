package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.google.firebase.Timestamp;

public class RoomBookingActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener  {

    DatePickerDialog datePickerDialog ;
    AppCompatButton setDateBtn, setTimeBtn, makeAppointmentBtn;
    TextView dateSelected, timeSelected;

    Dialog dialog;

    HashMap<String, ArrayList<Timestamp>> categorizedTimeslots;
    static String date = "";
    static String startTime, endTime;


    Spinner roomSpinner;
    static String selectedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_booking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dateSelected = findViewById(R.id.dateDisplay);
        timeSelected = findViewById(R.id.timeDisplay);
        makeAppointmentBtn = findViewById(R.id.setappoinment);

        setDateBtn = findViewById(R.id.setDate);
        setTimeBtn = findViewById(R.id.setTime);

        Calendar now = Calendar.getInstance();
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(RoomBookingActivity.this,
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
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(RoomBookingActivity.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
        setTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (date.equals("")) Toast.makeText(RoomBookingActivity.this, "Select date first!", Toast.LENGTH_SHORT).show();
                else openDialog(RoomBookingActivity.this);
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

        this.roomSpinner.setAdapter(adapter);
        this.roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


    public void openDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.select_booking_time);

        Button ok = dialog.findViewById(R.id.ok_btn);
        Button cancel = dialog.findViewById(R.id.cancel_btn);
        EditText startHour = dialog.findViewById(R.id.startHour);
        EditText endHour = dialog.findViewById(R.id.endHour);
        EditText startMinute = dialog.findViewById(R.id.startMinute);
        EditText endMinute = dialog.findViewById(R.id.endMinute);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = endTime = "";
                timeSelected.setText("");
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = startHour.getText().toString() +":" + startMinute.getText().toString();
                endTime = endHour.getText().toString() +":" + endMinute.getText().toString();
                timeSelected.setText(startTime +"-" + endTime);
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + "0" + (monthOfYear+1) + "/"+ year;
        Toast.makeText(RoomBookingActivity.this, date, Toast.LENGTH_LONG).show();
        dateSelected.setText(date);
    }


}