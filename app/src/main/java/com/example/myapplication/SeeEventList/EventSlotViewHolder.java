package com.example.myapplication.SeeEventList;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.EventInfo;
import com.example.myapplication.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class EventSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final ArrayList<EventInfo> eventInfoArrayList;
    public final View eventSlotParent;
    public final TextView hourOfEvent;
    public final TextView dayOfEvent;
    private final EventSlotAdapter.OnItemListener onItemListener;
    public EventSlotViewHolder(@NonNull View itemView, EventSlotAdapter.OnItemListener onItemListener, ArrayList<EventInfo> eventInfoArrayList)
    {
        super(itemView);
        eventSlotParent = itemView.findViewById(R.id.eventSlotParent);
        hourOfEvent = itemView.findViewById(R.id.eventSlotHour);
        dayOfEvent = itemView.findViewById(R.id.eventSlotDate);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.eventInfoArrayList = eventInfoArrayList;
    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), eventInfoArrayList.get(getAdapterPosition()));
    }
}
