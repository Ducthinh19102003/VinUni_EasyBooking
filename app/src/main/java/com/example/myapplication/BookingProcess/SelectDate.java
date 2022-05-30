package com.example.myapplication.BookingProcess;
import static com.example.myapplication.Login.professorList;
import static com.example.myapplication.Login.studentList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.Fragments.Home.HomeFragment;
import com.example.myapplication.HomePage;
import com.example.myapplication.Login;
import com.example.myapplication.ProfessorInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
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

    DatePickerDialog datePickerDialog;
    MaterialCardView setDateBtn, setTimeBtn;
    AppCompatButton makeAppointmentBtn;
    EditText meetingTitle, meetingParticipants, meetingNote;
    TextView dateSelected, timeSelected;
    Switch isOnline;
    ArrayList<String> professors = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();
    ProfessorInfo currentProfessor;

    ProgressBar progressBar;

    RecyclerView timeSlotRecyclerView;
    TimeSlotAdapter timeSlotAdapter;
    RecyclerView.LayoutManager layoutManager;
    Dialog dialog;
    static Timestamp selectedTimestamp, endTime;

    public static ArrayList<Timestamp> availableSlots;
    public static String location;
    ArrayList<Calendar> calendarList;
    HashMap<String, ArrayList<Timestamp>> categorizedTimeslots;
    static String date = "";
    static String time = "";
    String converted;

    ArrayList<Timestamp> hours;
    FirebaseFirestore fstore;
    EventInfo new_event;
    public static ArrayList<EventInfo> evlst;

    void setTimeSlotRecyclerView() {
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
        currentProfessor = getIntent().getParcelableExtra("professor");
        availableSlots = currentProfessor.getAvailableTimeSlots();

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
                ArrayList<String> participantList = new ArrayList<>();
                participantList.add(Login.currentStudent.getEmail());
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
                Log.d("SelectDate", "check: " + check);
                if (check.size() > 0) {
                    Toast.makeText(SelectDate.this, "Unavailable record(s): " + check, Toast.LENGTH_SHORT).show();
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
                if (TextUtils.isEmpty(note)) {
                    meetingNote.setError("Add meeting note!");
                    meetingNote.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (isOnline.isChecked()) {
                    location = "Microsoft Teams";
                }
                Log.d("Professor", currentProfessor + "");
                new_event = new EventInfo(currentProfessor.getName(), participantList, selectedTimestamp,endTime,note, title, currentProfessor.getLocation());
                ArrayList<String> conflict = checkConflict(new_event, evlst);
                progressBar.setVisibility(View.GONE);
                if (conflict.size() > 0) {
                    Toast.makeText(SelectDate.this, "Choosen timeslot conflict with " + conflict, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    professors.add(currentProfessor.getEmail());
                    EventToFireBase();
                    removeTimeslot();
                    startActivity(new Intent(SelectDate.this, HomePage.class));
                }
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
    public void EventToFireBase() {
        Log.d("Students", students + "");
        Log.d("Professors", professors + "");
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
    public void removeTimeslot() {
        Log.d("Professor", currentProfessor.getUID());
        DocumentReference docref = fstore.collection("Professors").document(currentProfessor.getUID());
        docref.update("availableTimeSlots", FieldValue.arrayRemove(selectedTimestamp));
    }

    public ArrayList<String> checkConflict(EventInfo newEvent, ArrayList<EventInfo> eventInfoArrayList){
        //loop thru everything because it's the only way. Like bruh.
        //May use binary search later. But there are 2 cases so I'm not sure.
        ArrayList<String> conflictEvents = new ArrayList<>();
        for (int i = 0; i < eventInfoArrayList.size(); i++){
            //Conflict cases
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0
                    && newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getEndTime()) < 0)
                //Event start as another is happening
                conflictEvents.add(eventInfoArrayList.get(i).getMeetingName());
            if (newEvent.getStartTime().compareTo(eventInfoArrayList.get(i).getStartTime()) < 0
                    && newEvent.getEndTime().compareTo(eventInfoArrayList.get(i).getStartTime()) > 0)
                //Another event would start as this event is happening
                conflictEvents.add(eventInfoArrayList.get(i).getMeetingName());
        }
        return conflictEvents;
    }
    @Override
    public void onTimeSlotClick(int position) {
        Log.d("Time", time);
    }


}