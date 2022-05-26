package com.example.myapplication.Fragments.Calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class EventSlotAdapter extends RecyclerView.Adapter<EventSlotViewHolder>{
    private final ArrayList<EventInfo> eventInfoArrayList;
    private final EventSlotAdapter.OnItemListener onItemListener;

    public EventSlotAdapter(ArrayList<EventInfo> eventInfoArrayList, EventSlotAdapter.OnItemListener onItemListener)
    {
        this.eventInfoArrayList = eventInfoArrayList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public EventSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_slot_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight()*0.3);

        return new EventSlotViewHolder(view, onItemListener, eventInfoArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull EventSlotViewHolder holder, int position) {
        final EventInfo event = eventInfoArrayList.get(position);
        if(event == null) {
            holder.dayOfEvent.setText("");
            holder.hourOfEvent.setText("");
        }
        else
        {
            Timestamp startHour = event.getStartTime();
            Timestamp endHour = event.getEndTime();
            holder.hourOfEvent.setText(String.valueOf(startHour.toDate().getHours() + ":" + startHour.toDate().getMinutes() + "-" + endHour.toDate().getHours() + ":" + endHour.toDate().getMinutes()));
            holder.dayOfEvent.setText(startHour.toDate().toString());
            holder.eventSlotParent.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return eventInfoArrayList.size();
    }

    public interface OnItemListener
    {
        void onItemClick(int position, EventInfo event);
    }
}
