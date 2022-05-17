package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.ProfessorSetAvailableTimeSlots.WeekViewActivity;

public class HomePageProfessor extends AppCompatActivity {
    //Need to pass professor name in for further activities
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_professor);
    }

    public void toSetAvailableTimeSlot(View view) {
        Intent k = new Intent(this, WeekViewActivity.class);
        startActivity(k);
    }
}