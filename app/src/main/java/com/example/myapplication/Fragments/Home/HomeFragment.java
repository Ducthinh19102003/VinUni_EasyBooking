package com.example.myapplication.Fragments.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.Login;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView CardsEventsRv;
    private static final String TAG  = "WeekViewActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static ArrayList<EventInfo> eventInfoArrayList;
    public static ArrayList<CardEvents> cardEventsArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CardsEventsRv = binding.appointmentsID;
        eventInfoArrayList = new ArrayList<>();
        cardEventsArrayList = new ArrayList<CardEvents>();

//        retrieveData();
//        Log.d("HomeFragment", eventInfoArrayList + "");
//        CreateCardEventsArrayList();
//        Log.d("HomeFragment", cardEventsArrayList + "");
//        setCardsEventsView();
        return root;
    }

    void CreateCardEventsArrayList() {
        String pattern = "EEEE, dd/MM";
        DateFormat df = new SimpleDateFormat(pattern);

        String date = "";
        ArrayList<EventInfo> eventList = new ArrayList<>();

        for (EventInfo event: eventInfoArrayList){
            if (date == "") {
                eventList.add(event);
                date = df.format(event.getStartTime().toDate());
            }
            else if (date == df.format(event.getStartTime().toDate())) {
                eventList.add(event);
            }
            else {
                cardEventsArrayList.add(new CardEvents(date, eventList));
                eventList = new ArrayList<>();
                date = "";
            }
        }
    }

    public void setCardsEventsView() {
        CardEventsAdapter eventSlotAdapter = new CardEventsAdapter(cardEventsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        CardsEventsRv.setLayoutManager(layoutManager);
        CardsEventsRv.setAdapter(eventSlotAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void retrieveData() {
        //Retrieve the array of event ids

        DocumentReference docRef = db.collection(Login.userType).document(Login.userID);
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
                            tasks.add(db.collection("Events").document(doc.trim()).get());
                        }
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> list) {
                                ArrayList<EventInfo> eventInfoDummyList = new ArrayList<>();
                                for (Object object : list) {

                                    EventInfo fm = ((DocumentSnapshot) object).toObject(EventInfo.class);
                                    eventInfoDummyList.add(fm);
                                }
                                for (EventInfo event :eventInfoDummyList){
                                    if (event == null){
                                        Log.d("HomeFragment", "1");
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(null));
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else if (event.getStartTime().compareTo(Timestamp.now()) < 0){
                                        Log.d("HomeFragment", "2");
                                        String fmID = eventIDArrayList.get(eventInfoDummyList.indexOf(event));
                                        db.collection("Events").document(fmID).delete();
                                        docRef.update("events", FieldValue.arrayRemove(fmID));
                                    }
                                    else {
                                        Log.d("HomeFragment", "3");
                                        eventInfoArrayList.add(event);
                                    }
                                }
                                Collections.sort(eventInfoArrayList);
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