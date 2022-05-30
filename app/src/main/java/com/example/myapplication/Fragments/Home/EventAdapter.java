package com.example.myapplication.Fragments.Home;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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
        return new EventSlotViewHolder(view, eventInfoArrayList);
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

    public static class EventSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView meetingName, time, location;
        ArrayList<EventInfo> eventInfoArrayList;
        public EventSlotViewHolder(@NonNull View itemView, ArrayList<EventInfo> eventInfoArrayList)
        {
            super(itemView);
            this.eventInfoArrayList = eventInfoArrayList;
            meetingName = itemView.findViewById(R.id.eventNameID);
            time = itemView.findViewById(R.id.timeID);
            location = itemView.findViewById(R.id.locationID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("EventSlotViewHolder", "clicked");
            int position = this.getAdapterPosition();
            EventInfo thisEvent  = eventInfoArrayList.get(position);

            Dialog dialog = new Dialog(itemView.getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.event_info_dialogue);
            TextView currentEvName = dialog.findViewById(R.id.meeting_name);
            TextView currentEvTime = dialog.findViewById(R.id.meeting_time);
            TextView currentEvLoc = dialog.findViewById(R.id.meeting_loc);
            TextView currentEvNote = dialog.findViewById(R.id.meeting_note);
            Button done = dialog.findViewById(R.id.done_btn);

            currentEvName.setText(thisEvent.getMeetingName());
            currentEvLoc.setText(thisEvent.getLocation());
            DateFormat df = new SimpleDateFormat("HH:mm");
            String startTime = df.format(thisEvent.getStartTime().toDate());
            String endTime = df.format(thisEvent.getEndTime().toDate());
            currentEvTime.setText((new SimpleDateFormat("dd/MM/yyyy")).format(thisEvent.getStartTime().toDate()) + ": " + startTime + " - " + endTime);
            currentEvNote.setText(thisEvent.getNote());

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
}
