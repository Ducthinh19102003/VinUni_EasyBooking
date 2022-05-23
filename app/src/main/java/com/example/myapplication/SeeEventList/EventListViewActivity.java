package com.example.myapplication.SeeEventList;

import static com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils.defaultHours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.EventInfo;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotAdapter;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.HourSlotUtils;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventListViewActivity extends AppCompatActivity implements EventSlotAdapter.OnItemListener {
    private RecyclerView eventRecyclerView;
    private static final String TAG  = "WeekViewActivity";
    String professorID = "GutdziKpOWV44uZ6aSEeQqhwfVc2";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Professors").document(professorID);
    private static ArrayList<EventInfo> eventInfoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        initWidgets();
        //Retrieve the array of event ids
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> eventIDArrayList = (ArrayList<String>) document.get("events");
                        //Retrieve the event arraylist from the ids
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (String doc : eventIDArrayList) {
                            Log.d("Debug", doc.trim()); //yes, a bug was here
                            tasks.add(db.collection("Events").document(doc.trim()).get());
                        }
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> list) {
                                //Do what you need to do with your list
                                for (Object object : list) {
                                    EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                    eventInfoArrayList.add(fm);
                                }
                                setEventView();
                            }

                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void setEventView() {
        EventSlotAdapter eventSlotAdapter = new EventSlotAdapter(eventInfoArrayList, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setAdapter(eventSlotAdapter);
    }

    private void initWidgets()
    {
        eventRecyclerView = findViewById(R.id.EventRecyclerView);
    }

    @Override
    public void onItemClick(int position, EventInfo event) {

    }
}