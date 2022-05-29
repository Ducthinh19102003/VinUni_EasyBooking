package com.example.myapplication.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingProcess.SelectDate;
import com.example.myapplication.EventInfo;
import com.example.myapplication.HomePage;
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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView CardsEventsRv;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    public ArrayList<EventInfo> eventInfoArrayList;
    public ArrayList<CardEvents> cardEventsArrayList;
    TextView noti;
    CardEventsAdapter eventSlotAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        eventInfoArrayList = new ArrayList<>();
        cardEventsArrayList = new ArrayList<>();

        noti = binding.notiID;

        CardsEventsRv = binding.appointmentsID;
        CardsEventsRv.setHasFixedSize(true);
        CardsEventsRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventSlotAdapter = new CardEventsAdapter(cardEventsArrayList);
        CardsEventsRv.setAdapter(eventSlotAdapter);

        retrieveData();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                                SelectDate.evlst = eventInfoArrayList;
                                Log.d("Debug", eventInfoArrayList + "");
                                createCardEventsArrayList();
                                eventSlotAdapter.notifyDataSetChanged();
                            }
                            else {
                                noti.setVisibility(View.VISIBLE);
                            }


                        } else {
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