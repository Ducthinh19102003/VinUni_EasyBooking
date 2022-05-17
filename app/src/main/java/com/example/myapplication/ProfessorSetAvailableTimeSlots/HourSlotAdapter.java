package com.example.myapplication.ProfessorSetAvailableTimeSlots;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.util.ArrayList;

public class HourSlotAdapter extends RecyclerView.Adapter<HourSlotViewHolder>{
    private final ArrayList<Timestamp> hours;
    private final HourSlotAdapter.OnItemListener2 onItemListener2;

    public HourSlotAdapter(ArrayList<Timestamp> hours, HourSlotAdapter.OnItemListener2 onItemListener2)
    {
        this.hours = hours;
        this.onItemListener2 = onItemListener2;
    }

    @NonNull
    @Override
    public HourSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.hour_slot_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight()*0.25);

        return new HourSlotViewHolder(view, onItemListener2, hours);
    }

    @Override
    public void onBindViewHolder(@NonNull HourSlotViewHolder holder, int position) {
        final Timestamp hour = hours.get(position);
        if(hour == null)
            holder.hourOfSlot.setText("");
        else
        {
            holder.hourOfSlot.setText(String.valueOf(hour.toDate().getHours() + ":" + hour.toDate().getMinutes()));
            if(HourSlotUtils.profAvailableSlots.contains(hour))
                holder.parentView2.setBackgroundColor(Color.LTGRAY);
            //Make it so that it changes color depends on whether the slot is in the retrieved array or not
        }
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public interface OnItemListener2
    {
        void onItemClick(int position, Timestamp hour);
    }
}


