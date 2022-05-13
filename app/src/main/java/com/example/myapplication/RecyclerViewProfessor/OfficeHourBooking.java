package com.example.myapplication.RecyclerViewProfessor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.ProfessorInfo;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;

public class OfficeHourBooking extends AppCompatActivity {
    RecyclerView professorRecView;
    ArrayList<ProfessorItem> professorItemArrayList;
    ProfessorAdapter myAdapter;
    FirebaseFirestore fstore;
//    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_hour_booking);


//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Fetching data...");
//        progressDialog.show();

        professorRecView = findViewById(R.id.prof_list);
        professorRecView.setHasFixedSize(true);
        professorRecView.setLayoutManager(new LinearLayoutManager(this));

        fstore = FirebaseFirestore.getInstance();
        professorItemArrayList = new ArrayList<ProfessorItem>();
        myAdapter = new ProfessorAdapter(OfficeHourBooking.this, professorItemArrayList);

        professorRecView.setAdapter(myAdapter);

        EventChangeListener();
    }

    void EventChangeListener() {
        fstore.collection("Professors").orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error!", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc: value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String name = dc.getDocument().getString("name");
                                String email = dc.getDocument().getString("email");
                                String subjects = dc.getDocument().getString("Subject");

                                ProfessorItem professorItem = new ProfessorItem(email,name,subjects, R.drawable.image1);
                                professorItemArrayList.add(professorItem);
                            }
                            myAdapter.notifyDataSetChanged();

//                            if (progressDialog.isShowing()) {
//                                progressDialog.dismiss();
//                            }
                        }
                    }
                });
    }


}