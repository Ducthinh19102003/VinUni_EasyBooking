package com.example.myapplication.Fragments.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventInfo;
import com.example.myapplication.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventSlotViewHolder>{
    ArrayList<EventInfo> eventInfoArrayList;
    Context context;

    public EventAdapter(Context context, ArrayList<EventInfo> eventInfoArrayList)
    {
        this.eventInfoArrayList = eventInfoArrayList;
        this.context = context;
    }

    public EventAdapter(ArrayList<EventInfo> eventInfoArrayList)
    {
        this.eventInfoArrayList = eventInfoArrayList;
    }

    @NonNull
    @Override
    public EventSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.event_item, parent, false);
        return new EventSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventSlotViewHolder holder, int position) {
        EventInfo event = eventInfoArrayList.get(position);
        holder.meetingName.setText(event.getMeetingName());

        DateFormat df = new SimpleDateFormat("HH:mm");
        String startTime = df.format(event.getStartTime().toDate());
        String endTime = df.format(event.getEndTime().toDate());

        holder.time.setText(startTime + " - " + endTime);

        holder.location.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        return eventInfoArrayList.size();
    }

    public static class EventSlotViewHolder extends RecyclerView.ViewHolder
    {
        public TextView meetingName, time, location;
        public EventSlotViewHolder(@NonNull View itemView)
        {
            super(itemView);
            meetingName = itemView.findViewById(R.id.eventNameID);
            time = itemView.findViewById(R.id.timeID);
            location = itemView.findViewById(R.id.locationID);
        }
    }
}
