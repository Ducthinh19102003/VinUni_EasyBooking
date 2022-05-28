package com.example.myapplication.BookingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.Login;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.firebase.Timestamp;

public class SelectDate extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimeSlotAdapter.OnTimeSlotListener  {

    DatePickerDialog datePickerDialog ;
    MaterialCardView setDateBtn, setTimeBtn;
    AppCompatButton makeAppointmentBtn;
    EditText meetingTitle, meetingParticipants, meetingNote;
    TextView dateSelected, timeSelected;
    Switch isOnline;
    ArrayList<String> participantList = new ArrayList<>();
    ProgressBar progressBar;

    RecyclerView timeSlotRecyclerView;
    TimeSlotAdapter timeSlotAdapter;
    RecyclerView.LayoutManager layoutManager;
    Dialog dialog;
    static Timestamp selectedTimestamp;

    public static ArrayList<Timestamp> availableSlots;
    public static String location;
    ArrayList<Calendar> calendarList;
    HashMap<String, ArrayList<Timestamp>> categorizedTimeslots;
    static String date = "";
    static String time = "";
    static String host = "";
    static Timestamp endTime;
    ArrayList<Timestamp> hours;
    FirebaseFirestore fstore;

    void setTimeSlotRecyclerView() {
        Log.d("SelectDate", availableSlots + " ");
        Log.d("SelectDate", hours + " ");

        timeSlotRecyclerView = dialog.findViewById(R.id.recTimeID);
        timeSlotAdapter = new TimeSlotAdapter(SelectDate.this, hours, this);
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotRecyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_appointment);

        fstore = FirebaseFirestore.getInstance();
        calendarList = new ArrayList<Calendar>();

        setCalendarArrays();
        categorizedTimeslots = timestampArrayListToHashMap();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dateSelected = findViewById(R.id.DateSetID);
        timeSelected = findViewById(R.id.TimSetID);
        makeAppointmentBtn = findViewById(R.id.AppointmentSetID);

        setDateBtn = findViewById(R.id.setMeetingDate);
        setTimeBtn = findViewById(R.id.setMeetingTime);
        progressBar = findViewById(R.id.progressID);

        meetingTitle = findViewById(R.id.AddTitleID);
        meetingNote = findViewById(R.id.AddNoteID);
        meetingParticipants = findViewById(R.id.AddParticipantsID);
        isOnline = findViewById(R.id.isOnlineID);

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
        setTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (date.equals("")) {
                    Toast.makeText(SelectDate.this, "Select Date first!", Toast.LENGTH_SHORT).show();
                }
                else openDialog(SelectDate.this);
            }
        });

        makeAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = meetingTitle.getText().toString().trim();
                String participants = meetingParticipants.getText().toString().trim();
                participantList.addAll(Arrays.asList(participants.split("[, ]+")));
                String note = meetingNote.getText().toString().trim();


                if (TextUtils.isEmpty(title)) {
                    meetingTitle.setError("Add meeting title!");
                    meetingTitle.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    meetingNote.setError("Add meeting note!");
                    meetingNote.requestFocus();
                    return;
                }
                if (date.equals("")) {
                    Toast.makeText(SelectDate.this, "Date is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time.equals("")) {
                    Toast.makeText(SelectDate.this, "Time is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (isOnline.isChecked()) {
                    location = "Microsoft Teams";
                }
                EventInfo new_event = new EventInfo(host, participantList, selectedTimestamp, endTime, note, title, location);
                fstore.collection(Login.userType).document(Login.userID).collection("Events")
                        .add(new_event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("SelectDate", "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("SelectDate", "Error adding document", e);
                            }
                        });
            }
        });
    }
    public void openDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.select_time_slot);

        Button ok = dialog.findViewById(R.id.okBtn);
        Button cancel = dialog.findViewById(R.id.cancelBtn);

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTimeSlotRecyclerView();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = "";
                timeSelected.setText(time);
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSelected.setText(time);
                dialog.dismiss();
            }
        });
    }
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        Date chosenDate = cal.getTime();

        String pattern = "EEEE, dd/MM";
        DateFormat df = new SimpleDateFormat(pattern);

        date = df.format(chosenDate);
        Toast.makeText(SelectDate.this, date, Toast.LENGTH_LONG).show();
        dateSelected.setText(date);
        hours = categorizedTimeslots.get(date);
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
        String pattern = "EEEE, dd/MM";
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

    @Override
    public void onTimeSlotClick(int position) {
        Log.d("Time", time);
    }
}