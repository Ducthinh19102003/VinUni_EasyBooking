package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;
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

public class EventInfo implements Comparable<EventInfo> {

    private static final String TAG = "EventInfo";
    private String host;
    private ArrayList<String> members;
    private Timestamp startTime;
    private Timestamp endTime;
    private String note;
    private String meetingName;
    private String location;

    public EventInfo() {

    }

    public EventInfo(String host, Timestamp startTime, Timestamp endTime) {
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
        this.members = new ArrayList<>();
        this.note = "";
    }

    public EventInfo(String host, ArrayList<String> members, Timestamp startTime, Timestamp endTime, String note, String meetingName, String location) {
        this.host = host;
        this.members = members;
        this.startTime = startTime;
        this.endTime = endTime;
        this.note = note;
        this.meetingName = meetingName;
        this.location = location;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    @Override
    public int compareTo(EventInfo eventInfo) {
        if (this.getStartTime().compareTo(eventInfo.startTime) < 0) return -1;
        else if (this.getStartTime().compareTo(eventInfo.startTime) > 0)  return 1;
        else return 0;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static boolean checkConflict(EventInfo newEvent, ArrayList<EventInfo> eventInfoArrayList){
        //loop thru everything because it's the only way. Like bruh.
        //May use binary search later. But there are 2 cases so I'm not sure.
        for (int i = 0; i <eventInfoArrayList.size(); i++){
            //Conflict cases
            if (newEvent.startTime.compareTo(eventInfoArrayList.get(i).startTime) > 0
                    && newEvent.startTime.compareTo(eventInfoArrayList.get(i).endTime) < 0)
                //Event start as another is happening
                return true;
            if (newEvent.startTime.compareTo(eventInfoArrayList.get(i).startTime) < 0
                    && newEvent.endTime.compareTo(eventInfoArrayList.get(i).startTime) > 0)
                //Another event would start as this event is happening.
                return true;
        }
        return false;
    }

    public static void memberJoinEvent(String memberID, String eventID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //retrieving the needed event
        DocumentReference eventRef = db.collection("Events").document(eventID);
        eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EventInfo event = documentSnapshot.toObject(EventInfo.class);
                //retrieving the member's events
                DocumentReference memberRef = db.collection("Professors").document(memberID.trim());
                memberRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentReference memberDocRef;
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //if the host id is indeed in "Professors"
                                memberDocRef = db.collection("Professors").document(memberID.trim());
                            } else {
                                //if the host id is not in "Professors"
                                memberDocRef = db.collection("Students").document(memberID.trim());
                            }
                            memberDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                    ArrayList<EventInfo> memberEventInfoArrayList = new ArrayList<>();
                                                    for (Object object : list) {
                                                        EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                                        if (fm != null) memberEventInfoArrayList.add(fm);
                                                        boolean conflict = checkConflict(event, memberEventInfoArrayList);
                                                        if (conflict) Log.d("Debug", "conflict");
                                                        else {
                                                            memberDocRef.update("events", FieldValue.arrayUnion(eventID));
                                                            eventRef.update("members", FieldValue.arrayUnion(memberID));
                                                        }
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

                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
            }
        });
    }
    public void addMember(String memberID) {
        //This is for adding new member to the eventInfo object BEFORE it gets online.
        //For why, see method RoomInfo.roomBooking.
        //check for conflict
        EventInfo event = this;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference memberRef = db.collection("Professors").document(memberID.trim());
        memberRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentReference memberDocRef;
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //if the new member id is indeed in "Professors"
                        memberDocRef = db.collection("Professors").document(memberID.trim());
                    } else {
                        //if the new member id is not in "Professors"
                        memberDocRef = db.collection("Students").document(memberID.trim());
                    }
                    memberDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                            ArrayList<EventInfo> memberEventInfoArrayList = new ArrayList<>();
                                            for (Object object : list) {
                                                EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                                if (fm != null) memberEventInfoArrayList.add(fm);
                                                boolean conflict = checkConflict(event , memberEventInfoArrayList);
                                                if (conflict) Log.d("Debug", "conflict");
                                                else {
                                                    event.members.add(memberID);
                                                }
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

                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}
