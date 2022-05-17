package com.example.myapplication.ProfessorSetAvailableTimeSlots;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.util.ArrayList;

public class HourSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final ArrayList<Timestamp> hours;
    public final View parentView2;
    public final TextView hourOfSlot;
    private final HourSlotAdapter.OnItemListener2 onItemListener2;
    public HourSlotViewHolder(@NonNull View itemView, HourSlotAdapter.OnItemListener2 onItemListener2, ArrayList<Timestamp> hours)
    {
        super(itemView);
        parentView2 = itemView.findViewById(R.id.parentView2);
        hourOfSlot = itemView.findViewById(R.id.cellHourText);
        this.onItemListener2 = onItemListener2;
        itemView.setOnClickListener(this);
        this.hours = hours;
    }

    @Override
    public void onClick(View view) {
        onItemListener2.onItemClick(getAdapterPosition(), hours.get(getAdapterPosition()));
    }
}
