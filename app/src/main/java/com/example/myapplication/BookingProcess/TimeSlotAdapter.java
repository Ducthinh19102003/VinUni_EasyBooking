package com.example.myapplication.BookingProcess;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    Context context;
    ArrayList<Timestamp> hours;
    OnTimeSlotListener listener;
    int selectedPosition=-1;
    public TimeSlotAdapter(Context context, ArrayList<Timestamp> hours, OnTimeSlotListener listener) {
        this.context = context;
        this.hours = hours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_timeslot,parent,false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.MyViewHolder holder, int position) {
        Timestamp hour = hours.get(position);
        String h = String.valueOf(hour.toDate().getHours());
        String m = String.valueOf(hour.toDate().getMinutes());
        if(m.equals("0")) m = "00";
        String timetime = h + ":" + m;
        holder.time.setText(timetime);

        if (selectedPosition==position) {
            holder.time.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        }
        else
            holder.time.setBackgroundColor(Color.parseColor("#E1E1E1"));

        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDate.time = timetime;
                selectedPosition=position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (hours != null) return hours.size();
        else return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatButton time;
        OnTimeSlotListener listener;


        public MyViewHolder(@NonNull View itemView, TimeSlotAdapter.OnTimeSlotListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.timeslot);
            this.listener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onTimeSlotClick(getBindingAdapterPosition());
        }
    }

    public interface OnTimeSlotListener {
        void onTimeSlotClick(int position);
    }
}
