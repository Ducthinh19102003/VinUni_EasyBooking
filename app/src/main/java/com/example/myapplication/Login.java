package com.example.myapplication;
import static com.example.myapplication.Fragments.Home.HomeFragment.cardEventsArrayList;
import static com.example.myapplication.Fragments.Home.HomeFragment.eventInfoArrayList;

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

import com.example.myapplication.Fragments.Home.CardEvents;
import com.example.myapplication.Fragments.Home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public static ArrayList<String> usersList;

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

        usersList = new ArrayList<>();
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
                        userID = fAuth.getCurrentUser().getUid();
                        if (task.isSuccessful()) {
                            DocumentReference docRef = fstore.collection(userType).document(userID);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            Log.d("Login", "DocumentSnapshot data: " + task.getResult().getData());
                                            if (userType.equals("Students")) currentStudent = task.getResult().toObject(StudentInfo.class);
                                            else if (userType.equals("Professors")) currentProfessor = task.getResult().toObject(ProfessorInfo.class);
                                            Log.d("Login", "Student: " + currentStudent);
                                            retrieveData();
                                            retrieveUsersEmail();
                                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, HomePage.class));
                                        } else {
                                            Log.d("Login", "No such document");
                                        }
                                    } else {
                                        Log.d("Login", "get failed with ", task.getException());
                                    }
                                }
                            });
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
                if (Login.portal == 1) startActivity(new Intent(getApplicationContext(),RegisterStudent.class));
                if (Login.portal == 2) startActivity(new Intent(getApplicationContext(),RegisterProfessor.class));
            }
        });
    }

    public void retrieveData() {
        fstore.collection(Login.userType).document(Login.userID).collection("Events")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Login", "Getting document");
                                EventInfo event = document.toObject(EventInfo.class);
                                if (event.getStartTime().compareTo(Timestamp.now()) < 0) {
                                    document.getReference().delete();
                                }
                                else {
                                    Log.d("Login", "event is " + event);
                                    eventInfoArrayList.add(event);
                                }
                            }
                            Collections.sort(eventInfoArrayList);

                            if(eventInfoArrayList.size() > 0) {
                                createCardEventsArrayList();
                            }

                        } else {
                            Log.d("Login", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void retrieveUsersEmail() {
        fstore.collection("Professors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usersList.add(document.getString("email"));

                    }
                }
                else {
                    Log.d("Login", "Error getting documents: ", task.getException());
                }
            }
        });
        fstore.collection("Students").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usersList.add(document.getString("email"));
                    }
                }
                else {
                    Log.d("Login", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void createCardEventsArrayList() {
        String pattern = "EEEE, dd/MM";
        DateFormat df = new SimpleDateFormat(pattern);

        String date = df.format(eventInfoArrayList.get(0).getStartTime().toDate());

        ArrayList<EventInfo> eventList = new ArrayList<>();
        eventList.add(eventInfoArrayList.get(0));

        cardEventsArrayList.add(new CardEvents(date, eventList));

        int index = 0;

        for (int i = 1; i < eventInfoArrayList.size(); i++) {
            String new_date = df.format(eventInfoArrayList.get(i).getStartTime().toDate());
            if (date.equals(new_date)) {
                cardEventsArrayList.get(index).eventList.add(eventInfoArrayList.get(i));
            }
            else {
                eventList = new ArrayList<>();
                eventList.add(eventInfoArrayList.get(i));
                cardEventsArrayList.add(new CardEvents(new_date, eventList));
                index++;
            }
        }
    }
}
