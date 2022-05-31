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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterProfessor extends AppCompatActivity {

    private EditText professorPassword, professorName, profesorSubject, professorEmail;
    private Button RegisterBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    String profID;
    ProfessorInfo professor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_professor);

        getSupportActionBar().setTitle("Create a professor account");

        professorEmail = findViewById(R.id.profEmail);
        professorPassword = findViewById(R.id.profPassword);
        professorName = findViewById(R.id.profName);
        profesorSubject = findViewById(R.id.profSubject);

        progressBar = findViewById(R.id.progressBar);
        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();

        RegisterBtn = findViewById(R.id.profRegisterBtn);
        // adding on click listener for our button.
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting text from our edittext fields.
                String email = professorEmail.getText().toString().trim();
                String password = professorPassword.getText().toString();
                String name = professorName.getText().toString();
                String subject = profesorSubject.getText().toString();

                // below line is for checking weather the
                // edittext fields are empty or not.
                if (TextUtils.isEmpty(email)) {
                    professorEmail.setError("Email is required");
                    professorEmail.requestFocus();
                    return;
                }
                if (!email.contains("@vinuni.edu.vn")) {
                    professorEmail.setError("Must be a vinuni email");
                    professorEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    professorPassword.setError("Password is required");
                    professorPassword.requestFocus();
                    return;
                }
                if (password.length() < 8) {
                    professorPassword.setError("Password must have at least 8 characters");
                    professorPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    professorName.setError("Name is required");
                    professorName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(subject)) {
                    profesorSubject.setError("Subject is required");
                    profesorSubject.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            profID = fAuth.getCurrentUser().getUid();
                            professor = new ProfessorInfo(email, password, name, subject, profID);
                            DocumentReference documentReference = fStore.collection("Professors").document(profID);
                            documentReference.set(professor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterProfessor.this, "You have successfully created an account!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterProfessor.this, Login.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterProfessor.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterProfessor.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
