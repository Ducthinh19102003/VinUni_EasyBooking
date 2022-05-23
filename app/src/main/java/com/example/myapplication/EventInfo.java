package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventInfo {

    private static final String TAG = "EventInfo";
    private String host;
    private ArrayList<String> members;
    private Timestamp startTime;
    private Timestamp endTime;
    private String note;


    public EventInfo() {

    }

    public EventInfo(String host, Timestamp startTime, Timestamp endTime) {
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
        this.members = new ArrayList<>();
        this.note = "";
    }


    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void addMembers(String member) {this.members.add(member);}

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static void eventToDatabase(EventInfo event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //retrieving the id
                String eventID = documentReference.getId();
                Log.d(TAG, "DocumentSnapshot written with ID: " + eventID);

                //Adding event id to host's array
                //Check if the host's ID is in the "Professors" collection
                DocumentReference userRef = db.collection("Professors").document(event.getHost().trim());
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //if the host id is indeed in "Professors"
                                db.collection("Professors").document(event.getHost().trim()).update("events", FieldValue.arrayUnion(eventID));
                            } else {
                                //if the host id is not in "Professors"
                                db.collection("Students").document(event.getHost().trim()).update("events", FieldValue.arrayUnion(eventID));
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });

                //Adding event id to member's array
                for (String i: event.getMembers()) {
                    DocumentReference memberRef = db.collection("Professors").document(i.trim());
                    memberRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //if the host id is indeed in "Professors"
                                    db.collection("Professors").document(i.trim()).update("events", FieldValue.arrayUnion(eventID));
                                } else {
                                    //if the host id is not in "Professors"
                                    db.collection("Students").document(i.trim()).update("events", FieldValue.arrayUnion(eventID));
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });;
    }

    public static boolean checkConflict(EventInfo newEvent, ArrayList<EventInfo> eventInfoArrayList){
        //loop thru everything because it's the only way. Like bruh.
        //May use binary search later. But there are 2 cases so I'm not sure.
        for (int i = 0; i <eventInfoArrayList.size(); i++){
            //Conflict cases
            if (newEvent.startTime.compareTo(eventInfoArrayList.get(i).startTime) < 0
                && newEvent.startTime.compareTo(eventInfoArrayList.get(i).endTime) > 0)
                //Event start as another is happening
                return true;
            if (newEvent.endTime.compareTo(eventInfoArrayList.get(i).startTime) < 0
                    && newEvent.endTime.compareTo(eventInfoArrayList.get(i).endTime) > 0)
                //Another event would start as this event is happening.
                return true;
        }
        return false;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
