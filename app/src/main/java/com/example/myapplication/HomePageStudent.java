package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.RecyclerViewProfessor.OfficeHourBooking;

public class HomePageStudent extends AppCompatActivity {
    CardView booking;
    TextView name,email;
    static String studentName;
    static String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_student);

        name = findViewById(R.id.txt_user_name);
        email = findViewById(R.id.txt_email);
        booking = findViewById(R.id.booking_card);

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getApplicationContext(), OfficeHourBooking.class);
                startActivity(k);
            }
        });
    }
}