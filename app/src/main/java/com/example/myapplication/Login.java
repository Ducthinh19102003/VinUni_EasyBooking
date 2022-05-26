package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.view.MenuItem;

import com.example.myapplication.Fragments.Home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText getEmail, getPassword;
    TextView toRegister;
    Button LoginButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    static public ProfessorInfo currentProfessor;
    static public StudentInfo currentStudent;
    static public String userID;
    static public String userType;
    static public int portal;
    private static final String tag = "MyActivity";

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ActionBar actionBar = getSupportActionBar();
        if (Login.portal == 1) {
            actionBar.setTitle("Welcome student!");
        }
        else if (Login.portal == 2) {
            actionBar.setTitle("Welcome professor!");
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        getEmail = findViewById(R.id.email);
        getPassword = findViewById(R.id.profPassword);
        LoginButton = findViewById(R.id.profRegisterBtn);
        toRegister = findViewById(R.id.createAccount);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        // adding on click listener for our button.
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting text from our edittext fields.
                String email = getEmail.getText().toString().trim();
                String password = getPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    getEmail.setError("Email is Required.");
                    getEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    getPassword.setError("Password is Required.");
                    getPassword.requestFocus();
                    return;
                }

                if (!email.contains("@vinuni.edu.vn")) {
                    getEmail.setError("Invalid email");
                    getEmail.requestFocus();
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                            if (Login.portal == 1) {
                                userType = "Students";
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference docRef = fstore.collection("Students").document(userID);
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        currentStudent = documentSnapshot.toObject(StudentInfo.class);
                                    }
                                });
                            }
                            else if (Login.portal == 2) {
                                userType = "Professors";
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference docRef = fstore.collection("Professors").document(userID);
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        currentProfessor = documentSnapshot.toObject(ProfessorInfo.class);
                                    }
                                });
                            }
                            //Need to synchronize professor with student homepage into homepage
                            startActivity(new Intent(Login.this, HomePage.class));
                        } else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, Login.portal + " ");
                if (Login.portal == 1) startActivity(new Intent(getApplicationContext(),RegisterStudent.class));
                if (Login.portal == 2) startActivity(new Intent(getApplicationContext(),RegisterProfessor.class));
            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
