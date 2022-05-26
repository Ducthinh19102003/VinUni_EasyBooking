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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView CardsEventsRv;
    private static final String TAG  = "WeekViewActivity";
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    public ArrayList<EventInfo> eventInfoArrayList;
    public ArrayList<CardEvents> cardEventsArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eventInfoArrayList = new ArrayList<EventInfo>();
        cardEventsArrayList = new ArrayList<CardEvents>();

        CardsEventsRv = binding.appointmentsID;
        retrieveData();
        return root;
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

    public void setCardsEventsView() {
        Log.d("HomeFragment", "Card Events: " + cardEventsArrayList );
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
        Log.d("HomeFragment", "Hello");
        Log.d("HomeFragment", Login.userType + "/" + Login.userID + "/" + "Events");
        fstore.collection(Login.userType).document(Login.userID).collection("Events")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("HomeFragment", "Getting document");
                                EventInfo event = document.toObject(EventInfo.class);
                                if (event.getStartTime().compareTo(Timestamp.now()) < 0) {
                                    document.getReference().delete();
                                }
                                else {
                                    Log.d("HomeFragment", "event is " + event);
                                    eventInfoArrayList.add(event);
                                }
                            }
                            Collections.sort(eventInfoArrayList);
                            createCardEventsArrayList();
                            setCardsEventsView();
                        } else {
                            Log.d("HomeFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}