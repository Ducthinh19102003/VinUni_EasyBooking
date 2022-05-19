package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.BookingProcess.OfficeHourBooking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePageStudent extends AppCompatActivity {
    CardView booking;
    TextView name,email;
    FirebaseFirestore fstore;
    StudentInfo student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_student);

        getSupportActionBar().hide();


        name = findViewById(R.id.txt_user_name);
        email = findViewById(R.id.txt_email);
        booking = findViewById(R.id.booking_card);
        String UID_student = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();

        //Query the data of student then convert it to object
        DocumentReference docRef = fstore.collection("Students").document(UID_student);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                student = documentSnapshot.toObject(StudentInfo.class);
                name.setText(student.getName());
                email.setText(student.getEmail());
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), OfficeHourBooking.class);
                startActivity(k);
            }
        });
    }
}