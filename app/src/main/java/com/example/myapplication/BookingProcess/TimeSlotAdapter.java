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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeslot,parent,false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.MyViewHolder holder, int position) {
        Timestamp timeslot = hours.get(position);
        Date dateSlot = timeslot.toDate();
        SelectDate.selectedTimestamp = hours.get(position);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        String timeselect = df.format(dateSlot);


        holder.time.setText(timeselect);

        if (selectedPosition==holder.getBindingAdapterPosition()) {
            holder.time.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        }
        else
            holder.time.setBackgroundColor(Color.parseColor("#E1E1E1"));

        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateSlot);
                cal.add(Calendar.MINUTE, 30);
                SelectDate.endTime = new Timestamp(cal.getTime());
                String endTime = df.format(SelectDate.endTime);

                SelectDate.time = timeselect + " - " + endTime;
                selectedPosition = holder.getBindingAdapterPosition();
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
