package com.example.myapplication.Fragments.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class CardEventsAdapter extends RecyclerView.Adapter<CardEventsAdapter.CardViewHolder>{
    RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    ArrayList<CardEvents> cardEventsArrayList;

    public CardEventsAdapter(ArrayList<CardEvents> cardEventsArrayList)
    {
        this.cardEventsArrayList = cardEventsArrayList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.events_day_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardEvents cardEvents = cardEventsArrayList.get(position);
        holder.date.setText(cardEventsArrayList.get(position).getDate());

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.subRvEvents.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(cardEvents.getEventInfoArrayList().size());

        // Create sub item view adapter
        EventAdapter eventAdapter = new EventAdapter(cardEvents.getEventInfoArrayList());

        holder.subRvEvents.setLayoutManager(layoutManager);
        holder.subRvEvents.setAdapter(eventAdapter);
        holder.subRvEvents.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return cardEventsArrayList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {
        public TextView date;
        public RecyclerView subRvEvents;
        public CardViewHolder(@NonNull View itemView)
        {
            super(itemView);
            date = itemView.findViewById(R.id.dateID);
            subRvEvents = itemView.findViewById(R.id.rvEventsID);
        }
    }
}
