package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    Button student, professor, admin;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        student = findViewById(R.id.studentID);
        professor = findViewById(R.id.profID);
        admin = findViewById(R.id.adminID);

        student.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login.portal = 1;
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        professor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Login.portal = 2;
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Login.portal = 3;
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }
    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


