package com.example.myapplication.BookingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.ProfessorInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OfficeHourBooking extends AppCompatActivity implements ProfessorAdapter.OnItemListener {
    RecyclerView professorRecView;
    ProfessorAdapter myAdapter;
    ArrayList<ProfessorInfo> professorItemArrayList;
    FirebaseFirestore fstore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_hour_booking);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Booking page");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        professorRecView = findViewById(R.id.prof_list);
        professorRecView.setHasFixedSize(true);
        professorRecView.setLayoutManager(new LinearLayoutManager(this));

        fstore = FirebaseFirestore.getInstance();
        professorItemArrayList = new ArrayList<ProfessorInfo>();
        myAdapter = new ProfessorAdapter(OfficeHourBooking.this, professorItemArrayList,this);

        professorRecView.setAdapter(myAdapter);

        EventChangeListener();
    }

    void EventChangeListener() {
        fstore.collection("Professors").orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("OfficeHourBooking", document.getId() + " => " + document.getData());
                                ProfessorInfo professor = document.toObject(ProfessorInfo.class);
                                professorItemArrayList.add(professor);
                            }
                        } else {
                            Log.d("OfficeHourBooking", "Error getting documents: ", task.getException());
                        }
                        myAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        ProfessorInfo currentProfessor = professorItemArrayList.get(position);
        ArrayList<Timestamp> evl = currentProfessor.getAvailableTimeSlots();
        for (Timestamp time : evl) {
            if (time.compareTo(Timestamp.now()) <= 0) {
                evl.remove(time);
            }
        }
        if (evl.size() == 0) {
            Toast.makeText(OfficeHourBooking.this, "Professor " + currentProfessor.getName() + " is currently unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }
        currentProfessor.setAvailableTimeSlots(evl);
        Intent intent = new Intent(OfficeHourBooking.this, SelectDate.class);
        intent.putExtra("professor", currentProfessor);
        startActivity(intent);
    }
}