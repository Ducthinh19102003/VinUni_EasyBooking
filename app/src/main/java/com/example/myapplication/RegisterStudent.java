package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterStudent extends AppCompatActivity {

    private EditText studentEmail, studentPassword, studentName, studentID;
    private Button RegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_student);

        studentEmail = findViewById(R.id.stuEmail);
        studentPassword = findViewById(R.id.stuPassword);
        studentName = findViewById(R.id.stuName);
        studentID = findViewById(R.id.stuID);

        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();

        RegisterBtn = findViewById(R.id.stuRegisterBtn);
        // adding on click listener for our button.
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting text from our edittext fields.
                String email = studentEmail.getText().toString().trim();
                String password = studentPassword.getText().toString();
                String name = studentName.getText().toString();
                String subject = studentID.getText().toString();

                // below line is for checking weather the
                // edittext fields are empty or not.
                if (TextUtils.isEmpty(email)) {
                    studentEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    studentPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 8) {
                    studentPassword.setError("Password must have at least 8 characters");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    studentName.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(subject)) {
                    studentID.setError("Student ID is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                if(fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), HomePageStudent.class));
                    finish();
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterStudent.this, "You have successfully created an account!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterStudent.this, Login.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RegisterStudent.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
