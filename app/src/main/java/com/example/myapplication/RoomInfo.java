package com.example.myapplication;

import static com.example.myapplication.EventInfo.checkConflict;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RoomInfo {
    private static final String TAG = "RoomInfo";
    private ArrayList<String> events;
    String roomID = "A101";
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public RoomInfo(){
        this.events = new ArrayList<>();
    }
    public static void roomBooking(String roomID, EventInfo bookedEventInfo){
        //Assuming that the event is not on the database yet.
        //Assuming no conflict with the host and members
        //Retireving the room's eventlist
        DocumentReference docRef = db.collection("Rooms").document(roomID);
        ArrayList<EventInfo> eventInfoArrayList = new ArrayList<>();
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
                                Log.d("RoomInfo debug", list.toString());
                                //create dummy list, put everything there, check later
                                ArrayList<EventInfo> eventInfoDummyList = new ArrayList<>();
                                for (Object object : list) {

                                    EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                    eventInfoDummyList.add(fm);
                                }
                                //look into the dummy list
                                for (EventInfo event :eventInfoDummyList){
                                    if (event == null){
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(null));
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else if (event.getStartTime().compareTo(Timestamp.now()) < 0){
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(event));
                                        db.collection("Events").document(fmID).delete();
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else eventInfoArrayList.add(event);
                                }
                                if (!checkConflict(bookedEventInfo, eventInfoArrayList)){
                                    //EventToDatabase with a twist
                                    db.collection("Events").add(bookedEventInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //retrieving the id
                                            String eventID = documentReference.getId();
                                            //Adding event ID to room's array
                                            db.collection("Rooms").document(roomID).update("events", FieldValue.arrayUnion(eventID));
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + eventID);

                                            //Adding event id to host's array
                                            //Check if the host's ID is in the "Professors" collection
                                            DocumentReference userRef = db.collection("Professors").document(bookedEventInfo.getHost().trim());
                                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            //if the host id is indeed in "Professors"
                                                            db.collection("Professors").document(bookedEventInfo.getHost().trim()).update("events", FieldValue.arrayUnion(eventID));
                                                        } else {
                                                            //if the host id is not in "Professors"
                                                            db.collection("Students").document(bookedEventInfo.getHost().trim()).update("events", FieldValue.arrayUnion(eventID));
                                                        }
                                                    } else {
                                                        Log.d(TAG, "Failed with: ", task.getException());
                                                    }
                                                }
                                            });

                                            //Adding event id to member's array
                                            for (String i: bookedEventInfo.getMembers()) {
                                                DocumentReference memberRef = db.collection("Professors").document(i.trim());
                                                memberRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                //if the member id is indeed in "Professors"
                                                                db.collection("Professors").document(i.trim()).update("events", FieldValue.arrayUnion(eventID));
                                                            } else {
                                                                //if the member id is not in "Professors"
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
}