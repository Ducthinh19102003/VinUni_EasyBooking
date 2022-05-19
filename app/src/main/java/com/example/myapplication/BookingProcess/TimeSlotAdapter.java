package com.example.myapplication.BookingProcess;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    Context context;
    ArrayList<Timestamp> hours;

    public TimeSlotAdapter(Context context, ArrayList<Timestamp> hours) {
        this.context = context;
        this.hours = hours;
    }

    @NonNull
    @Override
    public TimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_timeslot,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.MyViewHolder holder, int position) {
        Timestamp hour = hours.get(position);
        holder.time.setText(hour.toDate().getHours() + ":" + hour.toDate().getMinutes());
    }

    @Override
    public int getItemCount() {
        if (hours != null) return hours.size();
        else return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.timeSlot);
        }
    }
}
